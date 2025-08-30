package com.team.hotelmanagementapp.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.team.hotelmanagementapp.pojo.Room;

public interface RoomService {

    List<Room> findAll();

    Room getById(int id);

    List<Room> find(Map<String, String> params, Boolean available);

    Room createOrUpdate(Room room);

    void delete(int id);

    void delete(List<Integer> ids);

    long countRooms(Map<String, String> params, Boolean available);

    // Room availability methods
    List<Room> findAvailableRooms(LocalDate checkIn, LocalDate checkOut);

    List<Room> findAvailableRoomsByType(int roomTypeId, LocalDate checkIn, LocalDate checkOut);

    boolean isRoomAvailable(int roomId, LocalDate checkIn, LocalDate checkOut);

    List<Room> findAvailableRoomsWithFilters(Map<String, Object> filters);
}