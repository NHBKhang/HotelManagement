package com.team.hotelmanagementapp.controllers.api;

import com.team.hotelmanagementapp.components.JwtService;
import com.team.hotelmanagementapp.pojo.Booking;
import com.team.hotelmanagementapp.pojo.Room;
import com.team.hotelmanagementapp.pojo.User;
import com.team.hotelmanagementapp.services.BookingService;
import com.team.hotelmanagementapp.services.RoomService;
import com.team.hotelmanagementapp.services.UserService;
import com.team.hotelmanagementapp.utils.Pagination;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
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

    @Autowired
    private JwtService jwtService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, String> bodyData,
            HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Thiếu token xác thực");
            }

            String username = jwtService.getUsernameFromToken(token);
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token không hợp lệ");
            }

            Booking b = new Booking();
            b.setCheckInDate(LocalDate.parse(bodyData.get("checkin")));
            b.setCheckOutDate(LocalDate.parse(bodyData.get("checkout")));
            b.setGuests(Integer.valueOf(bodyData.get("guests")));
            b.setStatus(Booking.Status.PROCESSING);
            b.setUser(this.userService.getByUsername(username));
            b.setRoom(this.roomService.getById(Integer.parseInt(bodyData.get("roomId"))));

            return new ResponseEntity<>(this.bookingService.createOrUpdate(b), HttpStatus.CREATED);
        } catch (NumberFormatException e) {
            return new ResponseEntity<>("Lỗi tạo đơn đặt phòng: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<?> getMyBookings(HttpServletRequest request,
            @RequestParam Map<String, String> params) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Thiếu token xác thực");
            }

            String username = jwtService.getUsernameFromToken(token);
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token không hợp lệ");
            }

            List<Booking> bookings = this.bookingService.findByUsername(params, username);
            Long totalBookings = this.bookingService.countBookingsByUsername(params, username);

            return new ResponseEntity<>(new Pagination<>(bookings, totalBookings, params), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Lỗi lấy đơn đặt phòng: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/my-bookings/{id}")
    public ResponseEntity<?> retrieveMyBooking(@PathVariable("id") int id, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Thiếu token xác thực");
            }

            String username = jwtService.getUsernameFromToken(token);
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token không hợp lệ");
            }
            
            Booking booking = bookingService.getById(id);
            if (booking != null) {
                return new ResponseEntity<>(booking, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Lỗi lấy đơn đặt phòng: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @PutMapping("/{id}/status")
//    public ResponseEntity<Map<String, Object>> updateBookingStatus(
//            @PathVariable int id,
//            @RequestParam String status) {
//        
//        try {
//            Booking.Status bookingStatus = Booking.Status.valueOf(status.toUpperCase());
//            Booking updatedBooking = bookingService.updateBookingStatus(id, bookingStatus);
//            
//            if (updatedBooking != null) {
//                Map<String, Object> response = new HashMap<>();
//                response.put("booking", updatedBooking);
//                response.put("message", "Booking status updated successfully");
//                return new ResponseEntity<>(response, HttpStatus.OK);
//            } else {
//                Map<String, Object> error = new HashMap<>();
//                error.put("error", "Booking not found");
//                return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
//            }
//        } catch (IllegalArgumentException e) {
//            Map<String, Object> error = new HashMap<>();
//            error.put("error", "Invalid status value");
//            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
//        }
//    }
//    @GetMapping("/user/{userId}")
//    public ResponseEntity<List<Booking>> getUserBookings(
//            @PathVariable int userId,
//            @RequestParam(required = false) String status) {
//        
//        Map<String, String> params = new HashMap<>();
//        if (status != null) params.put("status", status);
//        
//        List<Booking> bookings = bookingService.findByUser(userId, params);
//        return new ResponseEntity<>(bookings, HttpStatus.OK);
//    }
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
