package com.team.hotelmanagementapp.controllers;

import com.team.hotelmanagementapp.pojo.Payment;
import com.team.hotelmanagementapp.pojo.User;
import com.team.hotelmanagementapp.services.PaymentService;
import com.team.hotelmanagementapp.services.UserService;
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
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private PaymentService paymentService;

    @GetMapping
    public String users(Model model, @RequestParam Map<String, String> params,
            RedirectAttributes redirectAttributes) {
        try {
            long totalUsers = userService.countUsers(params);
            List<User> users = userService.find(params);

            model.addAttribute("rows", users);
            model.addAttribute("totalUsers", totalUsers);
            model.addAttribute("pagination", new Pagination(totalUsers, params));
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra, vui lòng thử lại!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi tải danh sách khách hàng!");
        }
        return "users";
    }

    @GetMapping("/add")
    public String showAddUserForm(Model model) {
        model.addAttribute("user", new User());
        return "user_form";
    }

    @PostMapping("/save")
    public String saveUser(@Valid @ModelAttribute("user") User user, BindingResult result,
            Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            redirectAttributes.addFlashAttribute("error", "Lỗi hệ thống!");
            if (user.getId() == null) {
                return "redirect:/users/add";
            } else {
                return "redirect:/users/edit/" + user.getId();
            }
        }

        try {
            User u;
            if (user.getId() == null) {
                u = userService.createOrUpdate(user);
                redirectAttributes.addFlashAttribute("success", "Thêm mới thành công!");
            } else {
                u = userService.createOrUpdate(user);
                redirectAttributes.addFlashAttribute("success", "Cập nhật thành công!");
            }
            return "redirect:/users/edit/" + u.getId();
        } catch (org.hibernate.exception.ConstraintViolationException e) {
            redirectAttributes.addFlashAttribute("error", "Tên người dùng đã tồn tại!");
            return "redirect:/users/add";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra, vui lòng thử lại!");
            return "redirect:/users/add";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditUserForm(@PathVariable(value = "id") int id, Model model) {
        User user = userService.getById(id);
        model.addAttribute("user", user);
        return "user_form";
    }

    @DeleteMapping(value = "/delete/{id}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> deleteUser(@PathVariable(value = "id") int id) {
        try {
            userService.delete(id);
            return ResponseEntity.ok().body(Map.of("message", "Xóa khách hàng thành công!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Đã xảy ra lỗi khi xóa khách hàng!"));
        }
    }

    @DeleteMapping(value = "/delete", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> deleteUsers(@RequestBody Map<String, List<Integer>> request) {
        List<Integer> ids = request.get("ids");
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Không có ID nào được chọn để xóa."));
        }

        try {
            userService.delete(ids);
            return ResponseEntity.ok().body(Map.of("message", "Xóa khách hàng thành công!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Đã xảy ra lỗi khi xóa khách hàng!"));
        }
    }

    @GetMapping("/{id}/payments")
    public String getPaymentsPage(@PathVariable("id") int id,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            Model model) {
        Map<String, String> params = Map.of("page", String.valueOf(page),
                "pageSize", String.valueOf(pageSize));
        List<Payment> payments = paymentService.findByUserId(id, params);
        long totalPayments = paymentService.countByUserId(id, params);

        model.addAttribute("payments", payments);
        model.addAttribute("pagination", new Pagination(totalPayments, params));

        return "fragments/tables/payments :: table";
    }
}
