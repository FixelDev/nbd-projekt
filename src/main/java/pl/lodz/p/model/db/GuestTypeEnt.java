package pl.lodz.p.model.db;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity(name = "Guest_type")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE_NAME", discriminatorType = DiscriminatorType.STRING)
@Table(name = "GUEST_TYPES")
public class GuestTypeEnt {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    @Column(name = "UNLIMITED_WIFI")
    private boolean unlimitedWifi;

    @NotNull
    @Column(name = "SAUNA_AVAILABLE")
    private boolean saunaAvailable;

    @NotNull
    @Column(name = "MEAL_SERVICE_TYPE")
    private String mealServiceType;

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isUnlimitedWifi() {
        return unlimitedWifi;
    }

    public void setUnlimitedWifi(boolean unlimitedWifi) {
        this.unlimitedWifi = unlimitedWifi;
    }

    public boolean isSaunaAvailable() {
        return saunaAvailable;
    }

    public void setSaunaAvailable(boolean saunaAvailable) {
        this.saunaAvailable = saunaAvailable;
    }

    public String getMealServiceType() {
        return mealServiceType;
    }

    public void setMealServiceType(String mealServiceType) {
        this.mealServiceType = mealServiceType;
    }

    public Long getId() {
        return id;
    }
}
