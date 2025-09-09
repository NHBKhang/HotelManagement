package com.team.hotelmanagementapp.controllers;

import java.time.LocalDate;
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
import com.team.hotelmanagementapp.pojo.Booking;
import com.team.hotelmanagementapp.services.BookingService;
import com.team.hotelmanagementapp.services.FeedbackService;
import com.team.hotelmanagementapp.services.RoomService;
import com.team.hotelmanagementapp.services.UserService;
import com.team.hotelmanagementapp.utils.Pagination;

import jakarta.validation.Valid;

@Controller
@ControllerAdvice
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;
    
    @Autowired
    private RoomService roomService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private FeedbackService feedbackService;

    @GetMapping
    public String bookings(Model model, @RequestParam Map<String, String> params,
            RedirectAttributes redirectAttributes) {
        try {
            long totalBookings = bookingService.countBookings(params);
            List<Booking> bookings = bookingService.find(params);

            model.addAttribute("rows", bookings);
            model.addAttribute("totalBookings", totalBookings);
            model.addAttribute("pagination", new Pagination(totalBookings, params));
            model.addAttribute("bookingStatuses", Booking.Status.values());
            
            model.addAttribute("currentDate", LocalDate.now());
            
            long pendingCount = bookings.stream().filter(b -> b.getStatus() == Booking.Status.PENDING).count();
            long confirmedCount = bookings.stream().filter(b -> b.getStatus() == Booking.Status.CONFIRMED).count();
            long checkedInCount = bookings.stream().filter(b -> b.getStatus() == Booking.Status.CHECKED_IN).count();
            long checkedOutCount = bookings.stream().filter(b -> b.getStatus() == Booking.Status.CHECKED_OUT).count();
            
            model.addAttribute("pendingCount", pendingCount);
            model.addAttribute("confirmedCount", confirmedCount);
            model.addAttribute("checkedInCount", checkedInCount);
            model.addAttribute("checkedOutCount", checkedOutCount);
            
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra với tham số lọc, vui lòng thử lại!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi tải danh sách booking!");
            e.printStackTrace();
        }
        return "booking/bookings";
    }
    
    @GetMapping("/{id}")
    public String bookingDetail(@PathVariable(value = "id") int id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Booking booking = bookingService.getById(id);
            if (booking == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy booking với ID: " + id);
                return "redirect:/bookings";
            }

            model.addAttribute("booking", booking);
            model.addAttribute("bookingStatuses", Booking.Status.values());
            model.addAttribute("feedbacks", feedbackService.findByBooking(id, null));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi tải thông tin booking!");
            e.printStackTrace();
        }
        return "booking/booking_detail";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("booking", new Booking());
        model.addAttribute("rooms", roomService.findAll());
        model.addAttribute("users", userService.find(Map.of()));
        model.addAttribute("bookingStatuses", Booking.Status.values());
        model.addAttribute("isEdit", false);
        
        return "booking/booking_form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable(value = "id") int id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Booking booking = bookingService.getById(id);
            if (booking == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy booking với ID: " + id);
                return "redirect:/bookings";
            }

            model.addAttribute("booking", booking);
            model.addAttribute("rooms", roomService.findAll());
            model.addAttribute("users", userService.find(Map.of()));
            model.addAttribute("bookingStatuses", Booking.Status.values());
            model.addAttribute("isEdit", true);
            model.addAttribute("feedbacks", this.feedbackService.findByBooking(id, null));
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi tải form chỉnh sửa!");
            e.printStackTrace();
        }
        return "booking/booking_form";
    }

    @PostMapping("/save")
    public String saveBooking(@Valid @ModelAttribute Booking booking, 
            BindingResult bindingResult,
            Model model, 
            RedirectAttributes redirectAttributes) {
        if (booking.getCheckInDate() != null && booking.getCheckOutDate() != null) {
            if (booking.getCheckInDate().isAfter(booking.getCheckOutDate())) {
                bindingResult.rejectValue("checkOutDate", "error.booking",
                    "Ngày trả phòng phải sau ngày nhận phòng");
            }
            if (booking.getCheckInDate().isBefore(LocalDate.now()) && booking.getId() == null) {
                bindingResult.rejectValue("checkInDate", "error.booking",
                    "Ngày nhận phòng không thể là ngày trong quá khứ");
            }
            
            // Check room availability
            if (booking.getRoom() != null && booking.getRoom().getId() != null) {
                boolean isAvailable = bookingService.isRoomAvailable(
                    booking.getRoom().getId(),
                    booking.getCheckInDate(),
                    booking.getCheckOutDate(),
                    booking.getId() // Exclude current booking when editing
                );
                
                if (!isAvailable) {
                    bindingResult.rejectValue("room", "error.booking",
                        "Phòng này đã được đặt trong khoảng thời gian bạn chọn. Vui lòng chọn phòng khác hoặc thời gian khác.");
                }
            }
        }
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("rooms", roomService.findAll());
            model.addAttribute("users", userService.find(Map.of()));
            model.addAttribute("bookingStatuses", Booking.Status.values());
            model.addAttribute("isEdit", booking.getId() != null);
            return "booking/booking_form";
        }

        try {
            Booking savedBooking = bookingService.createOrUpdate(booking);
            
            if (booking.getId() == null) {
                redirectAttributes.addFlashAttribute("success", 
                    "Tạo booking mới thành công! Mã booking: " + savedBooking.getCode());
            } else {
                redirectAttributes.addFlashAttribute("success", "Cập nhật booking thành công!");
            }
            
            return "redirect:/bookings/" + savedBooking.getId();
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Có lỗi xảy ra khi lưu booking: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/bookings";
        }
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable(value = "id") int id,
            @RequestParam(value = "status") Booking.Status status,
            RedirectAttributes redirectAttributes) {
        try {
            Booking booking = bookingService.getById(id);
            if (booking == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy booking với ID: " + id);
                return "redirect:/bookings";
            }
            
            // Validate status transition
            if (!isValidStatusTransition(booking.getStatus(), status)) {
                redirectAttributes.addFlashAttribute("error", 
                    "Không thể chuyển trạng thái từ " + booking.getStatus().getDescription() + 
                    " sang " + status.getDescription());
                return "redirect:/bookings/" + id;
            }
            
            booking.setStatus(status);
            bookingService.createOrUpdate(booking);
            
            redirectAttributes.addFlashAttribute("success", 
                "Cập nhật trạng thái booking thành công!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Có lỗi xảy ra khi cập nhật trạng thái: " + e.getMessage());
            e.printStackTrace();
        }
        
        return "redirect:/bookings/" + id;
    }

    @PostMapping("/{id}/cancel")
    public String cancelBooking(@PathVariable(value = "id") int id, RedirectAttributes redirectAttributes) {
        try {
            bookingService.cancelBooking(id);
            redirectAttributes.addFlashAttribute("success", "Hủy booking thành công!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Có lỗi xảy ra khi hủy booking: " + e.getMessage());
            e.printStackTrace();
        }
        
        return "redirect:/bookings";
    }

    @GetMapping("/calendar")
    public String bookingCalendar(Model model, @RequestParam Map<String, String> params) {
        try {
            List<Booking> bookings = bookingService.find(params);
            model.addAttribute("bookings", bookings);
            model.addAttribute("rooms", roomService.findAll());

        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra khi tải lịch booking!");
            e.printStackTrace();
        }
        return "booking/booking_calendar";
    }

    @GetMapping("/check-availability")
    @ResponseBody
    public ResponseEntity<?> checkAvailability(
            @RequestParam(value = "roomId") int roomId,
            @RequestParam(value = "checkIn") String checkIn,
            @RequestParam(value = "checkOut") String checkOut,
            @RequestParam(value = "excludeBookingId", required = false) Integer excludeBookingId) {
        try {
            LocalDate checkInDate = LocalDate.parse(checkIn);
            LocalDate checkOutDate = LocalDate.parse(checkOut);
            
            boolean isAvailable = bookingService.isRoomAvailable(roomId, checkInDate, checkOutDate, excludeBookingId);
            
            if (isAvailable) {
                return ResponseEntity.ok().body(Map.of("available", true, "message", "Phòng còn trống"));
            } else {
                List<Booking> conflictingBookings = bookingService.findBookingsByRoomAndDateRange(roomId, checkInDate, checkOutDate);
                return ResponseEntity.ok().body(Map.of(
                    "available", false,
                    "message", "Phòng đã được đặt trong khoảng thời gian này",
                    "conflictingBookings", conflictingBookings.size()
                ));
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("available", false, "message", "Có lỗi xảy ra: " + e.getMessage()));
        }
    }

    @DeleteMapping("/bulk-delete")
    @ResponseBody
    public ResponseEntity<?> bulkDelete(@RequestBody List<Integer> ids) {
        try {
            for (Integer id : ids) {
                bookingService.cancelBooking(id);
            }
            return ResponseEntity.ok().body(Map.of("success", true, "message", "Xóa thành công " + ids.size() + " booking"));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Có lỗi xảy ra: " + e.getMessage()));
        }
    }

    private boolean isValidStatusTransition(Booking.Status currentStatus, Booking.Status newStatus) {
        if (currentStatus == newStatus) {
            return true;
        }
        
        switch (currentStatus) {
            case PENDING:
                return newStatus == Booking.Status.CONFIRMED || 
                       newStatus == Booking.Status.CANCELLED ||
                       newStatus == Booking.Status.PROCESSING;
                       
            case PROCESSING:
                return newStatus == Booking.Status.CONFIRMED || 
                       newStatus == Booking.Status.CANCELLED;
                       
            case CONFIRMED:
                return newStatus == Booking.Status.CHECKED_IN || 
                       newStatus == Booking.Status.CANCELLED;
                       
            case CHECKED_IN:
                return newStatus == Booking.Status.CHECKED_OUT;
                
            case CHECKED_OUT:
            case CANCELLED:
                return false;
                
            default:
                return false;
        }
    }
}
