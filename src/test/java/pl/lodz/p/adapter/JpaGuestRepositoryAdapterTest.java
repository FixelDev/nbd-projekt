package pl.lodz.p.adapter;

import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.lodz.p.AbstractDatabaseTest;
import pl.lodz.p.adapter.mapper.GuestMapper;
import pl.lodz.p.exception.ConcurrentOperationException;
import pl.lodz.p.exception.EntityNotFoundException;
import pl.lodz.p.exception.RepositoryException;
import pl.lodz.p.model.db.GuestEnt;
import pl.lodz.p.model.domain.*;
import pl.lodz.p.repository.JpaGuestRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class JpaGuestRepositoryAdapterTest extends AbstractDatabaseTest {
    private JpaGuestRepositoryAdapter adapter;
    private Guest validGuest;
    private Guest validGuest2;
    private Guest invalidGuest;
    private Guest invalidGuest2;

    @BeforeEach
    public void setup() {
        JpaGuestRepository guestRepository = new JpaGuestRepository(emf);
        adapter = new JpaGuestRepositoryAdapter(guestRepository);

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Session session = em.unwrap(Session.class);

            session.createMutationQuery("DELETE FROM Rent").execute();
            session.createMutationQuery("DELETE FROM Room").execute();
            session.createMutationQuery("DELETE FROM Guest").execute();
            session.createMutationQuery("DELETE FROM Guest_type").execute();

            em.getTransaction().commit();
        }

        Address address = new Address("Warszawa", "Lesna", "6A");
        GuestType guestType = new GuestTypePremium();
        validGuest = new Guest("Jan", "Kowalski", "1234", address, guestType);
        validGuest2 = new Guest("Jakub", "Pawlak", "4567", address, new GuestTypeExtra());
        invalidGuest = new Guest("Jan", "", "1234", address, guestType);
        invalidGuest2 = new Guest("Michał", "Kowal", "1234", address, guestType);
    }

    @Test
    public void saveGuestTest() {
        assertDoesNotThrow(() -> adapter.saveGuest(validGuest));
        Guest addedGuest = adapter.findGuestByPersonalId("1234");

        assertEquals(validGuest, addedGuest);
    }

    @Test
    public void saveInvalidGuestTest() {
        // Puste pole nazwiska
        assertThrows(RepositoryException.class, () -> adapter.saveGuest(invalidGuest));

        adapter.saveGuest(validGuest);

        // Brak unikalności personalId
        assertThrows(RepositoryException.class, () -> adapter.saveGuest(invalidGuest2));

    }

    @Test
    public void setGuestArchivedStatusTest() {
        adapter.saveGuest(validGuest);
        Guest foundGuest = adapter.findGuestByPersonalId("1234");

        assertFalse(foundGuest.isArchived());

        assertDoesNotThrow(() -> adapter.setGuestArchivedStatus("1234", true));
        foundGuest =adapter.findGuestByPersonalId("1234");

        assertTrue(foundGuest.isArchived());
    }

//    @Test
//    public void setGuestArchivedStatusRaceTest() {
//        adapter.saveGuest(validGuest);
//
//        AtomicInteger successCount = new AtomicInteger(0);
//        ConcurrentLinkedQueue<Exception> exceptions = new ConcurrentLinkedQueue<>();
//        CyclicBarrier barrier = new CyclicBarrier(2);
//
//        Runnable raceTask = () -> {
//            try {
//                barrier.await();
//                adapter.setGuestArchivedStatus("1234", true);
//                successCount.incrementAndGet();
//            } catch (Exception e) {
//                exceptions.add(e);
//            }
//        };
//
//
//        CompletableFuture<Void> task1 = CompletableFuture.runAsync(raceTask);
//        CompletableFuture<Void> task2 = CompletableFuture.runAsync(raceTask);
//
//        CompletableFuture.allOf(task1, task2).join();
//
//        assertEquals(1, successCount.get());
//        assertEquals(1, exceptions.size());
//        assertInstanceOf(ConcurrentOperationException.class, exceptions.peek());
//    }

    @Test
    public void findGuestByPersonalIdTest() {
        adapter.saveGuest(validGuest);

        Guest foundGuest = adapter.findGuestByPersonalId("1234");
        assertNotNull(foundGuest);
        assertEquals(validGuest, foundGuest);
    }

    @Test
    public void findInvalidGuestByPersonalIdTest() {
        adapter.saveGuest(validGuest);
        assertThrows(EntityNotFoundException.class, () -> adapter.findGuestByPersonalId("1"));
    }

    @Test
    public void findAllGuestsTest() {
        adapter.saveGuest(validGuest);
        adapter.saveGuest(validGuest2);

        List<Guest> allGuests = adapter.findAllGuests();

        assertEquals(2, allGuests.size());
        assertTrue(allGuests.contains(validGuest));
        assertTrue(allGuests.contains(validGuest2));

    }

}