package pl.lodz.p.adapter;

import pl.lodz.p.exception.RepositoryException;
import pl.lodz.p.model.db.*;
import pl.lodz.p.model.domain.*;
import pl.lodz.p.repository.GuestRepositoryInterface;
import pl.lodz.p.repository.JpaGuestRepository;

import java.util.List;

public class JpaGuestRepositoryAdapter implements GuestRepositoryInterface {
    private JpaGuestRepository clientRepository;

    public JpaGuestRepositoryAdapter(JpaGuestRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public void registerGuest(Guest guest) {
        GuestEnt guestEnt = mapGuestFromDomainToDb(guest);

        AddressEnt addressEnt = mapAddressFromDomainToDb(guest.getAddress());
        guestEnt.setAddress(addressEnt);

        GuestTypeEnt guestTypeDb = mapGuestTypeFromDomainToDb(guest.getGuestType());
        guestEnt.setGuestType(guestTypeDb);

        clientRepository.saveGuest(guestEnt);
    }

    @Override
    public void unregisterGuest(Guest guest) {

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

    private AddressEnt mapAddressFromDomainToDb(Address address) {
        AddressEnt addressEnt = new AddressEnt();

        addressEnt.setCity(address.getCity());
        addressEnt.setStreet(address.getStreet());
        addressEnt.setNumber(address.getNumber());

        return addressEnt;
    }

}
