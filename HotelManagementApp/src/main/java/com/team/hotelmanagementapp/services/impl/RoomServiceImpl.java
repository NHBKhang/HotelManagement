package com.team.hotelmanagementapp.services.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.team.hotelmanagementapp.pojo.Room;
import com.team.hotelmanagementapp.repositories.RoomRepository;
import com.team.hotelmanagementapp.services.RoomService;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Override
    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    @Override
    public Room getById(int id) {
        return roomRepository.getById(id);
    }

    @Override
    public List<Room> find(Map<String, String> params, Boolean available) {
        return roomRepository.find(params, available);
    }

    @Override
    public Room createOrUpdate(Room room) {
        return roomRepository.createOrUpdate(room);
    }

    @Override
    public void delete(int id) {
        roomRepository.delete(id);
    }

    @Override
    public void delete(List<Integer> ids) {
        roomRepository.delete(ids);
    }

    @Override
    public long countRooms(Map<String, String> params, Boolean available) {
        return roomRepository.countRooms(params, available);
    }

}
