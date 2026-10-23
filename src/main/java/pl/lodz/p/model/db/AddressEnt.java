package pl.lodz.p.model.db;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotEmpty;

@Embeddable
@Access(AccessType.FIELD)
public class AddressEnt {

    @NotEmpty
    @Column(name = "CITY")
    private String city;

    @NotEmpty
    @Column(name = "STREET")
    private String street;

    @NotEmpty
    @Column(name = "NUMBER")
    private String number;

    @NotEmpty
    @Column(name = "POSTAL_CODE")
    private String postalCode;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}
