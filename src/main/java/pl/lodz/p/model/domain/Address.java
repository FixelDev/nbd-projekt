package pl.lodz.p.model.domain;

public class Address {
    private final String city;
    private final String street;
    private final String number;
    private final String postalCode;

    public Address(String city, String street, String number, String postalCode) {
        this.city = city;
        this.street = street;
        this.number = number;
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public String getNumber() {
        return number;
    }

    public String getPostalCode() {
        return postalCode;
    }

    @Override
    public String toString() {
        return "Address: " +
                "city: '" + city + '\'' +
                ", street: '" + street + '\'' +
                ", number: '" + number + '\'' +
                ", postalCode: '" + postalCode + '\'';
    }
}
