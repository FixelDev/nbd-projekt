package pl.lodz.p.model.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GuestTypeTest {
    private GuestType defaultGuestType;
    private GuestType extraGuestType;
    private GuestType premiumGuestType;

    @BeforeEach
    void setUp() {
        defaultGuestType = new GuestTypeDefault();
        extraGuestType = new GuestTypeExtra();
        premiumGuestType = new GuestTypePremium();
    }

    @Test
    void defaultConstructorTests() {
        assertFalse(defaultGuestType.isSaunaAvailable());
        assertFalse(defaultGuestType.isUnlimitedWifi());
        assertEquals("None", defaultGuestType.getMealServiceType());
    }

    @Test
    void extraConstructorTests() {
        assertFalse(extraGuestType.isSaunaAvailable());
        assertTrue(extraGuestType.isUnlimitedWifi());
        assertEquals("BreakfastOnly", extraGuestType.getMealServiceType());
    }

    @Test
    void premiumConstructorTests() {
        assertTrue(premiumGuestType.isSaunaAvailable());
        assertTrue(premiumGuestType.isUnlimitedWifi());
        assertEquals("Full", premiumGuestType.getMealServiceType());
    }

    @Test
    void applyingAdditionalCostsTests() {
        final int basePrice = 100;

        assertEquals(0.0, defaultGuestType.applyAdditionalCosts(basePrice));
        assertEquals(300.0, extraGuestType.applyAdditionalCosts(basePrice));
        assertEquals(440.0, premiumGuestType.applyAdditionalCosts(basePrice));
    }

}