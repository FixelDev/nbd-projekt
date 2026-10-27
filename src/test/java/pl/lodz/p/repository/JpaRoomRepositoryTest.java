package pl.lodz.p.repository;

import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.lodz.p.AbstractDatabaseTest;
import pl.lodz.p.adapter.mapper.RoomMapper;
import pl.lodz.p.exception.ConcurrentOperationException;
import pl.lodz.p.exception.EntityNotFoundException;
import pl.lodz.p.exception.RepositoryException;
import pl.lodz.p.model.db.RoomEnt;
import pl.lodz.p.model.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class JpaRoomRepositoryTest extends AbstractDatabaseTest {
    private JpaRoomRepository roomRepository;
    private Room validRoom;
    private Room validRoom2;
    private Room invalidRoom;
    private Room invalidRoom2;

    @BeforeEach
    public void setup() {
        roomRepository = new JpaRoomRepository(emf);

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Session session = em.unwrap(Session.class);

            session.createMutationQuery("DELETE FROM Rent").execute();
            session.createMutationQuery("DELETE FROM Room").execute();
            session.createMutationQuery("DELETE FROM Guest").execute();
            session.createMutationQuery("DELETE FROM Guest_type").execute();

            em.getTransaction().commit();
        }

        validRoom = new Room("12", 3, 200.0);
        validRoom2 = new Room("11", 2, 50.0);
        invalidRoom = new Room("", 1, 200.0);
        invalidRoom2 = new Room("12", 2, 100.0);
    }

    @Test
    public void saveRoomTest() {
        assertDoesNotThrow(() -> roomRepository.saveRoom(RoomMapper.mapRoomFromDomainToDb(validRoom)));
        Room addedRoom = RoomMapper.mapRoomFromDbToDomain(roomRepository.findRoomByNumber("12"));

        assertEquals(validRoom, addedRoom);
    }

    @Test
    public void saveInvalidRoomTest() {
        // Puste pole numeru
        assertThrows(RepositoryException.class, () -> roomRepository.saveRoom(RoomMapper.mapRoomFromDomainToDb(invalidRoom)));
        roomRepository.saveRoom(RoomMapper.mapRoomFromDomainToDb(validRoom));

        // Brak unikalności numeru pokoju
        assertThrows(RepositoryException.class, () -> roomRepository.saveRoom(RoomMapper.mapRoomFromDomainToDb(invalidRoom2)));
    }

    @Test
    public void setRoomArchivedStatusTest() {
        roomRepository.saveRoom(RoomMapper.mapRoomFromDomainToDb(validRoom));

        Room foundRoom = RoomMapper.mapRoomFromDbToDomain(roomRepository.findRoomByNumber("12"));

        assertFalse(foundRoom.isArchived());

        assertDoesNotThrow(() -> roomRepository.setRoomArchivedStatus("12", true));

        foundRoom = RoomMapper.mapRoomFromDbToDomain(roomRepository.findRoomByNumber("12"));

        assertTrue(foundRoom.isArchived());
    }

    @Test
    public void setRoomArchivedStatusRaceTest() {
        roomRepository.saveRoom(RoomMapper.mapRoomFromDomainToDb(validRoom));

        AtomicInteger successCount = new AtomicInteger(0);
        ConcurrentLinkedQueue<Exception> exceptions = new ConcurrentLinkedQueue<>();
        CyclicBarrier barrier = new CyclicBarrier(2);

        Runnable raceTask = () -> {
            try {
                barrier.await();
                roomRepository.setRoomArchivedStatus("12", true);
                successCount.incrementAndGet();
            } catch (Exception e) {
                exceptions.add(e);
            }
        };


        CompletableFuture<Void> task1 = CompletableFuture.runAsync(raceTask);
        CompletableFuture<Void> task2 = CompletableFuture.runAsync(raceTask);

        CompletableFuture.allOf(task1, task2).join();

        assertEquals(1, successCount.get());
        assertEquals(1, exceptions.size());
        assertInstanceOf(ConcurrentOperationException.class, exceptions.peek());
    }

    @Test
    public void findRoomByNumberTest() {
        roomRepository.saveRoom(RoomMapper.mapRoomFromDomainToDb(validRoom));
        Room foundRoom = RoomMapper.mapRoomFromDbToDomain(roomRepository.findRoomByNumber("12"));
        assertNotNull(foundRoom);
        assertEquals(validRoom, foundRoom);
    }

    @Test
    public void findInvalidRoomByNumberTest() {
        roomRepository.saveRoom(RoomMapper.mapRoomFromDomainToDb(validRoom));
        assertThrows(EntityNotFoundException.class, () -> roomRepository.findRoomByNumber("1"));
    }

    @Test
    public void findAllRoomsTest() {
        roomRepository.saveRoom(RoomMapper.mapRoomFromDomainToDb(validRoom));
        roomRepository.saveRoom(RoomMapper.mapRoomFromDomainToDb(validRoom2));

        List<Room> allRooms = new ArrayList<>();

        for (RoomEnt roomEnt : roomRepository.findAllRooms()) {
            allRooms.add(RoomMapper.mapRoomFromDbToDomain(roomEnt));
        }

        assertEquals(2, allRooms.size());
        assertTrue(allRooms.contains(validRoom));
        assertTrue(allRooms.contains(validRoom2));
    }
}