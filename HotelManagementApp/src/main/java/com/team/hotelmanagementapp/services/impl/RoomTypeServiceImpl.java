package com.team.hotelmanagementapp.services.impl;

import com.team.hotelmanagementapp.pojo.RoomType;
import com.team.hotelmanagementapp.repositories.RoomTypeRepository;
import com.team.hotelmanagementapp.services.RoomTypeService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoomTypeServiceImpl implements RoomTypeService {

    @Autowired
    private RoomTypeRepository typeRepository;

    @Override
    public RoomType getById(int id) {
        return typeRepository.getById(id);
    }

    @Override
    public List<RoomType> findAll() {
        return typeRepository.findAll();
    }

    @Override
    public List<RoomType> find(Map<String, String> params) {
        return typeRepository.find(params);
    }

    @Override
    public RoomType createOrUpdate(RoomType room) {
        return typeRepository.createOrUpdate(room);
    }

    @Override
    public void delete(int id) {
        typeRepository.delete(id);
    }

    @Override
    public void delete(List<Integer> ids) {
        typeRepository.delete(ids);
    }

    @Override
    public long countTypes(Map<String, String> params) {
        return typeRepository.countTypes(params);
    }

}
