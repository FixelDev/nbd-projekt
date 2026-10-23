package pl.lodz.p.model.domain;

public class Room {
    private final String number;
    private int bedsAmount;
    private double price;
    private boolean archived;

    public Room(String number, int bedsAmount, double price) {
        this.number = number;
        this.bedsAmount = bedsAmount;
        this.price = price;
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

    @Override
    public String toString() {
        return "Room: " +
                "number: '" + number + '\'' +
                ", bedsAmount: " + bedsAmount +
                ", price: " + price +
                ", archived: " + archived;
    }
}
