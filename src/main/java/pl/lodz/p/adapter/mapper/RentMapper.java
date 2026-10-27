package pl.lodz.p.adapter.mapper;

import pl.lodz.p.model.db.RentEnt;
import pl.lodz.p.model.domain.Guest;
import pl.lodz.p.model.domain.Rent;
import pl.lodz.p.model.domain.Room;

public class RentMapper {

    public static Rent mapRentFromDbToDomain(RentEnt rentEnt) {
        Guest guest = GuestMapper.mapGuestFromDbToDomain(rentEnt.getGuest());
        Room room = RoomMapper.mapRoomFromDbToDomain(rentEnt.getRoom());

        Rent rent = new Rent(
                rentEnt.getUuid(),
                rentEnt.getBeginTime(),
                rentEnt.getEndTime(),
                guest,
                room,
                rentEnt.getRentCost()
        );

        rent.setActive(rentEnt.isActive());

        return rent;
    }

    public static RentEnt mapRentFromDomainToDb(Rent rent) {
        RentEnt rentEnt = new RentEnt();

        rentEnt.setUuid(rent.getUuid());
        rentEnt.setBeginTime(rent.getBeginTime());
        rentEnt.setEndTime(rent.getEndTime());
        rentEnt.setRentCost(rent.getRentCost());
        rentEnt.setActive(rent.isActive());

        return rentEnt;
    }
}
