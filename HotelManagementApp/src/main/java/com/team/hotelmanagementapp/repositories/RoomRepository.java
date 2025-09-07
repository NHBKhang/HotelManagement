package com.team.hotelmanagementapp.repositories;

import com.team.hotelmanagementapp.pojo.Room;
import java.util.List;
import java.util.Map;

public interface RoomRepository {
    
    List<Room> findAll();
    
    Room getById(int id);
    
    List<Room> find(Map<String, String> params, Boolean available);
    
    Room createOrUpdate(Room room);
    
    void delete(int id);
    
    void delete(List<Integer> ids);
    
    long countRooms(Map<String, String> params, Boolean available);

    long countByStatus(Room.Status status);
}