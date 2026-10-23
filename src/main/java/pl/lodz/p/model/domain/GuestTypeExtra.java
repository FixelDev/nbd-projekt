package pl.lodz.p.model.domain;

public class GuestTypeExtra extends GuestType{
    public GuestTypeExtra() {
        setMealServiceType("BreakfastOnly");
        setSaunaAvailable(false);
        setUnlimitedWifi(true);
    }

    @Override
    public double applyAdditionalCosts(double price) {
        return 300.0;
    }

    @Override
    public String toString() {
        return "GuestType: EXTRA" + '\n' +
                super.toString();
    }
}
