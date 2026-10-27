package pl.lodz.p.model.domain;

public class GuestTypeDefault extends GuestType{
    public GuestTypeDefault() {
        setMealServiceType("None");
        setSaunaAvailable(false);
        setUnlimitedWifi(false);
    }

    @Override
    public double applyAdditionalCosts(double price) {
        return 0.0;
    }

    @Override
    public String toString() {
        return "GuestType: DEFAULT" + '\n' +
                super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
