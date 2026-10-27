package pl.lodz.p.model.domain;

import java.util.Objects;

public class GuestTypeExtra extends GuestType{
    private final int loyaltyPoints = 100;

    public GuestTypeExtra() {
        setMealServiceType("BreakfastOnly");
        setSaunaAvailable(false);
        setUnlimitedWifi(true);
    }

    @Override
    public double applyAdditionalCosts(double price) {
        return 300.0;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    @Override
    public String toString() {
        return "GuestType: EXTRA" + '\n' +
                "loyalty points: " + loyaltyPoints + '\n' +
                super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GuestTypeExtra that = (GuestTypeExtra) o;
        return loyaltyPoints == that.loyaltyPoints;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), loyaltyPoints);
    }
}
