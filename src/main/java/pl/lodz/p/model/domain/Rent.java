package pl.lodz.p.model.domain;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class Rent {
    private final LocalDateTime beginTime;
    private final LocalDateTime endTime;
    private final Guest guest;
    private final Room room;
    private double rentCost;
    private boolean active = true;
    private UUID uuid;

    public Rent(LocalDateTime beginTime, LocalDateTime endTime, Guest guest, Room room) {
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.guest = guest;
        this.room = room;
        this.uuid = UUID.randomUUID();

        calculateRentCost();
    }

    public Rent(UUID uuid, LocalDateTime beginTime, LocalDateTime endTime, Guest guest, Room room, double rentCost) {
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.guest = guest;
        this.room = room;
        this.uuid = uuid;
        this.rentCost = rentCost;

    }

    private void calculateRentCost() {
        long days = ChronoUnit.DAYS.between(beginTime, endTime);

        if (days > 0) {
            double rentPriceWithoutAdditionalCosts = room.getBasePrice();
            rentCost = rentPriceWithoutAdditionalCosts + guest.getAdditionalCosts(rentPriceWithoutAdditionalCosts);
        } else {
            rentCost = 0.0;
        }
    }

    public LocalDateTime getBeginTime() {
        return beginTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public Guest getGuest() {
        return guest;
    }

    public Room getRoom() {
        return room;
    }

    public double getRentCost() {
        return rentCost;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public String toString() {
        return "Rent: " +
                "beginTime: " + beginTime + '\n' +
                "endTime: " + endTime + '\n' +
                "guest: " + guest + '\n' +
                "room: " + room + '\n' +
                "rentCost: " + rentCost + '\n' +
                "isActive: " + active + '\n';
    }
}
