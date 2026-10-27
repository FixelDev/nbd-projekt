package pl.lodz.p.adapter;

import pl.lodz.p.model.domain.Room;

import java.util.List;

public interface JpaRoomRepositoryAdapterInterface {
    Room saveRoom(Room room);
    void setRoomArchivedStatus(String number, boolean isArchived);
    Room findRoomByNumber(String number);
    List<Room> findAllRooms();
}
