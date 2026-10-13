package pl.lodz.p;

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
}
