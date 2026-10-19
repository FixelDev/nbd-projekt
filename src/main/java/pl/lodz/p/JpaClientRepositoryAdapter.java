package pl.lodz.p;

import pl.lodz.p.model.db.GuestEnt;
import pl.lodz.p.model.domain.Guest;

public class JpaClientRepositoryAdapter implements GuestRepositoryInterface{
    private JpaGuestRepository clientRepository;

    public JpaClientRepositoryAdapter(JpaGuestRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public void saveGuest(Guest guest) {
        GuestEnt guestEnt = new GuestEnt();

        guestEnt.setPersonalId(guest.getPersonalId());
        guestEnt.setFirstName(guest.getFirstName());
        guestEnt.setLastName(guest.getLastName());
        guest.setArchived(guest.isArchived());


    }

    @Override
    public void removeGuest() {

    }
}
