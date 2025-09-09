package com.team.hotelmanagementapp.controllers;

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

import com.team.hotelmanagementapp.pojo.Service;
import com.team.hotelmanagementapp.pojo.ServiceBooking;
import com.team.hotelmanagementapp.services.BookingService;
import com.team.hotelmanagementapp.services.ServiceBookingService;
import com.team.hotelmanagementapp.services.ServiceService;
import com.team.hotelmanagementapp.utils.Pagination;

import jakarta.validation.Valid;

@Controller
@ControllerAdvice
@RequestMapping("/service-bookings")
public class ServiceBookingController {

    @Autowired
    private ServiceBookingService serviceBookingService;
    
    @Autowired
    private BookingService bookingService;
    
    @Autowired
    private ServiceService serviceService;

    @GetMapping
    public String serviceBookings(Model model, @RequestParam Map<String, String> params,
            RedirectAttributes redirectAttributes) {
        try {
            long totalServiceBookings = serviceBookingService.countServiceBookings(params);
            List<ServiceBooking> serviceBookings = serviceBookingService.find(params);

            // Get services for filter dropdown
            List<Service> services = serviceService.find(Map.of());

            model.addAttribute("rows", serviceBookings);
            model.addAttribute("services", services);
            model.addAttribute("totalServiceBookings", totalServiceBookings);
            model.addAttribute("pagination", new Pagination(totalServiceBookings, params));
            
            // Calculate statistics
            double totalRevenue = serviceBookings.stream()
                    .mapToDouble(sb -> sb.getTotalPrice() != null ? sb.getTotalPrice() : 0.0)
                    .sum();
            int totalQuantity = serviceBookings.stream()
                    .mapToInt(sb -> sb.getQuantity() != null ? sb.getQuantity() : 0)
                    .sum();
            
            model.addAttribute("totalRevenue", totalRevenue);
            model.addAttribute("totalQuantity", totalQuantity);
            
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra với tham số lọc, vui lòng thử lại!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi tải danh sách đặt dịch vụ!");
            e.printStackTrace();
        }
        return "serviceBooking/service_bookings";
    }
    
    @GetMapping("/{id}")
    public String serviceBookingDetail(@PathVariable(value = "id") int id, Model model, RedirectAttributes redirectAttributes) {
        try {
            ServiceBooking serviceBooking = serviceBookingService.getById(id);
            if (serviceBooking == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy đặt dịch vụ với ID: " + id);
                return "redirect:/service-bookings";
            }

            model.addAttribute("serviceBooking", serviceBooking);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi tải thông tin đặt dịch vụ!");
            e.printStackTrace();
        }
        return "serviceBooking/service_booking_detail";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("serviceBooking", new ServiceBooking());
        model.addAttribute("bookings", bookingService.findAll());
        model.addAttribute("services", serviceService.find(Map.of()));
        model.addAttribute("isEdit", false);
        
        return "serviceBooking/service_booking_form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable(value = "id") int id, Model model, RedirectAttributes redirectAttributes) {
        try {
            ServiceBooking serviceBooking = serviceBookingService.getById(id);
            if (serviceBooking == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy đặt dịch vụ với ID: " + id);
                return "redirect:/service-bookings";
            }

            model.addAttribute("serviceBooking", serviceBooking);
            model.addAttribute("bookings", bookingService.findAll());
            model.addAttribute("services", serviceService.find(Map.of()));
            model.addAttribute("isEdit", true);
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi tải form chỉnh sửa!");
            e.printStackTrace();
        }
        return "serviceBooking/service_booking_form";
    }

    @PostMapping("/save")
    public String saveServiceBooking(@Valid @ModelAttribute ServiceBooking serviceBooking, 
            BindingResult bindingResult,
            Model model, 
            RedirectAttributes redirectAttributes) {
        
        // Validate business logic
        if (serviceBooking.getQuantity() != null && serviceBooking.getQuantity() <= 0) {
            bindingResult.rejectValue("quantity", "error.serviceBooking",
                "Số lượng phải lớn hơn 0");
        }
        
        if (serviceBooking.getTotalPrice() != null && serviceBooking.getTotalPrice() <= 0) {
            bindingResult.rejectValue("totalPrice", "error.serviceBooking",
                "Tổng tiền phải lớn hơn 0");
        }
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("bookings", bookingService.findAll());
            model.addAttribute("services", serviceService.find(Map.of()));
            model.addAttribute("isEdit", serviceBooking.getId() != null);
            return "serviceBooking/service_booking_form";
        }

        try {
            // Calculate total price if not set
            if (serviceBooking.getTotalPrice() == null && serviceBooking.getService() != null && serviceBooking.getQuantity() != null) {
                double totalPrice = serviceBooking.getService().getPrice() * serviceBooking.getQuantity();
                serviceBooking.setTotalPrice(totalPrice);
            }
            
            ServiceBooking savedServiceBooking = serviceBookingService.createOrUpdate(serviceBooking);
            
            if (serviceBooking.getId() == null) {
                redirectAttributes.addFlashAttribute("success", "Tạo đặt dịch vụ mới thành công!");
            } else {
                redirectAttributes.addFlashAttribute("success", "Cập nhật đặt dịch vụ thành công!");
            }
            
            return "redirect:/service-bookings/" + savedServiceBooking.getId();
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Có lỗi xảy ra khi lưu đặt dịch vụ: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/service-bookings";
        }
    }

    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteServiceBooking(@PathVariable(value = "id") int id) {
        try {
            serviceBookingService.delete(id);
            return ResponseEntity.ok().body(Map.of("success", true, "message", "Xóa đặt dịch vụ thành công!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Có lỗi xảy ra khi xóa đặt dịch vụ!"));
        }
    }

    @DeleteMapping("/bulk-delete")
    @ResponseBody
    public ResponseEntity<?> bulkDelete(@RequestBody List<Integer> ids) {
        try {
            serviceBookingService.delete(ids);
            return ResponseEntity.ok().body(Map.of("success", true, "message", "Xóa thành công " + ids.size() + " đặt dịch vụ"));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Có lỗi xảy ra: " + e.getMessage()));
        }
    }
}
