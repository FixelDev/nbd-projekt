package pl.lodz.p.adapter;

import pl.lodz.p.model.domain.Guest;
import pl.lodz.p.model.domain.Rent;

import java.util.List;
import java.util.UUID;

public interface JpaRentRepositoryAdapterInterface {
    Rent saveRent(Rent rent);
    void endRent(UUID uuid);
    Guest findRentByUuid(UUID uuid);
    List<Rent> findAllRents();
}
