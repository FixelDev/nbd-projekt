package pl.lodz.p.adapter;

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
import pl.lodz.p.model.domain.Room;
import pl.lodz.p.repository.JpaRoomRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class JpaRoomRepositoryAdapterTest extends AbstractDatabaseTest {
    private JpaRoomRepositoryAdapter adapter;
    private Room validRoom;
    private Room validRoom2;
    private Room invalidRoom;
    private Room invalidRoom2;

    @BeforeEach
    public void setup() {
        JpaRoomRepository roomRepository = new JpaRoomRepository(emf);
        adapter = new JpaRoomRepositoryAdapter(roomRepository);

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
        assertDoesNotThrow(() -> adapter.saveRoom(validRoom));
        Room addedRoom = adapter.findRoomByNumber("12");

        assertEquals(validRoom, addedRoom);
    }

    @Test
    public void saveInvalidRoomTest() {
        // Puste pole numeru
        assertThrows(RepositoryException.class, () -> adapter.saveRoom(invalidRoom));
        adapter.saveRoom(validRoom);

        // Brak unikalności numeru pokoju
        assertThrows(RepositoryException.class, () -> adapter.saveRoom(invalidRoom2));
    }

    @Test
    public void setRoomArchivedStatusTest() {
        adapter.saveRoom(validRoom);

        Room foundRoom = adapter.findRoomByNumber("12");

        assertFalse(foundRoom.isArchived());

        assertDoesNotThrow(() -> adapter.setRoomArchivedStatus("12", true));

        foundRoom = adapter.findRoomByNumber("12");

        assertTrue(foundRoom.isArchived());
    }

    @Test
    public void setRoomArchivedStatusRaceTest() {
        adapter.saveRoom(validRoom);

        AtomicInteger successCount = new AtomicInteger(0);
        ConcurrentLinkedQueue<Exception> exceptions = new ConcurrentLinkedQueue<>();
        CyclicBarrier barrier = new CyclicBarrier(2);

        Runnable raceTask = () -> {
            try {
                barrier.await();
                adapter.setRoomArchivedStatus("12", true);
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
        adapter.saveRoom(validRoom);
        Room foundRoom = adapter.findRoomByNumber("12");
        assertNotNull(foundRoom);
        assertEquals(validRoom, foundRoom);
    }

    @Test
    public void findInvalidRoomByNumberTest() {
        adapter.saveRoom(validRoom);
        assertThrows(EntityNotFoundException.class, () -> adapter.findRoomByNumber("1"));
    }

    @Test
    public void findAllRoomsTest() {
        adapter.saveRoom(validRoom);
        adapter.saveRoom(validRoom2);


        List<Room> allRooms = adapter.findAllRooms();
        assertEquals(2, allRooms.size());
        assertTrue(allRooms.contains(validRoom));
        assertTrue(allRooms.contains(validRoom2));
    }
}