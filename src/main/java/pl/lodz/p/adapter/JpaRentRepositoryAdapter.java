package pl.lodz.p.adapter;

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
        RentEnt savedRent = rentRepository.saveRent(mapRentFromDomainToDb(rent), rent.getRoom().getNumber(), rent.getGuest().getPersonalId());
        return mapRentFromDbToDomain(savedRent);
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

    private Rent mapRentFromDbToDomain(RentEnt rentEnt) {
        Guest guest = mapGuestFromDbToDomain(rentEnt.getGuest());
        Room room = mapRoomFromDbToDomain(rentEnt.getRoom());

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

    private RentEnt mapRentFromDomainToDb(Rent rent) {
        RentEnt rentEnt = new RentEnt();


        rentEnt.setUuid(rent.getUuid());
        rentEnt.setBeginTime(rent.getBeginTime());
        rentEnt.setEndTime(rent.getEndTime());
        rentEnt.setRentCost(rent.getRentCost());
        rentEnt.setActive(rent.isActive());

        return rentEnt;
    }

    // DO PRZENIESIENIA

    private Guest mapGuestFromDbToDomain(GuestEnt guestEnt) {
        Address address = mapAddressFromDbToDomain(guestEnt.getAddress());
        GuestType guestType = mapGuestTypeFromDbToDomain(guestEnt.getGuestType());


        Guest guest = new Guest(guestEnt.getUuid(),
                guestEnt.getFirstName(),
                guestEnt.getLastName(),
                guestEnt.getPersonalId(),
                address,
                guestType);

        guest.setArchived(guestEnt.isArchived());

        return guest;
    }


    private GuestType mapGuestTypeFromDbToDomain(GuestTypeEnt guestTypeEnt) {
        GuestType guestType;

        if (guestTypeEnt instanceof GuestTypeDefaultEnt) {
            guestType = new GuestTypeDefault();
        } else if (guestTypeEnt instanceof GuestTypeExtraEnt) {
            guestType = new GuestTypeExtra();
        } else {
            guestType = new GuestTypePremium();
        }

        return guestType;
    }

    private Address mapAddressFromDbToDomain(AddressEnt addressEnt) {
        Address address = new Address(addressEnt.getCity(), addressEnt.getStreet(), addressEnt.getNumber());

        return address;
    }

    private Room mapRoomFromDbToDomain(RoomEnt roomEnt) {
        Room room = new Room(roomEnt.getUuid(),
                roomEnt.getNumber(),
                roomEnt.getBedsAmount(),
                roomEnt.getPrice());

        room.setRented(roomEnt.isRented());
        room.setArchived(roomEnt.isArchived());

        return room;
    }
}
