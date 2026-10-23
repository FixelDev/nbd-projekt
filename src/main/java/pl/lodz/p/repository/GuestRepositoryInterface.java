package pl.lodz.p.repository;

import pl.lodz.p.model.domain.Guest;

public interface GuestRepositoryInterface {
    void saveGuest(Guest guest);
}