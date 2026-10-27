package pl.lodz.p.manager;

import jakarta.persistence.EntityManagerFactory;
import pl.lodz.p.adapter.JpaGuestRepositoryAdapter;
import pl.lodz.p.exception.EntityNotFoundException;
import pl.lodz.p.exception.InvalidValueException;
import pl.lodz.p.exception.UniqueValueException;
import pl.lodz.p.model.domain.*;
import pl.lodz.p.repository.JpaGuestRepository;

import java.util.List;

public class GuestManager {
    private final JpaGuestRepositoryAdapter adapter;

    public GuestManager(EntityManagerFactory emf) {
        JpaGuestRepository repository = new JpaGuestRepository(emf);
        adapter = new JpaGuestRepositoryAdapter(repository);
    }

    public Guest registerGuest(String firstName, String lastName, String personalId, String city, String street, String number, String guestTypeName) {
        try {
            adapter.findGuestByPersonalId(personalId);
        } catch (EntityNotFoundException _) {
            Address address = new Address(city, street, number);
            GuestType guestType = mapNameToGuestType(guestTypeName);
            Guest guest = new Guest(firstName, lastName, personalId, address, guestType);
            return adapter.saveGuest(guest);
        }

        throw new UniqueValueException("Gość o personalId = " + personalId + " już istnieje");
    }

    public void unregisterGuest(String personalId) {
        adapter.setGuestArchivedStatus(personalId, true);
    }

    public void activateGuest(String personalId) {
        adapter.setGuestArchivedStatus(personalId, false);
    }

    public Guest findGuestByPersonalId(String personalId) {
        return adapter.findGuestByPersonalId(personalId);
    }

    public List<Guest> findAllGuests() {
        return adapter.findAllGuests();
    }



    private GuestType mapNameToGuestType(String guestTypeName) {
        if (guestTypeName.equalsIgnoreCase("default")) {
            return new GuestTypeDefault();
        } else if (guestTypeName.equalsIgnoreCase("extra")) {
            return new GuestTypeExtra();
        } else if (guestTypeName.equalsIgnoreCase("premium")) {
            return new GuestTypePremium();
        }

        throw new InvalidValueException("Typ gościa " + guestTypeName + " nie jest poprawny");
    }
}
