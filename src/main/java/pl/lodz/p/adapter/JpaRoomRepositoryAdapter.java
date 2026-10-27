package pl.lodz.p.adapter;

import pl.lodz.p.adapter.mapper.RoomMapper;
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
        RoomEnt roomEnt = RoomMapper.mapRoomFromDomainToDb(room);

        RoomEnt savedRoom = roomRepository.saveRoom(roomEnt);
        return RoomMapper.mapRoomFromDbToDomain(savedRoom);
    }

    @Override
    public void setRoomArchivedStatus(String number, boolean isArchived) {
        RoomEnt roomEnt = roomRepository.findRoomByNumber(number);
        roomRepository.setRoomArchivedStatus(roomEnt, isArchived);
    }

    @Override
    public Room findRoomByNumber(String number) {
        RoomEnt roomEnt = roomRepository.findRoomByNumber(number);

        return RoomMapper.mapRoomFromDbToDomain(roomEnt);
    }

    @Override
    public List<Room> findAllRooms() {
        List<Room> allRooms = new ArrayList<>();

        for (RoomEnt roomEnt : roomRepository.findAllRooms()) {
            allRooms.add(RoomMapper.mapRoomFromDbToDomain(roomEnt));
        }

        return allRooms;
    }

}
