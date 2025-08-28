package com.team.hotelmanagementapp.repositories;

import com.team.hotelmanagementapp.pojo.RoomType;
import java.util.List;

public interface RoomTypeRepository {
    
    List<RoomType> findAll();
    
    RoomType findById(int id);
    
    RoomType save(RoomType roomType);
    
    void delete(int id);
}