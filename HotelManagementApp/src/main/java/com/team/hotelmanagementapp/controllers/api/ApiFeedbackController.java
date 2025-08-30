package com.team.hotelmanagementapp.controllers.api;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.team.hotelmanagementapp.controllers.api.dto.FeedbackDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.hotelmanagementapp.components.JwtService;
import com.team.hotelmanagementapp.pojo.Booking;
import com.team.hotelmanagementapp.pojo.Feedback;
import com.team.hotelmanagementapp.pojo.User;
import com.team.hotelmanagementapp.services.BookingService;
import com.team.hotelmanagementapp.services.FeedbackService;
import com.team.hotelmanagementapp.services.UserService;
import com.team.hotelmanagementapp.utils.Pagination;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin
public class ApiFeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<?> list(@RequestParam Map<String, String> params) {
        try {
            List<FeedbackDTO> feedbacks = feedbackService.findAll();
            long totalFeedbacks = feedbackService.countFeedback(params);
            return ResponseEntity.ok(new Pagination<>(feedbacks, totalFeedbacks, params));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Có lỗi xảy ra khi tải danh sách phản hồi!"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFeedback(@PathVariable int id) {
        try {
            Feedback feedback = feedbackService.findById(id);
            if (feedback == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Không tìm thấy phản hồi với ID: " + id));
            }
            return ResponseEntity.ok(feedback);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Có lỗi xảy ra khi lấy phản hồi!"));
        }
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<?> getFeedbackByBooking(@PathVariable int bookingId) {
        try {
            Booking booking = bookingService.findById(bookingId);
            if (booking == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Không tìm thấy booking với ID: " + bookingId));
            }

            List<FeedbackDTO> feedbacks = feedbackService.findByBooking(bookingId);
            return ResponseEntity.ok(feedbacks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Có lỗi xảy ra khi tải phản hồi theo booking!"));
        }
    }

    @GetMapping("/my-feedback")
    public ResponseEntity<?> getMyFeedback(@RequestParam Map<String, String> params, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            if (token == null || jwtService.getUsernameFromToken(token) == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Token không hợp lệ"));
            }

            String username = jwtService.getUsernameFromToken(token);
            User user = userService.getByUsername(username);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Không tìm thấy người dùng"));
            }

            List<FeedbackDTO> feedbacks = feedbackService.findByUser(user.getId(), params);
            long totalFeedbacks = feedbackService.countFeedback(params);
            return ResponseEntity.ok(new Pagination<>(feedbacks, totalFeedbacks, params));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Có lỗi xảy ra khi tải phản hồi của bạn!"));
        }
    }

    @PostMapping
    public ResponseEntity<?> createFeedback(@RequestBody Map<String, Object> feedbackData, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            if (token == null || jwtService.getUsernameFromToken(token) == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Token không hợp lệ"));
            }

            String username = jwtService.getUsernameFromToken(token);
            User user = userService.getByUsername(username);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Không tìm thấy người dùng"));
            }

            // Validate required fields
            if (!feedbackData.containsKey("bookingId") || !feedbackData.containsKey("rating")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Booking ID và rating là bắt buộc"));
            }

            Integer bookingId = (Integer) feedbackData.get("bookingId");
            Booking booking = bookingService.findById(bookingId);

            if (booking == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Không tìm thấy booking"));
            }

            // Check if booking belongs to user
            if (!booking.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Bạn không có quyền đánh giá booking này"));
            }

            // Check if booking is completed (CHECKED_OUT)
            if (booking.getStatus() != Booking.Status.CHECKED_OUT) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Chỉ có thể đánh giá sau khi trả phòng"));
            }

            // Check if feedback already exists
            List<FeedbackDTO> existingFeedbacks = feedbackService.findByBooking(bookingId);
            if (!existingFeedbacks.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Bạn đã đánh giá booking này rồi"));
            }

            // Create feedback
            Feedback feedback = new Feedback();
            feedback.setBooking(booking);
            feedback.setUser(user);
            feedback.setRating(((Number) feedbackData.get("rating")).doubleValue());
            feedback.setComment((String) feedbackData.get("comment"));
            feedback.setCreatedAt(LocalDateTime.now());

            Feedback created = feedbackService.createFeedback(feedback);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Phản hồi đã được tạo thành công");
            response.put("feedback", created);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Có lỗi xảy ra khi tạo phản hồi!"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFeedback(@PathVariable int id, @RequestBody Map<String, Object> feedbackData, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            if (token == null || jwtService.getUsernameFromToken(token) == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Token không hợp lệ"));
            }

            String username = jwtService.getUsernameFromToken(token);
            User user = userService.getByUsername(username);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Không tìm thấy người dùng"));
            }

            Feedback feedback = feedbackService.findById(id);
            if (feedback == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Không tìm thấy phản hồi"));
            }

            // Check if feedback belongs to user
            if (!feedback.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Bạn không có quyền chỉnh sửa phản hồi này"));
            }

            // Update feedback data
            if (feedbackData.containsKey("rating")) {
                feedback.setRating(((Number) feedbackData.get("rating")).doubleValue());
            }
            if (feedbackData.containsKey("comment")) {
                feedback.setComment((String) feedbackData.get("comment"));
            }

            Feedback updated = feedbackService.updateFeedback(id, feedback);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Phản hồi đã được cập nhật thành công");
            response.put("feedback", updated);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Có lỗi xảy ra khi cập nhật phản hồi!"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFeedback(@PathVariable int id, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            if (token == null || jwtService.getUsernameFromToken(token) == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Token không hợp lệ"));
            }

            String username = jwtService.getUsernameFromToken(token);
            User user = userService.getByUsername(username);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Không tìm thấy người dùng"));
            }

            Feedback feedback = feedbackService.findById(id);
            if (feedback == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Không tìm thấy phản hồi"));
            }

            // Check if feedback belongs to user or user is admin/manager
            if (!feedback.getUser().getId().equals(user.getId()) &&
                !user.getRole().equals(User.Role.ADMIN) &&
                !user.getRole().equals(User.Role.MANAGER)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Bạn không có quyền xóa phản hồi này"));
            }

            feedbackService.deleteFeedback(id);

            return ResponseEntity.ok(Map.of("message", "Phản hồi đã được xóa thành công"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Có lỗi xảy ra khi xóa phản hồi!"));
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getFeedbackStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalFeedback", feedbackService.countFeedback(null));
            stats.put("averageRating", feedbackService.getAverageRating());
            stats.put("highRatingFeedback", feedbackService.findByRatingHigherThan(4.0).size());

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Có lỗi xảy ra khi tải thống kê phản hồi!"));
        }
    }
}
