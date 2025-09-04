package com.team.hotelmanagementapp.controllers.api;

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
