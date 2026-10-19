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

        guestEnt.setId(0L);
        guestEnt.setPersonalId(guest.getPersonalId());
        guestEnt.setFirstName(guest.getFirstName());
        guestEnt.setLastName(guest.getLastName());
        guest.setArchived(guest.isArchived());

        clientRepository.save(guestEnt);
    }

    @Override
    public void removeGuest() {

    }
}
