package pl.lodz.p.model.db;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;

@Entity
@DiscriminatorValue("PREMIUM")
public class GuestTypePremiumEnt extends GuestTypeEnt {

    @Column(name = "ATTRACTION_DISCOUNT")
    private double attractionDiscount;

    public double getAttractionDiscount() {
        return attractionDiscount;
    }

    public void setAttractionDiscount(double attractionDiscount) {
        this.attractionDiscount = attractionDiscount;
    }
}
