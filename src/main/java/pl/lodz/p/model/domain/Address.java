package pl.lodz.p.model.domain;

import java.util.Objects;

public class Address {
    private final String city;
    private final String street;
    private final String number;

    public Address(String city, String street, String number) {
        this.city = city;
        this.street = street;
        this.number = number;
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

    @Override
    public String toString() {
        return "Address: " +
                "city: '" + city + '\'' + '\n' +
                "street: '" + street + '\'' + '\n' +
                "number: '" + number + '\'' + '\n';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(city, address.city) &&
                Objects.equals(street, address.street) &&
                Objects.equals(number, address.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(city, street, number);
    }
}
