package com.team.hotelmanagementapp.controllers;

import com.team.hotelmanagementapp.pojo.RoomType;
import com.team.hotelmanagementapp.services.RoomTypeService;
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
@RequestMapping("/room-types")
public class RoomTypeController {

    @Autowired
    private RoomTypeService typeService;

    @GetMapping
    public String types(Model model, @RequestParam Map<String, String> params,
            RedirectAttributes redirectAttributes) {
        try {
            long totalTypes = typeService.countTypes(params);
            List<RoomType> types = typeService.find(params);

            model.addAttribute("rows", types);
            model.addAttribute("totalTypes", totalTypes);
            model.addAttribute("pagination", new Pagination(totalTypes, params));
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra, vui lòng thử lại!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi tải danh sách loại phòng!");
        }
        return "room_types";
    }

    @GetMapping("/add")
    public String showAddRoomTypeForm(Model model) {
        model.addAttribute("type", new RoomType());
        return "room_types_form";
    }

    @PostMapping("/save")
    public String saveRoomType(@Valid @ModelAttribute("type") RoomType type, BindingResult result,
            Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("type", type);
            redirectAttributes.addFlashAttribute("error", "Lỗi hệ thống!");
            if (type.getId() == null) {
                return "redirect:/room_types/add";
            } else {
                return "redirect:/room_types/edit/" + type.getId();
            }
        }

        try {
            RoomType t;
            if (type.getId() == null) {
                t = typeService.createOrUpdate(type);
                redirectAttributes.addFlashAttribute("success", "Thêm mới thành công!");
            } else {
                t = typeService.createOrUpdate(type);
                redirectAttributes.addFlashAttribute("success", "Cập nhật thành công!");
            }
            return "redirect:/room_types/edit/" + t.getId();
        } catch (org.hibernate.exception.ConstraintViolationException e) {
            redirectAttributes.addFlashAttribute("error", "Tên người dùng đã tồn tại!");
            return "redirect:/room_types/add";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra, vui lòng thử lại!");
            return "redirect:/room_types/add";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditRoomTypeForm(@PathVariable(value = "id") int id, Model model) {
        RoomType type = typeService.getById(id);
        model.addAttribute("type", type);
        return "room_type_form";
    }

    @DeleteMapping(value = "/delete/{id}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> deleteRoomType(@PathVariable(value = "id") int id) {
        try {
            typeService.delete(id);
            return ResponseEntity.ok().body(Map.of("message", "Xóa loại phòng thành công!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Đã xảy ra lỗi khi xóa loại phòng!"));
        }
    }

    @DeleteMapping(value = "/delete", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> deleteRoomTypes(@RequestBody Map<String, List<Integer>> request) {
        List<Integer> ids = request.get("ids");
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Không có ID nào được chọn để xóa."));
        }

        try {
            typeService.delete(ids);
            return ResponseEntity.ok().body(Map.of("message", "Xóa loại phòng thành công!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Đã xảy ra lỗi khi xóa loại phòng!"));
        }
    }
    
    
}
