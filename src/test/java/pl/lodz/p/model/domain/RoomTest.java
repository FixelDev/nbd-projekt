package pl.lodz.p.model.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoomTest {
    private final String initialNumber = "60A";
    private final int initialBedsAmount = 2;
    private final double initialBasePrice = 500.0;

    private final double newBasePrice = 700.0;


    @Test
    void constructorTests() {
        Room room = new Room(initialNumber, initialBedsAmount, initialBasePrice);

        assertEquals(initialNumber, room.getNumber());
        assertEquals(initialBedsAmount, room.getBedsAmount());
        assertEquals(initialBasePrice, room.getBasePrice());
        assertFalse(room.isArchived());
    }

    @Test
    void settersTests() {
        Room room = new Room(initialNumber, initialBedsAmount, initialBasePrice);

        room.setArchived(true);
        assertTrue(room.isArchived());

        room.setBasePrice(newBasePrice);
        assertEquals(newBasePrice, room.getBasePrice());
    }
}