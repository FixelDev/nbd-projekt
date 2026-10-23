package pl.lodz.p.model.domain;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Rent {
    private final LocalDateTime beginTime;
    private final LocalDateTime endTime;
    private final Guest guest;
    private final Room room;
    private double rentCost;

    public Rent(LocalDateTime beginTime, LocalDateTime endTime, Guest guest, Room room) {
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.guest = guest;
        this.room = room;

        calculateRentCost();
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

    @Override
    public String toString() {
        return "Rent: " +
                "beginTime: " + beginTime +
                ", endTime: " + endTime +
                ", guest: " + guest +
                ", room: " + room +
                ", rentCost: " + rentCost;
    }
}
