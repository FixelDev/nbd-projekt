package pl.lodz.p.model.db;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("DEFAULT")
public class GuestTypeDefaultEnt extends GuestTypeEnt {
}
