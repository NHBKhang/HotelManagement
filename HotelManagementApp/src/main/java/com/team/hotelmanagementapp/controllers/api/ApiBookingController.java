package com.team.hotelmanagementapp.controllers.api;

import com.team.hotelmanagementapp.components.JwtService;
import com.team.hotelmanagementapp.pojo.Booking;
import com.team.hotelmanagementapp.pojo.Feedback;
import com.team.hotelmanagementapp.pojo.User;
import com.team.hotelmanagementapp.services.BookingService;
import com.team.hotelmanagementapp.services.FeedbackService;
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

    @Autowired
    private FeedbackService feedbackService;

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
            if (booking != null || username.equals(booking.getUser().getUsername())) {
                return new ResponseEntity<>(booking, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Lỗi lấy đơn đặt phòng: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/my-bookings/{id}/feedbacks")
    public ResponseEntity<?> getMyFeedbacks(@RequestParam Map<String, String> params,
            HttpServletRequest request, @PathVariable("id") int id) {
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

            List<Feedback> feedbacks = feedbackService.findByBooking(id, params);
//            long totalFeedbacks = feedbackService.countFeedback(params);
            return ResponseEntity.ok(new Pagination<>(feedbacks, 10, params));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Có lỗi xảy ra khi tải phản hồi của bạn!"));
        }
    }

    @PostMapping("/my-bookings/{id}/feedbacks")
    public ResponseEntity<?> postMyFeedback(
            @PathVariable("id") int bookingId,
            @RequestBody Map<String, String> bodyData,
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

            User user = userService.getByUsername(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Không tìm thấy người dùng");
            }

            Booking booking = bookingService.getById(bookingId);
            if (booking == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy đơn đặt phòng");
            }
            String comment = bodyData.getOrDefault("comment", "").trim();
            String ratingStr = bodyData.getOrDefault("rating", "0");
            Double rating = 0.0;
            try {
                rating = Double.valueOf(ratingStr);
            } catch (NumberFormatException ignored) {
            }

            Feedback feedback = new Feedback();
            feedback.setComment(comment);
            feedback.setRating(rating);
            feedback.setUser(user);
            feedback.setBooking(booking);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    feedbackService.createOrUpdate(feedback));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Có lỗi xảy ra khi lưu phản hồi của bạn!"));
        }
    }

    @PutMapping("/my-bookings/{id}/cancel")
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
