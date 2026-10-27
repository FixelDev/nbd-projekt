package pl.lodz.p.model.db;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
@DiscriminatorValue("EXTRA")
public class GuestTypeExtraEnt extends GuestTypeEnt {

    @Column(name = "LOYALTY_POINTS")
    private int loyaltyPoints;

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }
}
