package com.team.hotelmanagementapp.controllers.api;

import com.team.hotelmanagementapp.pojo.RoomType;
import com.team.hotelmanagementapp.services.RoomTypeService;
import com.team.hotelmanagementapp.utils.Pagination;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/room-types")
@CrossOrigin
public class ApiRoomTypeController {
    
    @Autowired
    private RoomTypeService typeService;
    
    @GetMapping
    public ResponseEntity<?> getAllRoomTypes(@RequestParam Map<String, String> params) {
        List<RoomType> roomTypes = typeService.find(params);
        Long total = typeService.countTypes(params);
        return new ResponseEntity<>(new Pagination<>(roomTypes, total, params), HttpStatus.OK);
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