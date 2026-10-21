package pl.lodz.p.model.domain;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Rent {
    private int id;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private Guest guest;
    private Room room;
    private double rentCost;

    public Rent(int id, LocalDateTime beginTime, LocalDateTime endTime, Guest guest, Room room) {
        this.id = id;
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

    public int getId() {
        return id;
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
}
