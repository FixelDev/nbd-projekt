package pl.lodz.p.adapter;

import pl.lodz.p.model.domain.Guest;
import pl.lodz.p.model.domain.Rent;

import java.util.List;

public interface JpaGuestRepositoryAdapterInterface {
    Guest saveGuest(Guest guest);
    void archiveGuset(Guest guest);
    Guest findGuestByPersonalId(String personalId);
    List<Guest> findAllGuests(boolean includeArchived);
    List<Rent> getGuestRents(String personalId);
}