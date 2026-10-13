package pl.lodz.p;

public class Guest {
    private String firstName;
    private String lastName;
    private String personalId;
    private boolean archived;
    private Address address;
    private GuestType guestType;

    public Guest(String firstName, String lastName, String personalId, boolean archived, Address address, GuestType guestType) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.personalId = personalId;
        this.archived = archived;
        this.address = address;
        this.guestType = guestType;
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

}
