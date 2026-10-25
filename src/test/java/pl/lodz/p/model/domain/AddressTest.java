package pl.lodz.p.model.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AddressTest {

    @Test
    public void constructorTest() {
        final String initialCity = "Radomsko";
        final String initialStreet = "Lesna";
        final String initialNumber = "6B";

        Address address = new Address(initialCity, initialStreet, initialNumber);

        assertEquals(initialCity, address.getCity());
        assertEquals(initialStreet, address.getStreet());
        assertEquals(initialNumber, address.getNumber());
    }
}