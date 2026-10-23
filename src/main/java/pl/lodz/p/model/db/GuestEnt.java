package pl.lodz.p.model.db;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import pl.lodz.p.model.domain.Address;

@Entity(name = "Guest")
@Table(name = "GUESTS")
public class GuestEnt {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotEmpty
    @Column(name = "FIRST_NAME")
    private String firstName;

    @NotEmpty
    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "PERSONAL_ID", unique = true)
    private String personalId;

    @NotEmpty
    @Column(name = "ARCHIVED")
    private boolean archived;

    @Embedded
    @NotNull
    private Address address;

    @ManyToOne(cascade = CascadeType.MERGE)
    @NotNull
    @JoinColumn(name = "GUEST_TYPE_ID")
    private GuestTypeEnt guestType;

    public Long getId() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getPersonalId() {
        return personalId;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPersonalId(String personalId) {
        this.personalId = personalId;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public void setGuestType(GuestTypeEnt guestType) {
        this.guestType = guestType;
    }

    public void setAddress(Address address) {
        this.address = address;
    }


}
