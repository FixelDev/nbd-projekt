package pl.lodz.p.model.domain;

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

    @Override
    public String toString() {
        return "GuestType: PREMIUM" + '\n' +
                super.toString();
    }
}
