package pl.lodz.p.adapter;

import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.lodz.p.AbstractDatabaseTest;
import pl.lodz.p.adapter.mapper.GuestMapper;
import pl.lodz.p.adapter.mapper.RentMapper;
import pl.lodz.p.adapter.mapper.RoomMapper;
import pl.lodz.p.exception.ConcurrentOperationException;
import pl.lodz.p.exception.EntityNotFoundException;
import pl.lodz.p.exception.RepositoryException;
import pl.lodz.p.model.db.RentEnt;
import pl.lodz.p.model.domain.*;
import pl.lodz.p.repository.JpaGuestRepository;
import pl.lodz.p.repository.JpaRentRepository;
import pl.lodz.p.repository.JpaRoomRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class JpaRentRepositoryAdapterTest extends AbstractDatabaseTest {
    private JpaGuestRepositoryAdapter guestAdapter;
    private JpaRoomRepositoryAdapter roomAdapter;
    private JpaRentRepositoryAdapter rentAdapter;

    private Guest validGuest;
    private Guest validGuest2;
    private Room validRoom;
    private Room validRoom2;
    private Room validRoom3;
    private Room archivedRoom;
    private Rent validRent;
    private Rent validRent2;
    private Rent validRent3;
    private Rent validRent4;
    private Rent invalidRentNullBeginTime;
    private Rent invalidRentNotUniqueUuid;
    private Rent invalidRentArchivedRoom;

    @BeforeEach
    public void setup() {
        JpaRentRepository rentRepository = new JpaRentRepository(emf);
        JpaRoomRepository roomRepository = new JpaRoomRepository(emf);
        JpaGuestRepository guestRepository = new JpaGuestRepository(emf);

        rentAdapter = new JpaRentRepositoryAdapter(rentRepository);
        roomAdapter = new JpaRoomRepositoryAdapter(roomRepository);
        guestAdapter = new JpaGuestRepositoryAdapter(guestRepository);

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Session session = em.unwrap(Session.class);

            session.createMutationQuery("DELETE FROM Rent").execute();
            session.createMutationQuery("DELETE FROM Room").execute();
            session.createMutationQuery("DELETE FROM Guest").execute();
            session.createMutationQuery("DELETE FROM Guest_type").execute();

            em.getTransaction().commit();
        }

        String strBegin = "1986-04-08 12:30";
        String strEnd = "1986-04-10 12:30";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        LocalDateTime dateTimeBegin = LocalDateTime.parse(strBegin, formatter);
        LocalDateTime dateTimeEnd = LocalDateTime.parse(strEnd, formatter);

        Address address = new Address("Warszawa", "Lesna", "6A");
        GuestType guestType = new GuestTypePremium();
        validGuest = new Guest("Jan", "Kowalski", "1234", address, guestType);
        validGuest2 = new Guest("Jakub", "Pawlak", "456", address, guestType);
        validRoom = new Room("12", 3, 200.0);
        validRoom2 = new Room("13", 2, 100.0);
        validRoom3 = new Room("16", 2, 100.0);
        archivedRoom = new Room("14", 2, 100.0);
        validRent = new Rent(UUID.fromString("bbcc4621-d88f-4a94-ae2f-b38072bf5087"), dateTimeBegin, dateTimeEnd, validGuest, validRoom, 200.0);
        validRent2 = new Rent(UUID.fromString("bbcc4621-d18f-4a94-ae2f-b38072bf5087"), dateTimeBegin, dateTimeEnd, validGuest, validRoom, 100.0);
        validRent3 = new Rent(UUID.fromString("aacc4621-d18f-4a94-ae2f-b38072bf5087"), dateTimeBegin, dateTimeEnd, validGuest, validRoom2, 100.0);
        validRent4 = new Rent(UUID.fromString("ddcc4621-d18f-4a94-ae2f-b38072bf5087"), dateTimeBegin, dateTimeEnd, validGuest2, validRoom3, 100.0);
        invalidRentNullBeginTime = new Rent(UUID.fromString("abcc4621-d88f-4a94-ae2f-b38072bf5087"), null, dateTimeEnd, validGuest, validRoom, 200.0);
        invalidRentNotUniqueUuid = new Rent(UUID.fromString("bbcc4621-d88f-4a94-ae2f-b38072bf5087"), dateTimeBegin, dateTimeEnd, validGuest2, validRoom2, 200.0);
        invalidRentArchivedRoom = new Rent(UUID.fromString("bbcc5521-d88f-4a94-ae2f-b38072bf5087"), dateTimeBegin, dateTimeEnd, validGuest, archivedRoom, 200.0);
    }

    @Test
    public void saveRentTest() {
        guestAdapter.saveGuest(validGuest);
        roomAdapter.saveRoom(validRoom);

        assertDoesNotThrow(() -> rentAdapter.saveRent(validRent));
        Rent addedRent = rentAdapter.findRentByUuid(UUID.fromString("bbcc4621-d88f-4a94-ae2f-b38072bf5087"));

        assertEquals(validRent, addedRent);
        assertTrue(addedRent.getRoom().isRented());
        assertTrue(addedRent.isActive());

        assertTrue(roomAdapter.findRoomByNumber("12").isRented());
        assertTrue(rentAdapter.findRentByUuid(UUID.fromString("bbcc4621-d88f-4a94-ae2f-b38072bf5087")).isActive());
    }

    @Test
    public void saveInvalidRentTest() {
        guestAdapter.saveGuest(validGuest);
        guestAdapter.saveGuest(validGuest2);
        roomAdapter.saveRoom(validRoom);
        roomAdapter.saveRoom(validRoom2);

        // Przekaznie null do beginTime
        assertThrows(RepositoryException.class, () -> rentAdapter.saveRent(invalidRentNullBeginTime));

        rentAdapter.saveRent(validRent);

        // Brak unikalności UUID
        assertThrows(RepositoryException.class, () -> rentAdapter.saveRent(invalidRentNotUniqueUuid));


    }

    @Test
    public void rentInvalidRoomTest() {
        guestAdapter.saveGuest(validGuest);
        roomAdapter.saveRoom(validRoom);
        roomAdapter.saveRoom(archivedRoom);

        roomAdapter.setRoomArchivedStatus("14", true);


        rentAdapter.saveRent(validRent);

        // Już wypożyczony pokój
        assertThrows(ConcurrentOperationException.class, () -> rentAdapter.saveRent(validRent));

        // Zarchiwizowany pokój
        assertThrows(ConcurrentOperationException.class, () -> rentAdapter.saveRent(invalidRentArchivedRoom));
    }

    @Test
    public void endRentTest() {
        guestAdapter.saveGuest(validGuest);
        roomAdapter.saveRoom(validRoom);
        rentAdapter.saveRent(validRent);

        assertDoesNotThrow(() -> rentAdapter.endRent(UUID.fromString("bbcc4621-d88f-4a94-ae2f-b38072bf5087")));
        assertFalse(roomAdapter.findRoomByNumber("12").isRented());
        assertFalse(rentAdapter.findRentByUuid(UUID.fromString("bbcc4621-d88f-4a94-ae2f-b38072bf5087")).isActive());

    }

    @Test
    public void rentRoomRaceTest() {
        guestAdapter.saveGuest(validGuest);
        roomAdapter.saveRoom(validRoom);

        AtomicInteger successCount = new AtomicInteger(0);
        ConcurrentLinkedQueue<Exception> exceptions = new ConcurrentLinkedQueue<>();
        CyclicBarrier barrier = new CyclicBarrier(2);

        Runnable raceTask1 = () -> {
            try {
                barrier.await();
                rentAdapter.saveRent(validRent);
                successCount.incrementAndGet();
            } catch (Exception e) {
                exceptions.add(e);
            }
        };

        Runnable raceTask2 = () -> {
            try {
                barrier.await();
                rentAdapter.saveRent(validRent2);
                successCount.incrementAndGet();
            } catch (Exception e) {
                exceptions.add(e);
            }
        };


        CompletableFuture<Void> task1 = CompletableFuture.runAsync(raceTask1);
        CompletableFuture<Void> task2 = CompletableFuture.runAsync(raceTask2);

        CompletableFuture.allOf(task1, task2).join();

        assertEquals(1, successCount.get());
        assertEquals(1, exceptions.size());
        assertInstanceOf(ConcurrentOperationException.class, exceptions.peek());
    }

    @Test
    public void endRentRaceTest() {
        guestAdapter.saveGuest(validGuest);
        roomAdapter.saveRoom(validRoom);
        rentAdapter.saveRent(validRent);

        AtomicInteger successCount = new AtomicInteger(0);
        ConcurrentLinkedQueue<Exception> exceptions = new ConcurrentLinkedQueue<>();
        CyclicBarrier barrier = new CyclicBarrier(2);

        Runnable raceTask = () -> {
            try {
                barrier.await();
                rentAdapter.endRent(UUID.fromString("bbcc4621-d88f-4a94-ae2f-b38072bf5087"));
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
    public void findRentByUUIDTest() {
        guestAdapter.saveGuest(validGuest);
        roomAdapter.saveRoom(validRoom);
        rentAdapter.saveRent(validRent);
        Rent foundRent = rentAdapter.findRentByUuid(UUID.fromString("bbcc4621-d88f-4a94-ae2f-b38072bf5087"));
        assertNotNull(foundRent);
        assertEquals(validRent, foundRent);
    }

    @Test
    public void findInvalidRentByUUIDTest() {
        guestAdapter.saveGuest(validGuest);
        roomAdapter.saveRoom(validRoom);
        rentAdapter.saveRent(validRent);

        assertThrows(EntityNotFoundException.class, () -> rentAdapter.findRentByUuid(UUID.fromString("bbcc6767-d88f-4a94-ae2f-b38072bf5087")));
    }

    @Test
    public void findAllRentsTest() {
        guestAdapter.saveGuest(validGuest);
        roomAdapter.saveRoom(validRoom);
        roomAdapter.saveRoom(validRoom2);
        rentAdapter.saveRent(validRent);
        rentAdapter.saveRent(validRent3);

        List<Rent> allRents = rentAdapter.findAllRents();

        assertEquals(2, allRents.size());
        assertTrue(allRents.contains(validRent));
        assertTrue(allRents.contains(validRent3));

    }

    @Test
    public void findGuestRentsTest() {
        guestAdapter.saveGuest(validGuest);
        guestAdapter.saveGuest(validGuest2);

        roomAdapter.saveRoom(validRoom);
        roomAdapter.saveRoom(validRoom2);
        roomAdapter.saveRoom(validRoom3);

        rentAdapter.saveRent(validRent);
        rentAdapter.saveRent(validRent3);
        rentAdapter.saveRent(validRent4);

        List<Rent> guest1Rents = rentAdapter.findAllGuestRents(validGuest.getPersonalId());

        List<Rent> guest2Rents = rentAdapter.findAllGuestRents(validGuest2.getPersonalId());

        assertEquals(2, guest1Rents.size());
        assertTrue(guest1Rents.contains(validRent));
        assertTrue(guest1Rents.contains(validRent3));

        assertEquals(1, guest2Rents.size());
        assertTrue(guest2Rents.contains(validRent4));
    }
}