package pl.lodz.p.repository;

import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import pl.lodz.p.adapter.mapper.GuestMapper;
import pl.lodz.p.exception.RepositoryException;
import pl.lodz.p.model.domain.Address;
import pl.lodz.p.model.domain.Guest;
import pl.lodz.p.model.domain.GuestType;
import pl.lodz.p.model.domain.GuestTypePremium;

import static org.junit.jupiter.api.Assertions.*;

class JpaGuestRepositoryTest extends AbstractDatabaseTest {
    private JpaGuestRepository guestRepository;

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
    }

    @Test
    public void saveGuestTest() {
        Address address = new Address("Warszawa", "Lesna", "6A");
        GuestType guestType = new GuestTypePremium();
        Guest guest = new Guest("Jan", "Kowalski", "1234", address, guestType);

        assertDoesNotThrow(() -> guestRepository.saveGuest(GuestMapper.mapGuestFromDomainToDb(guest)));
        Guest addedGuest = GuestMapper.mapGuestFromDbToDomain(guestRepository.findGuestByPersonalId("1234"));

        assertEquals(guest, addedGuest);
    }

    @Test
    public void saveInvalidGuestTest() {
        Address address = new Address("Warszawa", "Lesna", "6A");
        GuestType guestType = new GuestTypePremium();
        Guest guest = new Guest("Jan", "", "1234", address, guestType);

        assertThrows(RepositoryException.class, () -> guestRepository.saveGuest(GuestMapper.mapGuestFromDomainToDb(guest)));

    }


}