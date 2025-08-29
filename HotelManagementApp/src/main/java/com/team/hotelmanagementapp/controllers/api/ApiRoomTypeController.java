package com.team.hotelmanagementapp.controllers.api;

import com.team.hotelmanagementapp.pojo.RoomType;
import com.team.hotelmanagementapp.services.RoomTypeService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/room-types")
@CrossOrigin
public class ApiRoomTypeController {
    
    @Autowired
    private RoomTypeService typeService;
    
    @GetMapping
    public ResponseEntity<List<RoomType>> getAllRoomTypes() {
        List<RoomType> roomTypes = typeService.findAll();
        return new ResponseEntity<>(roomTypes, HttpStatus.OK);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<RoomType> getRoomTypeById(@PathVariable int id) {
        RoomType roomType = typeService.getById(id);
        if (roomType != null) {
            return new ResponseEntity<>(roomType, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}