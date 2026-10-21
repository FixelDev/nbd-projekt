package pl.lodz.p.model.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RentTest {
    private final int initialId = 0;
    private Guest initialGuest;
    private Address address;
    private GuestType defaultGuestType;
    private Room initialRoom;
    private LocalDateTime initialBeginTime;
    private LocalDateTime initialEndTime;

    @BeforeEach
    void setUp() {
        address = new Address("Warszawa", "Ładna", "6a", "12-000");
        defaultGuestType = new GuestTypeDefault();

        initialGuest = new Guest("Jan", "Nowak", "213", false, address, defaultGuestType);
        initialRoom = new Room("12", 2, 100.0);

        initialBeginTime = LocalDateTime.of(2015, 5, 13, 9, 25);
        initialEndTime = LocalDateTime.of(2015, 5, 16, 19, 0);
    }

    @Test
    void constructorTests() {
        Rent rent = new Rent(initialId, initialBeginTime, initialEndTime, initialGuest, initialRoom);

        assertEquals(initialId, rent.getId());
        assertEquals(initialGuest, rent.getGuest());
        assertEquals(initialRoom, rent.getRoom());
        assertEquals(initialBeginTime, rent.getBeginTime());
        assertEquals(initialEndTime, rent.getEndTime());
    }

    @Test
    void getRentCostTests() {
        Rent rentZeroDays = new Rent(initialId, initialBeginTime, initialBeginTime, initialGuest, initialRoom);
        assertEquals(0.0, rentZeroDays.getRentCost());

        Rent rentMultipleDays = new Rent(initialId, initialBeginTime, initialEndTime, initialGuest, initialRoom);

        assertEquals(100.0, rentMultipleDays.getRentCost());
    }
}