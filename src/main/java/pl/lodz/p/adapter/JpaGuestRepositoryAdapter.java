package pl.lodz.p.adapter;

import pl.lodz.p.model.db.*;
import pl.lodz.p.model.domain.*;
import pl.lodz.p.repository.JpaGuestRepository;

import java.util.List;

public class JpaGuestRepositoryAdapter implements JpaGuestRepositoryAdapterInterface {
    private JpaGuestRepository clientRepository;

    public JpaGuestRepositoryAdapter(JpaGuestRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public Guest saveGuest(Guest guest) {
        GuestEnt guestEnt = mapGuestFromDomainToDb(guest);

        AddressEnt addressEnt = mapAddressFromDomainToDb(guest.getAddress());
        guestEnt.setAddress(addressEnt);

        GuestTypeEnt guestTypeDb = mapGuestTypeFromDomainToDb(guest.getGuestType());
        guestEnt.setGuestType(guestTypeDb);

        GuestEnt savedGuest = clientRepository.saveGuest(guestEnt);
        return mapGuestFromDbToDomain(savedGuest
        );
    }

    @Override
    public void archiveGuset(Guest guest) {

    }

    @Override
    public Guest findGuestByPersonalId(String personalId) {
        return null;
    }

    @Override
    public List<Guest> findAllGuests(boolean includeArchived) {
        return List.of();
    }

    @Override
    public List<Rent> getGuestRents(String personalId) {
        return List.of();
    }

    private Guest mapGuestFromDbToDomain(GuestEnt guestEnt) {
        Address address = mapAddressFromDbToDomain(guestEnt.getAddress());
        GuestType guestType = mapGuestTypeFromDbToDomain(guestEnt.getGuestType());


        Guest guest = new Guest(guestEnt.getUuid(),
                                guestEnt.getFirstName(),
                                guestEnt.getLastName(),
                                guestEnt.getPersonalId(),
                                address,
                                guestType);

        return guest;
    }


    private GuestEnt mapGuestFromDomainToDb(Guest guest) {
        GuestEnt guestEnt = new GuestEnt();

        guestEnt.setPersonalId(guest.getPersonalId());
        guestEnt.setFirstName(guest.getFirstName());
        guestEnt.setLastName(guest.getLastName());
        guestEnt.setArchived(guest.isArchived());
        guestEnt.setUuid(guest.getUuid());

        return guestEnt;
    }

    private GuestTypeEnt mapGuestTypeFromDomainToDb(GuestType guestTypeDomain) {
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

    private GuestType mapGuestTypeFromDbToDomain(GuestTypeEnt guestTypeEnt) {
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

    private AddressEnt mapAddressFromDomainToDb(Address address) {
        AddressEnt addressEnt = new AddressEnt();

        addressEnt.setCity(address.getCity());
        addressEnt.setStreet(address.getStreet());
        addressEnt.setNumber(address.getNumber());

        return addressEnt;
    }

    private Address mapAddressFromDbToDomain(AddressEnt addressEnt) {
        Address address = new Address(addressEnt.getCity(), addressEnt.getStreet(), addressEnt.getNumber());

        return address;
    }

}
