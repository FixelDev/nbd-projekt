package pl.lodz.p.manager;

import jakarta.persistence.EntityManagerFactory;
import pl.lodz.p.adapter.JpaGuestRepositoryAdapter;
import pl.lodz.p.adapter.JpaRentRepositoryAdapter;
import pl.lodz.p.adapter.mapper.RentMapper;
import pl.lodz.p.exception.BusinessLogicException;
import pl.lodz.p.exception.InvalidValueException;
import pl.lodz.p.model.db.RentEnt;
import pl.lodz.p.model.domain.Guest;
import pl.lodz.p.model.domain.Rent;
import pl.lodz.p.model.domain.Room;
import pl.lodz.p.repository.JpaGuestRepository;
import pl.lodz.p.repository.JpaRentRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RentManager {
    private final JpaRentRepositoryAdapter adapter;

    public RentManager(EntityManagerFactory emf) {
        JpaRentRepository repository = new JpaRentRepository(emf);
        adapter = new JpaRentRepositoryAdapter(repository);
    }

    public Rent rentRoom(LocalDateTime beginTime, LocalDateTime endTime, Guest guest, Room room) {
        if (endTime.isBefore(beginTime)) {
            throw new InvalidValueException("Data zakończenia: " + endTime + " nie może być wcześniej niż data rozpoczęcia: " + beginTime);
        }

        if (guest.isArchived()) {
            throw new BusinessLogicException("Gość o personalId = " + guest.getPersonalId() + " jest zarchiwizowany");
        }

        if (room.isRented() || room.isArchived()) {
            throw new BusinessLogicException("Pokój o numerze = " + room.getNumber() + " jest już wypożyczony lub zarchiwizowany");
        }

        Rent rent = new Rent(beginTime, endTime, guest, room);

        return adapter.saveRent(rent);
    }

    public void endRent(UUID uuid) {
        adapter.endRent(uuid);
    }

    public Rent findRentByUuid(UUID uuid) {
        return adapter.findRentByUuid(uuid);
    }

    public List<Rent> findAllRents() {
        return adapter.findAllRents();
    }

    public List<Rent> findAllGuestRents(String personalId) {
        return adapter.findAllGuestRents(personalId);
    }
}
