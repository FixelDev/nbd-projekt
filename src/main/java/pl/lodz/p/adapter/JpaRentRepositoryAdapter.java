package pl.lodz.p.adapter;

import pl.lodz.p.adapter.mapper.RentMapper;
import pl.lodz.p.model.db.*;
import pl.lodz.p.model.domain.*;
import pl.lodz.p.repository.JpaRentRepository;

import java.util.List;
import java.util.UUID;

public class JpaRentRepositoryAdapter implements JpaRentRepositoryAdapterInterface{
    private JpaRentRepository rentRepository;

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

    }

    @Override
    public Guest findRentByUuid(UUID uuid) {
        return null;
    }

    @Override
    public List<Rent> findAllRents() {
        return List.of();
    }

}
