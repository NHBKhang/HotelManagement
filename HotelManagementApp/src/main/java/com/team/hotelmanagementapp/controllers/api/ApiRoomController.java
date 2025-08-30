package com.team.hotelmanagementapp.controllers.api;

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

import com.team.hotelmanagementapp.pojo.Room;
import com.team.hotelmanagementapp.services.RoomService;
import com.team.hotelmanagementapp.utils.Pagination;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin
public class ApiRoomController {

    @Autowired
    private RoomService roomService;

    @GetMapping
    public ResponseEntity<?> list(@RequestParam Map<String, String> params) {
        try {
            return ResponseEntity.ok().body(new Pagination<>(
                    roomService.find(params, true), roomService.countRooms(params, true), params));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Có lỗi xảy ra khi tải danh sách người dùng!");
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchAvailableRooms(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam(required = false) Integer roomTypeId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Integer maxGuests,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            // Validate date range
            if (checkIn.isAfter(checkOut) || checkIn.isEqual(checkOut)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Ngày nhận phòng phải trước ngày trả phòng"));
            }

            if (checkIn.isBefore(LocalDate.now())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Ngày nhận phòng không được trước ngày hôm nay"));
            }

            // Build search filters
            Map<String, Object> filters = new HashMap<>();
            filters.put("checkIn", checkIn.toString());
            filters.put("checkOut", checkOut.toString());

            if (roomTypeId != null) {
                filters.put("roomTypeId", roomTypeId.toString());
            }
            if (minPrice != null) {
                filters.put("minPrice", minPrice.toString());
            }
            if (maxPrice != null) {
                filters.put("maxPrice", maxPrice.toString());
            }
            if (maxGuests != null) {
                filters.put("maxGuests", maxGuests.toString());
            }

            List<Room> availableRooms = roomService.findAvailableRoomsWithFilters(filters);

            // Calculate price totals
            for (Room room : availableRooms) {
                if (room.getRoomType() != null) {
                    long totalNights = java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
                    double totalPrice = room.getRoomType().getPricePerNight() * totalNights;
                    room.setRoomNumber(String.format("%.2f", totalPrice)); // Temporary use of roomNumber for display
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("rooms", availableRooms);
            response.put("totalCount", availableRooms.size());
            response.put("checkIn", checkIn.toString());
            response.put("checkOut", checkOut.toString());
            response.put("totalNights", java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut));
            response.put("query", filters);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Có lỗi xảy ra khi tìm kiếm phòng trống!"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> retrieve(@PathVariable("id") int id) {
        try {
            Room r = roomService.getById(id);

            if (r == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy gói tập với ID: " + id);
            }

            return ResponseEntity.ok(r);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi khi lấy phòng!");
        }
    }
}
