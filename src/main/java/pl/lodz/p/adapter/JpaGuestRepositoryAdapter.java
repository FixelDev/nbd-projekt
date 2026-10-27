package pl.lodz.p.adapter;

import pl.lodz.p.adapter.mapper.GuestMapper;
import pl.lodz.p.model.db.*;
import pl.lodz.p.model.domain.*;
import pl.lodz.p.repository.JpaGuestRepository;

import java.util.ArrayList;
import java.util.List;

public class JpaGuestRepositoryAdapter implements JpaGuestRepositoryAdapterInterface {
    private final JpaGuestRepository guestRepository;

    public JpaGuestRepositoryAdapter(JpaGuestRepository guestRepository) {
        this.guestRepository = guestRepository;
    }

    @Override
    public Guest saveGuest(Guest guest) {
        GuestEnt guestEnt = GuestMapper.mapGuestFromDomainToDb(guest);
        GuestEnt savedGuest = guestRepository.saveGuest(guestEnt);
        return GuestMapper.mapGuestFromDbToDomain(savedGuest);
    }

    @Override
    public void setGuestArchivedStatus(String personalId, boolean isArchived) {
        GuestEnt guestEnt = guestRepository.findGuestByPersonalId(personalId);
        guestRepository.setGuestArchivedStatus(guestEnt, isArchived);
    }

    @Override
    public Guest findGuestByPersonalId(String personalId) {
        GuestEnt guestEnt = guestRepository.findGuestByPersonalId(personalId);
        return GuestMapper.mapGuestFromDbToDomain(guestEnt);
    }

    @Override
    public List<Guest> findAllGuests() {
        List<Guest> allGuests = new ArrayList<>();

        for (GuestEnt guestEnt : guestRepository.findAllGuests()) {
            allGuests.add(GuestMapper.mapGuestFromDbToDomain(guestEnt));
        }

        return allGuests;
    }

    @Override
    public List<Rent> getGuestRents(String personalId) {
        return List.of();
    }

}
