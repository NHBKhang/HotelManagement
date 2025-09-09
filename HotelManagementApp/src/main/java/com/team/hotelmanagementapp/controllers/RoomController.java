package com.team.hotelmanagementapp.controllers;

import com.team.hotelmanagementapp.pojo.Room;
import com.team.hotelmanagementapp.services.BookingService;
import com.team.hotelmanagementapp.services.FeedbackService;
import com.team.hotelmanagementapp.services.RoomService;
import com.team.hotelmanagementapp.utils.Pagination;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@ControllerAdvice
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private FeedbackService feedbackService;

    @GetMapping
    public String rooms(Model model,
            @RequestParam Map<String, String> params,
            RedirectAttributes redirectAttributes) {
        try {
            long totalRooms = roomService.countRooms(params, false);
            List<Room> rooms = roomService.find(params, false);

            model.addAttribute("rows", rooms);
            model.addAttribute("totalRooms", totalRooms);
            model.addAttribute("pagination", new Pagination(totalRooms, params));

            model.addAttribute("availableCount", roomService.countByStatus(Room.Status.AVAILABLE));
            model.addAttribute("occupiedCount", roomService.countByStatus(Room.Status.OCCUPIED));
            model.addAttribute("maintenanceCount", roomService.countByStatus(Room.Status.MAINTENANCE));
            model.addAttribute("cleaningCount", roomService.countByStatus(Room.Status.CLEANING));
            model.addAttribute("bookedCount", roomService.countByStatus(Room.Status.BOOKED));
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi khi đọc tham số tìm kiếm!");
            return "redirect:/rooms";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi tải danh sách phòng!");
            return "redirect:/rooms";
        }
        return "room/rooms";
    }
    
    @GetMapping("/{id}")
    public String roomDetail(@PathVariable(value = "id") int id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Room room = roomService.getById(id);
            if (room == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy phòng với ID: " + id);
                return "redirect:/rooms";
            }

            model.addAttribute("room", room);
            model.addAttribute("bookings", this.bookingService.findRecentBookingsByRoom(room, 5));
//            model.addAttribute("feedbacks", this.feedbackService.findByRoom(room));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi tải thông tin phòng!");
            e.printStackTrace();
        }
        return "room/room_detail";
    }

    @GetMapping("/add")
    public String showAddRoomForm(Model model) {
        model.addAttribute("room", new Room());
        return "room/room_form";
    }

    @PostMapping("/save")
    public String saveRoom(@Valid @ModelAttribute("room") Room room, BindingResult result,
            Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("room", room);
            redirectAttributes.addFlashAttribute("error", "Lỗi hệ thống!");
            if (room.getId() == null) {
                return "redirect:/rooms/add";
            } else {
                return "redirect:/rooms/edit/" + room.getId();
            }
        }

        try {
            Room r;
            if (room.getId() == null) {
                r = roomService.createOrUpdate(room);
                redirectAttributes.addFlashAttribute("success", "Thêm mới thành công!");
            } else {
                r = roomService.createOrUpdate(room);
                redirectAttributes.addFlashAttribute("success", "Cập nhật thành công!");
            }
            return "redirect:/rooms/edit/" + r.getId();
        } catch (org.hibernate.exception.ConstraintViolationException e) {
            redirectAttributes.addFlashAttribute("error", "Tên người dùng đã tồn tại!");
            return "redirect:/rooms/add";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra, vui lòng thử lại!");
            return "redirect:/rooms/add";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditRoomForm(@PathVariable(value = "id") int id, Model model) {
        Room room = roomService.getById(id);
        model.addAttribute("room", room);
        return "room/room_form";
    }

    @DeleteMapping(value = "/delete/{id}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> deleteRoom(@PathVariable(value = "id") int id) {
        try {
            roomService.delete(id);
            return ResponseEntity.ok().body(Map.of("message", "Xóa phòng thành công!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Đã xảy ra lỗi khi xóa phòng!"));
        }
    }

    @DeleteMapping(value = "/bulk-delete", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> deleteRooms(@RequestBody Map<String, List<Integer>> request) {
        List<Integer> ids = request.get("ids");
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Không có ID nào được chọn để xóa."));
        }

        try {
            roomService.delete(ids);
            return ResponseEntity.ok().body(Map.of("message", "Xóa phòng thành công!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Đã xảy ra lỗi khi xóa phòng!"));
        }
    }

}
