package pl.lodz.p.repository;

import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import pl.lodz.p.AbstractDatabaseTest;
import pl.lodz.p.adapter.mapper.GuestMapper;
import pl.lodz.p.exception.ConcurrentOperationException;
import pl.lodz.p.exception.EntityNotFoundException;
import pl.lodz.p.exception.RepositoryException;
import pl.lodz.p.model.db.GuestEnt;
import pl.lodz.p.model.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class JpaGuestRepositoryTest extends AbstractDatabaseTest {
    private JpaGuestRepository guestRepository;
    private Guest validGuest;
    private Guest validGuest2;
    private Guest invalidGuest;
    private Guest invalidGuest2;

    @BeforeEach
    public void setup() {
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

        Address address = new Address("Warszawa", "Lesna", "6A");
        GuestType guestType = new GuestTypePremium();
        validGuest = new Guest("Jan", "Kowalski", "1234", address, guestType);
        validGuest2 = new Guest("Jakub", "Pawlak", "4567", address, new GuestTypeExtra());
        invalidGuest = new Guest("Jan", "", "1234", address, guestType);
        invalidGuest2 = new Guest("Michał", "Kowal", "1234", address, guestType);
    }

    @Test
    public void saveGuestTest() {
        assertDoesNotThrow(() -> guestRepository.saveGuest(GuestMapper.mapGuestFromDomainToDb(validGuest)));
        Guest addedGuest = GuestMapper.mapGuestFromDbToDomain(guestRepository.findGuestByPersonalId("1234"));

        assertEquals(validGuest, addedGuest);
    }

    @Test
    public void saveInvalidGuestTest() {
        // Puste pole nazwiska
        assertThrows(RepositoryException.class, () -> guestRepository.saveGuest(GuestMapper.mapGuestFromDomainToDb(invalidGuest)));

        guestRepository.saveGuest(GuestMapper.mapGuestFromDomainToDb(validGuest));

        // Brak unikalności personalId
        assertThrows(RepositoryException.class, () -> guestRepository.saveGuest(GuestMapper.mapGuestFromDomainToDb(invalidGuest2)));

    }

    @Test
    public void setGuestArchivedStatusTest() {
        guestRepository.saveGuest(GuestMapper.mapGuestFromDomainToDb(validGuest));
        Guest foundGuest = GuestMapper.mapGuestFromDbToDomain(guestRepository.findGuestByPersonalId("1234"));

        assertFalse(foundGuest.isArchived());

        assertDoesNotThrow(() -> guestRepository.setGuestArchivedStatus(guestRepository.findGuestByPersonalId("1234"), true));
        foundGuest = GuestMapper.mapGuestFromDbToDomain(guestRepository.findGuestByPersonalId("1234"));

        assertTrue(foundGuest.isArchived());
    }


    @Test
    public void setGuestArchivedStatusRaceTest() {
        guestRepository.saveGuest(GuestMapper.mapGuestFromDomainToDb(validGuest));

        AtomicInteger successCount = new AtomicInteger(0);
        ConcurrentLinkedQueue<Exception> exceptions = new ConcurrentLinkedQueue<>();
        CyclicBarrier barrier = new CyclicBarrier(2);

        Runnable raceTask = () -> {
            try {
                GuestEnt guestEnt = guestRepository.findGuestByPersonalId("1234");
                barrier.await();
                guestRepository.setGuestArchivedStatus(guestEnt, true);
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
    public void findGuestByPersonalIdTest() {
        guestRepository.saveGuest(GuestMapper.mapGuestFromDomainToDb(validGuest));
        Guest foundGuest = GuestMapper.mapGuestFromDbToDomain(guestRepository.findGuestByPersonalId("1234"));
        assertNotNull(foundGuest);
        assertEquals(validGuest, foundGuest);
    }

    @Test
    public void findInvalidGuestByPersonalIdTest() {
        guestRepository.saveGuest(GuestMapper.mapGuestFromDomainToDb(validGuest));
        assertThrows(EntityNotFoundException.class, () -> guestRepository.findGuestByPersonalId("1"));
    }

    @Test
    public void findAllGuestsTest() {
        guestRepository.saveGuest(GuestMapper.mapGuestFromDomainToDb(validGuest));
        guestRepository.saveGuest(GuestMapper.mapGuestFromDomainToDb(validGuest2));

        List<Guest> allGuests = new ArrayList<>();

        for (GuestEnt guestEnt : guestRepository.findAllGuests()) {
            allGuests.add(GuestMapper.mapGuestFromDbToDomain(guestEnt));
        }

        assertEquals(2, allGuests.size());
        assertTrue(allGuests.contains(validGuest));
        assertTrue(allGuests.contains(validGuest2));

    }
}