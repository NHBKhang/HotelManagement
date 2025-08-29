package com.team.hotelmanagementapp.services;

import com.team.hotelmanagementapp.pojo.RoomType;
import java.util.List;
import java.util.Map;

public interface RoomTypeService {
    
    List<RoomType> findAll();
    
    List<RoomType> find(Map<String, String> params);

    RoomType getById(int id);

    RoomType createOrUpdate(RoomType type);

    void delete(int id);
    
    void delete(List<Integer> ids);

    long countTypes(Map<String, String> params);
    
}
