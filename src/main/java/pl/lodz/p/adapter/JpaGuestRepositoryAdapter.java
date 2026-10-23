package pl.lodz.p.adapter;

import pl.lodz.p.model.db.*;
import pl.lodz.p.model.domain.GuestType;
import pl.lodz.p.model.domain.GuestTypeDefault;
import pl.lodz.p.model.domain.GuestTypeExtra;
import pl.lodz.p.repository.GuestRepositoryInterface;
import pl.lodz.p.repository.JpaGuestRepository;
import pl.lodz.p.model.domain.Guest;

public class JpaGuestRepositoryAdapter implements GuestRepositoryInterface {
    private JpaGuestRepository clientRepository;

    public JpaGuestRepositoryAdapter(JpaGuestRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public void saveGuest(Guest guest) {
        GuestEnt guestEnt = new GuestEnt();

        guestEnt.setPersonalId(guest.getPersonalId());
        guestEnt.setFirstName(guest.getFirstName());
        guestEnt.setLastName(guest.getLastName());
        guestEnt.setArchived(guest.isArchived());
        guestEnt.setAddress(guest.getAddress());

        GuestTypeEnt guestTypeDb = mapGuestTypeFromDomainToDb(guest.getGuestType());
        guestEnt.setGuestType(guestTypeDb);

        clientRepository.save(guestEnt);
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

        return guestTypeDb;
    }

}
