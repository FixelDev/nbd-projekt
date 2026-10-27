package pl.lodz.p.model.domain;

import java.util.Objects;
import java.util.UUID;

public class Room {
    private final String number;
    private int bedsAmount;
    private double price;
    private boolean archived;
    private boolean rented;
    private UUID uuid;

    public Room(String number, int bedsAmount, double price) {
        this.number = number;
        this.bedsAmount = bedsAmount;
        this.price = price;
        this.uuid = UUID.randomUUID();
    }

    public Room(UUID uuid, String number, int bedsAmount, double price) {
        this.number = number;
        this.bedsAmount = bedsAmount;
        this.price = price;
        this.uuid = uuid;
    }

    public void setBasePrice(double price) {
        this.price = price;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public String getNumber() {
        return number;
    }

    public int getBedsAmount() {
        return bedsAmount;
    }

    public void setBedsAmount(int bedsAmount) { this.bedsAmount = bedsAmount; }

    public double getBasePrice() {
        return price;
    }

    public boolean isArchived() {
        return archived;
    }

    public boolean isRented() {
        return rented;
    }

    public void setRented(boolean rented) {
        this.rented = rented;
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public String toString() {
        return "Room: " +
                "UUID: '" + uuid + '\'' + '\n' +
                "number: '" + number + '\'' + '\n' +
                "bedsAmount: " + bedsAmount + '\n' +
                "price: " + price + '\n' +
                "archived: " + archived + '\n' +
                "rented: " + rented + '\n';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return bedsAmount == room.bedsAmount &&
                Double.compare(room.price, price) == 0 &&
                archived == room.archived &&
                Objects.equals(number, room.number) &&
                Objects.equals(uuid, room.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, bedsAmount, price, archived, rented, uuid);
    }
}
