package com.team.hotelmanagementapp.controllers;

import com.team.hotelmanagementapp.pojo.Service;
import com.team.hotelmanagementapp.services.ServiceService;
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
@RequestMapping("/services")
public class ServiceController {

    @Autowired
    private ServiceService serviceService;

    @GetMapping
    public String services(Model model, @RequestParam Map<String, String> params,
            RedirectAttributes redirectAttributes) {
        try {
            int page = params.containsKey("page") ? Integer.parseInt(params.get("page")) : 1;

            long totalServices = serviceService.countServices(params);
            int totalPages = (int) Math.ceil((double) totalServices
                    / Integer.parseInt(params.getOrDefault("pageSize", "10")));
            List<Service> services = serviceService.find(params);

            model.addAttribute("rows", services);
            model.addAttribute("totalServices", totalServices);
            model.addAttribute("pagination", new Pagination(page, totalPages));
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra, vui lòng thử lại!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi tải danh sách thành viên!");
        }
        return "services";
    }

    @GetMapping("/add")
    public String showAddServiceForm(Model model) {
        model.addAttribute("service", new Service());
        return "service_form";
    }

    @PostMapping("/save")
    public String saveService(@Valid @ModelAttribute("service") Service service, BindingResult result,
            Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("service", service);
            redirectAttributes.addFlashAttribute("error", "Lỗi hệ thống!");
            if (service.getId() == null) {
                return "redirect:/services/add";
            } else {
                return "redirect:/services/edit/" + service.getId();
            }
        }

        try {
            Service u;
            if (service.getId() == null) {
                u = serviceService.createOrUpdate(service);
                redirectAttributes.addFlashAttribute("success", "Thêm mới thành công!");
            } else {
                u = serviceService.createOrUpdate(service);
                redirectAttributes.addFlashAttribute("success", "Cập nhật thành công!");
            }
            return "redirect:/services/edit/" + u.getId();
        } catch (org.hibernate.exception.ConstraintViolationException e) {
            redirectAttributes.addFlashAttribute("error", "Tên người dùng đã tồn tại!");
            return "redirect:/services/add";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra, vui lòng thử lại!");
            return "redirect:/services/add";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditServiceForm(@PathVariable(value = "id") int id, Model model) {
        Service service = serviceService.getById(id);
        model.addAttribute("service", service);
        return "service_form";
    }

    @DeleteMapping(value = "/delete/{id}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> deleteService(@PathVariable(value = "id") int id) {
        try {
            serviceService.delete(id);
            return ResponseEntity.ok().body(Map.of("message", "Xóa khách hàng thành công!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Đã xảy ra lỗi khi xóa khách hàng!"));
        }
    }

    @DeleteMapping(value = "/delete", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> deleteServices(@RequestBody Map<String, List<Integer>> request) {
        List<Integer> ids = request.get("ids");
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Không có ID nào được chọn để xóa."));
        }

        try {
            serviceService.delete(ids);
            return ResponseEntity.ok().body(Map.of("message", "Xóa thành viên thành công!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Đã xảy ra lỗi khi xóa khách hàng!"));
        }
    }
    
}
