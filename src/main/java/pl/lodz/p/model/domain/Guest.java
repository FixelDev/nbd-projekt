package pl.lodz.p.model.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Guest {
    private final UUID uuid;
    private String firstName;
    private String lastName;
    private final String personalId;
    private boolean archived;
    private Address address;
    private GuestType guestType;

    public Guest(String firstName, String lastName, String personalId, Address address, GuestType guestType) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.personalId = personalId;
        this.address = address;
        this.guestType = guestType;
        this.uuid = UUID.randomUUID();
    }

    public Guest(UUID uuid, String firstName, String lastName, String personalId, Address address, GuestType guestType) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.personalId = personalId;
        this.address = address;
        this.guestType = guestType;
        this.uuid = uuid;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Address getAddress() {
        return address;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPersonalId() {
        return personalId;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setGuestType(GuestType guestType) {
        this.guestType = guestType;
    }

    public GuestType getGuestType() {
        return guestType;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public double getAdditionalCosts(double price) {
        return guestType.applyAdditionalCosts(price);
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public String toString() {
        return "Guest: " +
                "UUID: '" + uuid + '\'' + '\n' +
                "firstName: '" + firstName + '\'' + '\n' +
                "lastName: '" + lastName + '\'' + '\n' +
                "personalId: '" + personalId + '\'' + '\n' +
                "archived: " + archived + '\n' +
                address + '\n' +
                guestType + '\n';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Guest guest = (Guest) o;
        return archived == guest.archived &&
                Objects.equals(uuid, guest.uuid) &&
                Objects.equals(firstName, guest.firstName) &&
                Objects.equals(lastName, guest.lastName) &&
                Objects.equals(personalId, guest.personalId) &&
                Objects.equals(address, guest.address) &&
                Objects.equals(guestType, guest.guestType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, firstName, lastName, personalId, archived, address, guestType);
    }
}
