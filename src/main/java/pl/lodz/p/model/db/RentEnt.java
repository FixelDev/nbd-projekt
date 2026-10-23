package pl.lodz.p.model.db;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Table(name = "RENTS")
@Entity
public class RentEnt extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    @Column(name = "BEGIN_TIME")
    private LocalDateTime beginTime;

    @NotNull
    @Column(name = "END_TIME")
    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "GUEST_ID")
    private GuestEnt guest;

    @ManyToOne
    @JoinColumn(name = "ROOM_ID")
    private RoomEnt room;

    @Column(name = "RENT_COST")
    private double rentCost;

    @Column(name = "ACTIVE")
    private boolean active;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(LocalDateTime beginTime) {
        this.beginTime = beginTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public GuestEnt getGuest() {
        return guest;
    }

    public void setGuest(GuestEnt guest) {
        this.guest = guest;
    }

    public RoomEnt getRoom() {
        return room;
    }

    public void setRoom(RoomEnt room) {
        this.room = room;
    }

    public double getRentCost() {
        return rentCost;
    }

    public void setRentCost(double rentCost) {
        this.rentCost = rentCost;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
