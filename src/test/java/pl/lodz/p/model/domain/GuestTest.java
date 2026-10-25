package pl.lodz.p.model.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GuestTest {
    private final String initialFirstName = "Jan";
    private final String initialLastName = "Kowalski";
    private final String initialPersonalID = "1234";
    private final String newFirstName = "Krzysztof";
    private final String newLastName = "Nowak";

    private Address initialAddress;
    private Address newAddress;
    private GuestType initialGuestType;
    private GuestType newGuestType;

    @BeforeEach
    void setUp() {
        initialAddress = new Address("Radomsko", "Ładna", "45");
        newAddress = new Address("Warszawa", "Leśna", "12");
        initialGuestType = new GuestTypeDefault();
        newGuestType = new GuestTypePremium();
    }

    @Test
    void constructorTests() {
        Guest guest = new Guest(initialFirstName, initialLastName, initialPersonalID, initialAddress, initialGuestType);

        assertEquals(initialFirstName, guest.getFirstName());
        assertEquals(initialLastName, guest.getLastName());
        assertEquals(initialPersonalID, guest.getPersonalId());
        assertEquals(initialAddress, guest.getAddress());
        assertFalse(guest.isArchived());
        assertEquals(initialGuestType, guest.getGuestType());
    }

    @Test
    void settersTests() {
        Guest guest = new Guest(initialFirstName, initialLastName, initialPersonalID, initialAddress, initialGuestType);

        guest.setFirstName(newFirstName);
        assertEquals(newFirstName, guest.getFirstName());

        guest.setLastName(newLastName);
        assertEquals(newLastName, guest.getLastName());

        guest.setAddress(newAddress);
        assertEquals(newAddress, guest.getAddress());

//        guest.setArchivedStatus(true);
//        assertTrue(guest.isArchived());

        guest.setGuestType(newGuestType);
        assertEquals(newGuestType, guest.getGuestType());
    }
}