package com.team.hotelmanagementapp.services.impl;

import com.team.hotelmanagementapp.pojo.Room;
import com.team.hotelmanagementapp.repositories.RoomRepository;
import com.team.hotelmanagementapp.services.RoomService;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Override
    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    @Override
    public Room findById(int id) {
        return roomRepository.findById(id);
    }

    @Override
    public List<Room> findAvailableRooms(LocalDate checkIn, LocalDate checkOut, Map<String, String> params) {
        return roomRepository.findAvailableRooms(checkIn, checkOut, params);
    }

    @Override
    public Room save(Room room) {
        return roomRepository.save(room);
    }

    @Override
    public void delete(int id) {
        roomRepository.delete(id);
    }

    @Override
    public long countAvailableRooms(LocalDate checkIn, LocalDate checkOut, Map<String, String> params) {
        return roomRepository.countAvailableRooms(checkIn, checkOut, params);
    }
}