package pl.lodz.p;

public class GuestTypePremium extends GuestType{
    public GuestTypePremium() {
        setMealServiceType("Full");
        setSaunaAvailable(true);
        setUnlimitedWifi(true);
    }

    @Override
    public double applyAdditionalCosts(double price) {
        return 400.0 + (price * 0.4);
    }
}
