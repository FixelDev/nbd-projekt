package pl.lodz.p.adapter;

import pl.lodz.p.model.db.AddressEnt;
import pl.lodz.p.model.db.GuestEnt;
import pl.lodz.p.model.db.GuestTypeEnt;
import pl.lodz.p.model.db.RoomEnt;
import pl.lodz.p.model.domain.Address;
import pl.lodz.p.model.domain.Guest;
import pl.lodz.p.model.domain.GuestType;
import pl.lodz.p.model.domain.Room;
import pl.lodz.p.repository.JpaRoomRepository;

import java.util.ArrayList;
import java.util.List;

public class JpaRoomRepositoryAdapter implements JpaRoomRepositoryAdapterInterface {
    private JpaRoomRepository roomRepository;

    public JpaRoomRepositoryAdapter(JpaRoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public Room saveRoom(Room room) {
        RoomEnt roomEnt = mapRoomFromDomainToDb(room);

        RoomEnt savedRoom = roomRepository.saveRoom(roomEnt);
        return mapRoomFromDbToDomain(savedRoom);
    }

    @Override
    public void setRoomArchivedStatus(String number, boolean isArchived) {
        roomRepository.setRoomArchivedStatus(number, isArchived);
    }

    @Override
    public Room findRoomByNumber(String number) {
        RoomEnt roomEnt = roomRepository.findRoomByNumber(number);

        return mapRoomFromDbToDomain(roomEnt);
    }

    @Override
    public List<Room> findAllRooms() {
        List<Room> allRooms = new ArrayList<>();

        for (RoomEnt roomEnt : roomRepository.findAllRooms()) {
            allRooms.add(mapRoomFromDbToDomain(roomEnt));
        }

        return allRooms;
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

    private RoomEnt mapRoomFromDomainToDb(Room room) {
        RoomEnt roomEnt = new RoomEnt();

        roomEnt.setUuid(room.getUuid());
        roomEnt.setNumber(room.getNumber());
        roomEnt.setBedsAmount(room.getBedsAmount());
        roomEnt.setPrice(room.getBasePrice());
        roomEnt.setArchived(room.isArchived());
        roomEnt.setRented(room.isRented());

        return roomEnt;
    }
}
