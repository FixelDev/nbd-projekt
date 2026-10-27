package pl.lodz.p.model.domain;

import java.util.Objects;

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
        return  "unlimitedWifi: " + unlimitedWifi + '\n' +
                "saunaAvailable: " + saunaAvailable + '\n' +
                "mealServiceType: '" + mealServiceType + '\'' + '\n';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GuestType guestType = (GuestType) o;
        return unlimitedWifi == guestType.unlimitedWifi &&
                saunaAvailable == guestType.saunaAvailable &&
                Objects.equals(mealServiceType, guestType.mealServiceType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(unlimitedWifi, saunaAvailable, mealServiceType);
    }
}
