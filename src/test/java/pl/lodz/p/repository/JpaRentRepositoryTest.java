package pl.lodz.p.repository;

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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class JpaRentRepositoryTest extends AbstractDatabaseTest {
    private JpaRentRepository rentRepository;
    private JpaGuestRepository guestRepository;
    private JpaRoomRepository roomRepository;

    private Guest validGuest;
    private Guest validGuest2;
    private Room validRoom;
    private Room validRoom2;
    private Room archivedRoom;
    private Rent validRent;
    private Rent validRent2;
    private Rent validRent3;
    private Rent invalidRentNullBeginTime;
    private Rent invalidRentNotUniqueUuid;
    private Rent invalidRentArchivedRoom;

    @BeforeEach
    public void setup() {
        rentRepository = new JpaRentRepository(emf);
        roomRepository = new JpaRoomRepository(emf);
        guestRepository = new JpaGuestRepository(emf);

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
        archivedRoom = new Room("14", 2, 100.0);
        validRent = new Rent(UUID.fromString("bbcc4621-d88f-4a94-ae2f-b38072bf5087"), dateTimeBegin, dateTimeEnd, validGuest, validRoom, 200.0);
        validRent2 = new Rent(UUID.fromString("bbcc4621-d18f-4a94-ae2f-b38072bf5087"), dateTimeBegin, dateTimeEnd, validGuest, validRoom, 100.0);
        validRent3 = new Rent(UUID.fromString("aacc4621-d18f-4a94-ae2f-b38072bf5087"), dateTimeBegin, dateTimeEnd, validGuest, validRoom2, 100.0);
        invalidRentNullBeginTime = new Rent(UUID.fromString("abcc4621-d88f-4a94-ae2f-b38072bf5087"), null, dateTimeEnd, validGuest, validRoom, 200.0);
        invalidRentNotUniqueUuid = new Rent(UUID.fromString("bbcc4621-d88f-4a94-ae2f-b38072bf5087"), dateTimeBegin, dateTimeEnd, validGuest2, validRoom2, 200.0);
        invalidRentArchivedRoom = new Rent(UUID.fromString("bbcc5521-d88f-4a94-ae2f-b38072bf5087"), dateTimeBegin, dateTimeEnd, validGuest, archivedRoom, 200.0);
    }

    @Test
    public void saveRentTest() {
        guestRepository.saveGuest(GuestMapper.mapGuestFromDomainToDb(validGuest));
        roomRepository.saveRoom(RoomMapper.mapRoomFromDomainToDb(validRoom));

        assertDoesNotThrow(() -> rentRepository.saveRent(RentMapper.mapRentFromDomainToDb(validRent), validRent.getRoom().getNumber(), validRent.getGuest().getPersonalId()));
        Rent addedRent = RentMapper.mapRentFromDbToDomain(rentRepository.findRentByUuid(UUID.fromString("bbcc4621-d88f-4a94-ae2f-b38072bf5087")));

        assertEquals(validRent, addedRent);
        assertTrue(addedRent.getRoom().isRented());
        assertTrue(addedRent.isActive());

        assertTrue(RoomMapper.mapRoomFromDbToDomain(roomRepository.findRoomByNumber("12")).isRented());
        assertTrue(RentMapper.mapRentFromDbToDomain(rentRepository.findRentByUuid(UUID.fromString("bbcc4621-d88f-4a94-ae2f-b38072bf5087"))).isActive());
    }

    @Test
    public void saveInvalidRentTest() {
        guestRepository.saveGuest(GuestMapper.mapGuestFromDomainToDb(validGuest));
        guestRepository.saveGuest(GuestMapper.mapGuestFromDomainToDb(validGuest2));
        roomRepository.saveRoom(RoomMapper.mapRoomFromDomainToDb(validRoom));
        roomRepository.saveRoom(RoomMapper.mapRoomFromDomainToDb(validRoom2));

        // Przekaznie null do beginTime
        assertThrows(RepositoryException.class, () -> rentRepository.saveRent(RentMapper.mapRentFromDomainToDb(invalidRentNullBeginTime), invalidRentNullBeginTime.getRoom().getNumber(), invalidRentNullBeginTime.getGuest().getPersonalId()));

        rentRepository.saveRent(RentMapper.mapRentFromDomainToDb(validRent), validRent.getRoom().getNumber(), validRent.getGuest().getPersonalId());

        // Brak unikalności UUID
        assertThrows(RepositoryException.class, () -> rentRepository.saveRent(RentMapper.mapRentFromDomainToDb(invalidRentNotUniqueUuid), invalidRentNotUniqueUuid.getRoom().getNumber(), invalidRentNotUniqueUuid.getGuest().getPersonalId()));


    }

    @Test
    public void rentInvalidRoomTest() {
        guestRepository.saveGuest(GuestMapper.mapGuestFromDomainToDb(validGuest));
        roomRepository.saveRoom(RoomMapper.mapRoomFromDomainToDb(validRoom));
        roomRepository.saveRoom(RoomMapper.mapRoomFromDomainToDb(archivedRoom));

        roomRepository.setRoomArchivedStatus(roomRepository.findRoomByNumber("14"), true);


        rentRepository.saveRent(RentMapper.mapRentFromDomainToDb(validRent), validRent.getRoom().getNumber(), validRent.getGuest().getPersonalId());

        // Już wypożyczony pokój
        assertThrows(ConcurrentOperationException.class, () -> rentRepository.saveRent(RentMapper.mapRentFromDomainToDb(validRent), validRent.getRoom().getNumber(), validRent.getGuest().getPersonalId()));

        // Zarchiwizowany pokój
        assertThrows(ConcurrentOperationException.class, () -> rentRepository.saveRent(RentMapper.mapRentFromDomainToDb(invalidRentArchivedRoom), invalidRentArchivedRoom.getRoom().getNumber(), invalidRentArchivedRoom.getGuest().getPersonalId()));
    }

    @Test
    public void endRentTest() {
        guestRepository.saveGuest(GuestMapper.mapGuestFromDomainToDb(validGuest));
        roomRepository.saveRoom(RoomMapper.mapRoomFromDomainToDb(validRoom));
        rentRepository.saveRent(RentMapper.mapRentFromDomainToDb(validRent), validRent.getRoom().getNumber(), validRent.getGuest().getPersonalId());

        assertDoesNotThrow(() -> rentRepository.endRent(rentRepository.findRentByUuid(UUID.fromString("bbcc4621-d88f-4a94-ae2f-b38072bf5087"))));
        assertFalse(RoomMapper.mapRoomFromDbToDomain(roomRepository.findRoomByNumber("12")).isRented());
        assertFalse(RentMapper.mapRentFromDbToDomain(rentRepository.findRentByUuid(UUID.fromString("bbcc4621-d88f-4a94-ae2f-b38072bf5087"))).isActive());

    }

    @Test
    public void rentRoomRaceTest() {
        guestRepository.saveGuest(GuestMapper.mapGuestFromDomainToDb(validGuest));
        roomRepository.saveRoom(RoomMapper.mapRoomFromDomainToDb(validRoom));



        AtomicInteger successCount = new AtomicInteger(0);
        ConcurrentLinkedQueue<Exception> exceptions = new ConcurrentLinkedQueue<>();
        CyclicBarrier barrier = new CyclicBarrier(2);

        Runnable raceTask1 = () -> {
            try {
                barrier.await();
                rentRepository.saveRent(RentMapper.mapRentFromDomainToDb(validRent), validRent.getRoom().getNumber(), validRent.getGuest().getPersonalId());
                successCount.incrementAndGet();
            } catch (Exception e) {
                exceptions.add(e);
            }
        };

        Runnable raceTask2 = () -> {
            try {
                barrier.await();
                rentRepository.saveRent(RentMapper.mapRentFromDomainToDb(validRent2), validRent2.getRoom().getNumber(), validRent2.getGuest().getPersonalId());
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
        guestRepository.saveGuest(GuestMapper.mapGuestFromDomainToDb(validGuest));
        roomRepository.saveRoom(RoomMapper.mapRoomFromDomainToDb(validRoom));
        rentRepository.saveRent(RentMapper.mapRentFromDomainToDb(validRent), validRent.getRoom().getNumber(), validRent.getGuest().getPersonalId());

        AtomicInteger successCount = new AtomicInteger(0);
        ConcurrentLinkedQueue<Exception> exceptions = new ConcurrentLinkedQueue<>();
        CyclicBarrier barrier = new CyclicBarrier(2);

        Runnable raceTask = () -> {
            try {
                RentEnt rentEnt = rentRepository.findRentByUuid(UUID.fromString("bbcc4621-d88f-4a94-ae2f-b38072bf5087"));
                barrier.await();
                rentRepository.endRent(rentEnt);
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
        guestRepository.saveGuest(GuestMapper.mapGuestFromDomainToDb(validGuest));
        roomRepository.saveRoom(RoomMapper.mapRoomFromDomainToDb(validRoom));
        rentRepository.saveRent(RentMapper.mapRentFromDomainToDb(validRent), validRent.getRoom().getNumber(), validRent.getGuest().getPersonalId());
        Rent foundRent = RentMapper.mapRentFromDbToDomain(rentRepository.findRentByUuid(UUID.fromString("bbcc4621-d88f-4a94-ae2f-b38072bf5087")));
        assertNotNull(foundRent);
        assertEquals(validRent, foundRent);
    }

    @Test
    public void findInvalidGuestByPersonalIdTest() {
        guestRepository.saveGuest(GuestMapper.mapGuestFromDomainToDb(validGuest));
        roomRepository.saveRoom(RoomMapper.mapRoomFromDomainToDb(validRoom));
        rentRepository.saveRent(RentMapper.mapRentFromDomainToDb(validRent), validRent.getRoom().getNumber(), validRent.getGuest().getPersonalId());

        assertThrows(EntityNotFoundException.class, () -> rentRepository.findRentByUuid(UUID.fromString("bbcc6767-d88f-4a94-ae2f-b38072bf5087")));
    }

    @Test
    public void findAllGuestsTest() {
        guestRepository.saveGuest(GuestMapper.mapGuestFromDomainToDb(validGuest));
        roomRepository.saveRoom(RoomMapper.mapRoomFromDomainToDb(validRoom));
        roomRepository.saveRoom(RoomMapper.mapRoomFromDomainToDb(validRoom2));
        rentRepository.saveRent(RentMapper.mapRentFromDomainToDb(validRent), validRent.getRoom().getNumber(), validRent.getGuest().getPersonalId());
        rentRepository.saveRent(RentMapper.mapRentFromDomainToDb(validRent3), validRent3.getRoom().getNumber(), validRent3.getGuest().getPersonalId());

        List<Rent> allRents = new ArrayList<>();

        for (RentEnt rentEnt : rentRepository.findAllRents()) {
            allRents.add(RentMapper.mapRentFromDbToDomain(rentEnt));
        }

        assertEquals(2, allRents.size());
        assertTrue(allRents.contains(validRent));
        assertTrue(allRents.contains(validRent3));

    }
}