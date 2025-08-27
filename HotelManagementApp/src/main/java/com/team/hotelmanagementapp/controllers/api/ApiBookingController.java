package com.team.hotelmanagementapp.controllers.api;

import com.team.hotelmanagementapp.pojo.Booking;
import com.team.hotelmanagementapp.pojo.Room;
import com.team.hotelmanagementapp.pojo.User;
import com.team.hotelmanagementapp.services.BookingService;
import com.team.hotelmanagementapp.services.RoomService;
import com.team.hotelmanagementapp.services.UserService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin
public class ApiBookingController {
    
    @Autowired
    private BookingService bookingService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private RoomService roomService;
    
    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = bookingService.findAll();
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable int id) {
        Booking booking = bookingService.findById(id);
        if (booking != null) {
            return new ResponseEntity<>(booking, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Booking>> getUserBookings(
            @PathVariable int userId,
            @RequestParam(required = false) String status) {
        
        Map<String, String> params = new HashMap<>();
        if (status != null) params.put("status", status);
        
        List<Booking> bookings = bookingService.findByUser(userId, params);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }
    
    @GetMapping("/my-bookings")
    public ResponseEntity<List<Booking>> getMyBookings(
            @RequestParam(required = false) String status) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.getByUsername(username);
        
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        
        Map<String, String> params = new HashMap<>();
        if (status != null) params.put("status", status);
        
        List<Booking> bookings = bookingService.findByUser(user.getId(), params);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> createBooking(@RequestBody Booking booking) {
        try {
            // Validate room availability
            Room room = roomService.findById(booking.getRoom().getId());
            if (room == null || room.getStatus() != Room.Status.AVAILABLE) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Room is not available");
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
            }
            
            // Set user from authentication
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            User user = userService.getByUsername(username);
            booking.setUser(user);
            booking.setRoom(room);
            
            Booking createdBooking = bookingService.createBooking(booking);
            
            Map<String, Object> response = new HashMap<>();
            response.put("booking", createdBooking);
            response.put("message", "Booking created successfully");
            
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to create booking: " + e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateBookingStatus(
            @PathVariable int id,
            @RequestParam String status) {
        
        try {
            Booking.Status bookingStatus = Booking.Status.valueOf(status.toUpperCase());
            Booking updatedBooking = bookingService.updateBookingStatus(id, bookingStatus);
            
            if (updatedBooking != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("booking", updatedBooking);
                response.put("message", "Booking status updated successfully");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Booking not found");
                return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Invalid status value");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }
    
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Map<String, Object>> cancelBooking(@PathVariable int id) {
        try {
            bookingService.cancelBooking(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Booking cancelled successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to cancel booking");
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}