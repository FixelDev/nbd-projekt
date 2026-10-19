package pl.lodz.p.model.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity(name = "Guest")
@Table(name = "GUESTS")
public class GuestEnt {
    @Id
    private Long id;
    @Column(name = "FIRST_NAME")
    private String firstName;
    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "PERSONAL_ID")
    private String personalId;
    @Column(name = "ARCHIVED")
    private boolean archived;
//    private Address address;
//    private GuestType guestType;

    public void setId(Long id) {
        this.id = id;
    }

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
}
