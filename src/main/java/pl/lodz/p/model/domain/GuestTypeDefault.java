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
}
