package pl.lodz.p.adapter.mapper;

import pl.lodz.p.model.db.*;
import pl.lodz.p.model.domain.*;

public class GuestMapper {
    public static Guest mapGuestFromDbToDomain(GuestEnt guestEnt) {
        Address address = mapAddressFromDbToDomain(guestEnt.getAddress());
        GuestType guestType = mapGuestTypeFromDbToDomain(guestEnt.getGuestType());


        Guest guest = new Guest(guestEnt.getUuid(),
                guestEnt.getFirstName(),
                guestEnt.getLastName(),
                guestEnt.getPersonalId(),
                address,
                guestType);

        guest.setArchived(guestEnt.isArchived());

        return guest;
    }




    public static  GuestEnt mapGuestFromDomainToDb(Guest guest) {
        GuestEnt guestEnt = new GuestEnt();

        guestEnt.setPersonalId(guest.getPersonalId());
        guestEnt.setFirstName(guest.getFirstName());
        guestEnt.setLastName(guest.getLastName());
        guestEnt.setArchived(guest.isArchived());
        guestEnt.setUuid(guest.getUuid());



        return guestEnt;
    }

    public static  GuestTypeEnt mapGuestTypeFromDomainToDb(GuestType guestTypeDomain) {
        GuestTypeEnt guestTypeDb;

        if (guestTypeDomain instanceof GuestTypeDefault) {
            guestTypeDb = new GuestTypeDefaultEnt();
        } else if (guestTypeDomain instanceof GuestTypeExtra) {
            guestTypeDb = new GuestTypeExtraEnt();
        } else {
            guestTypeDb = new GuestTypePremiumEnt();
        }

        guestTypeDb.setMealServiceType(guestTypeDomain.getMealServiceType());
        guestTypeDb.setSaunaAvailable(guestTypeDomain.isSaunaAvailable());
        guestTypeDb.setUnlimitedWifi(guestTypeDomain.isUnlimitedWifi());

        return guestTypeDb;
    }

    public static  GuestType mapGuestTypeFromDbToDomain(GuestTypeEnt guestTypeEnt) {
        GuestType guestType;

        if (guestTypeEnt instanceof GuestTypeDefaultEnt) {
            guestType = new GuestTypeDefault();
        } else if (guestTypeEnt instanceof GuestTypeExtraEnt) {
            guestType = new GuestTypeExtra();
        } else {
            guestType = new GuestTypePremium();
        }

        return guestType;
    }

    public static  AddressEnt mapAddressFromDomainToDb(Address address) {
        AddressEnt addressEnt = new AddressEnt();

        addressEnt.setCity(address.getCity());
        addressEnt.setStreet(address.getStreet());
        addressEnt.setNumber(address.getNumber());

        return addressEnt;
    }

    public static  Address mapAddressFromDbToDomain(AddressEnt addressEnt) {
        Address address = new Address(addressEnt.getCity(), addressEnt.getStreet(), addressEnt.getNumber());

        return address;
    }
}
