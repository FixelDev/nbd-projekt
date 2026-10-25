package pl.lodz.p.repository;

import pl.lodz.p.model.domain.Guest;
import pl.lodz.p.model.domain.Rent;

import java.util.List;

public interface GuestRepositoryInterface {
    void registerGuest(Guest guest);
    void unregisterGuest(Guest guest);
    Guest findGuestByPersonalId(String personalId);
    List<Guest> findAllGuests(boolean includeArchived);
    List<Rent> getGuestRents(String personalId);
}