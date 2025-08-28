package com.team.hotelmanagementapp.controllers.api;

import com.team.hotelmanagementapp.pojo.Room;
import com.team.hotelmanagementapp.services.RoomService;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin
public class ApiRoomController {
    
    @Autowired
    private RoomService roomService;
    
    @GetMapping
    public ResponseEntity<List<Room>> getAllRooms() {
        List<Room> rooms = roomService.findAll();
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }
    
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchAvailableRooms(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam(required = false) Integer roomTypeId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, String> params = new HashMap<>();
        if (roomTypeId != null) params.put("roomTypeId", roomTypeId.toString());
        if (minPrice != null) params.put("minPrice", minPrice.toString());
        if (maxPrice != null) params.put("maxPrice", maxPrice.toString());
        if (minPrice != null) params.put("checkInDate", checkIn.toString());
        if (maxPrice != null) params.put("checkOutDate", checkOut.toString());
        
        List<Room> rooms = roomService.find(params, true);
        long totalCount = roomService.countRooms(params, true);
        
        Map<String, Object> response = new HashMap<>();
        response.put("rooms", rooms);
        response.put("totalCount", totalCount);
        response.put("page", page);
        response.put("size", size);
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable int id) {
        Room room = roomService.getById(id);
        if (room != null) {
            return new ResponseEntity<>(room, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
