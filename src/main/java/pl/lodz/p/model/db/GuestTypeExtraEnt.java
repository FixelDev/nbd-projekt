package pl.lodz.p.model.db;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("EXTRA")
public class GuestTypeExtraEnt extends GuestTypeEnt{
}
