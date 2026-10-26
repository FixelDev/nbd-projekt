package pl.lodz.p.adapter;

import pl.lodz.p.model.domain.Guest;
import pl.lodz.p.model.domain.Rent;

import java.util.List;

public interface JpaGuestRepositoryAdapterInterface {
    Guest saveGuest(Guest guest);
    void setGuestArchivedStatus(String personalId, boolean isArchived);
    Guest findGuestByPersonalId(String personalId);
    List<Guest> findAllGuests();
    List<Rent> getGuestRents(String personalId);
}