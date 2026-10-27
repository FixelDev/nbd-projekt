package pl.lodz.p.adapter;

import pl.lodz.p.adapter.mapper.GuestMapper;
import pl.lodz.p.adapter.mapper.RentMapper;
import pl.lodz.p.model.db.*;
import pl.lodz.p.model.domain.*;
import pl.lodz.p.repository.JpaRentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JpaRentRepositoryAdapter implements JpaRentRepositoryAdapterInterface{
    private final JpaRentRepository rentRepository;

    public JpaRentRepositoryAdapter(JpaRentRepository rentRepository) {
        this.rentRepository = rentRepository;
    }

    @Override
    public Rent saveRent(Rent rent) {
        RentEnt savedRent = rentRepository.saveRent(RentMapper.mapRentFromDomainToDb(rent), rent.getRoom().getNumber(), rent.getGuest().getPersonalId());
        return (RentMapper.mapRentFromDbToDomain(savedRent));
    }

    @Override
    public void endRent(UUID uuid) {
        RentEnt rentEnt = rentRepository.findRentByUuid(uuid);
        rentRepository.endRent(rentEnt);
    }

    @Override
    public Rent findRentByUuid(UUID uuid) {
        return RentMapper.mapRentFromDbToDomain(rentRepository.findRentByUuid(uuid));
    }

    @Override
    public List<Rent> findAllRents() {
        List<Rent> allRents = new ArrayList<>();

        for (RentEnt rentEnt : rentRepository.findAllRents()) {
            allRents.add(RentMapper.mapRentFromDbToDomain(rentEnt));
        }

        return allRents;
    }

    @Override
    public List<Rent> findAllGuestRents(String personalId) {
        List<Rent> guestRents = new ArrayList<>();

        for (RentEnt rentEnt : rentRepository.findGuestRents(personalId)) {
            guestRents.add(RentMapper.mapRentFromDbToDomain(rentEnt));
        }

        return guestRents;
    }

}
