package pl.lodz.p.adapter.mapper;

import pl.lodz.p.model.db.RoomEnt;
import pl.lodz.p.model.domain.Room;

public class RoomMapper {
    public static Room mapRoomFromDbToDomain(RoomEnt roomEnt) {
        Room room = new Room(roomEnt.getUuid(),
                roomEnt.getNumber(),
                roomEnt.getBedsAmount(),
                roomEnt.getPrice());

        room.setRented(roomEnt.isRented());
        room.setArchived(roomEnt.isArchived());

        return room;
    }

    public static  RoomEnt mapRoomFromDomainToDb(Room room) {
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
