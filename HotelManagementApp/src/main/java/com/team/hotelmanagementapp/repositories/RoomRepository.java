package com.team.hotelmanagementapp.repositories;

import com.team.hotelmanagementapp.pojo.Room;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface RoomRepository {
    
    List<Room> findAll();
    
    Room findById(int id);
    
    List<Room> findAvailableRooms(LocalDate checkIn, LocalDate checkOut, Map<String, String> params);
    
    Room save(Room room);
    
    void delete(int id);
    
    long countAvailableRooms(LocalDate checkIn, LocalDate checkOut, Map<String, String> params);
}