package com.team.hotelmanagementapp.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.team.hotelmanagementapp.pojo.Room;
import com.team.hotelmanagementapp.repositories.RoomRepository;
import com.team.hotelmanagementapp.services.RoomService;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private Cloudinary cloudinary;

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
        if (room.getFile() != null && !room.getFile().isEmpty()) {
            try {
                Map res = cloudinary.uploader().upload(room.getFile().getBytes(),
                        ObjectUtils.asMap("resource_type", "auto"));
                room.setImage(res.get("secure_url").toString());
            } catch (IOException ex) {
                Logger.getLogger(UserServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException("Error uploading file to Cloudinary", ex);
            }
        }
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

    @Override
    public long countByStatus(Room.Status status) {
        return roomRepository.countByStatus(status);
    }

}
