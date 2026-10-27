package pl.lodz.p.model.domain;

import java.util.Objects;

public class GuestTypePremium extends GuestType{
    private final double attractionDiscount = 0.2;

    public GuestTypePremium() {
        setMealServiceType("Full");
        setSaunaAvailable(true);
        setUnlimitedWifi(true);
    }

    public double getAttractionDiscount() {
        return attractionDiscount;
    }

    @Override
    public double applyAdditionalCosts(double price) {
        return 400.0 + (price * 0.4);
    }

    @Override
    public String toString() {
        return "GuestType: PREMIUM" + '\n' +
                "attraction discount: " + attractionDiscount * 100  + "%'\n'" +
                super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GuestTypePremium that = (GuestTypePremium) o;
        return Double.compare(that.attractionDiscount, attractionDiscount) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), attractionDiscount);
    }
}
