package pl.lodz.p.model.domain;

public abstract class GuestType {
    private boolean unlimitedWifi;
    private boolean saunaAvailable;
    private String mealServiceType;

    public String getMealServiceType() {
        return mealServiceType;
    }

    public boolean isSaunaAvailable() {
        return saunaAvailable;
    }

    public boolean isUnlimitedWifi() {
        return unlimitedWifi;
    }

    protected void setUnlimitedWifi(boolean unlimitedWifi) {
        this.unlimitedWifi = unlimitedWifi;
    }

    protected void setMealServiceType(String mealServiceType) {
        this.mealServiceType = mealServiceType;
    }

    protected void setSaunaAvailable(boolean saunaAvailable) {
        this.saunaAvailable = saunaAvailable;
    }

    public abstract double applyAdditionalCosts(double price);

    @Override
    public String toString() {
        return "GuestType: " +
                "unlimitedWifi: " + unlimitedWifi + '\n' +
                "saunaAvailable: " + saunaAvailable + '\n' +
                "mealServiceType: '" + mealServiceType + '\'' + '\n';
    }
}
