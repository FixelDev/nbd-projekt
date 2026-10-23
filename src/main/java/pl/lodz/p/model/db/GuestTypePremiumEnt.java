package pl.lodz.p.model.db;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("PREMIUM")
public class GuestTypePremiumEnt extends GuestTypeEnt{
}
