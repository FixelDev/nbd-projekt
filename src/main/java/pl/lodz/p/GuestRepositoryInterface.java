package pl.lodz.p;

import pl.lodz.p.model.domain.Guest;

public interface GuestRepositoryInterface {
    void saveGuest(Guest guest);
    void removeGuest();
}
