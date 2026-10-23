package pl.lodz.p.model.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;

@Entity
@Table(name = "ROOMS")
public class RoomEnt {

    @Id
    private Long id;

    @NotEmpty
    @Column(name = "NUMBER", unique = true)
    private String number;

    @NotEmpty
    @Column(name = "BEDS")
    private int bedsAmount;

    @NotEmpty
    @Column(name = "PRICE")
    private double price;

    @NotEmpty
    @Column(name = "ARCHIVED")
    private boolean archived;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getBedsAmount() {
        return bedsAmount;
    }

    public void setBedsAmount(int bedsAmount) {
        this.bedsAmount = bedsAmount;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }
}
