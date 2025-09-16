package com.team.hotelmanagementapp.controllers;

import com.team.hotelmanagementapp.pojo.Payment;
import com.team.hotelmanagementapp.services.PaymentService;
import com.team.hotelmanagementapp.utils.Pagination;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@ControllerAdvice
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping
    public String payments(Model model, @RequestParam Map<String, String> params,
            RedirectAttributes redirectAttributes) {
        try {
            long totalPayments = paymentService.count(params);
            List<Payment> payments = paymentService.find(params);

            model.addAttribute("rows", payments);
            model.addAttribute("totalPayments", totalPayments);
            model.addAttribute("pagination", new Pagination(totalPayments, params));
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra, vui lòng thử lại!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi tải danh sách!");
        }
        return "payment/payments";
    }
    
    @GetMapping("/{id}")
    public String paymentDetail(@PathVariable(value = "id") int id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Payment payment = paymentService.getById(id);
            if (payment.getId() == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy giao dịch với ID: " + id);
                return "redirect:/payments";
            }

            model.addAttribute("payment", payment);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi tải thông tin!");
            e.printStackTrace();
        }
        return "payment/payment_detail";
    }
    
    @GetMapping("/add")
    public String showAddPaymentForm(Model model) {
        Payment payment = new Payment();
        model.addAttribute("payment", payment);
        return "payment/payment_form";
    }

    @PostMapping("/save")
    public String savePayment(@Valid @ModelAttribute("payment") Payment payment, BindingResult result,
            Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("payment", payment);
            redirectAttributes.addFlashAttribute("error", "Lỗi hệ thống!");
            if (payment.getId() == null) {
                return "redirect:/payments/add";
            } else {
                return "redirect:/payments/" + payment.getId();
            }
        }

        try {
            Payment p;
            if (payment.getId() == null) {
                p = paymentService.createOrUpdate(payment);
                redirectAttributes.addFlashAttribute("success", "Thêm mới thành công!");
            } else {
                p = paymentService.createOrUpdate(payment);
                redirectAttributes.addFlashAttribute("success", "Cập nhật thành công!");
            }
            return "redirect:/payments/" + p.getId();
        } catch (org.hibernate.exception.ConstraintViolationException e) {
            redirectAttributes.addFlashAttribute("error", "=Giao dịch đã tồn tại!");
            return "redirect:/payments/add";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra, vui lòng thử lại!");
            return "redirect:/payments/add";
        }
    }
    
    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable(value = "id") int id,
            @RequestParam(value = "status") Payment.Status status,
            RedirectAttributes redirectAttributes) {
        try {
            Payment payment = paymentService.getById(id);
            if (payment == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy giao dịch với ID: " + id);
                return "redirect:/payments";
            }
            
            if (!isValidStatusTransition(payment.getStatus(), status)) {
                redirectAttributes.addFlashAttribute("error", 
                    "Không thể chuyển trạng thái từ " + payment.getStatus().getLabel() + 
                    " sang " + status.getLabel());
                return "redirect:/payments/" + id;
            }
            
            payment.setStatus(status);
            paymentService.createOrUpdate(payment);
            
            redirectAttributes.addFlashAttribute("success", 
                "Cập nhật trạng thái giao dịch thành công!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Có lỗi xảy ra khi cập nhật trạng thái: " + e.getMessage());
            e.printStackTrace();
        }
        
        return "redirect:/payments/" + id;
    }

    private boolean isValidStatusTransition(Payment.Status currentStatus, Payment.Status newStatus) {
        if (currentStatus == newStatus) {
            return true;
        }
        
        return switch (currentStatus) {
            case PENDING -> 
                newStatus == Payment.Status.SUCCESS || 
                newStatus == Payment.Status.CANCELLED ||
                newStatus == Payment.Status.FAILED ||
                newStatus == Payment.Status.REFUNDED;
            case CANCELLED, FAILED -> 
                newStatus == Payment.Status.SUCCESS || 
                newStatus == Payment.Status.PENDING;
            case SUCCESS -> 
                newStatus == Payment.Status.PENDING;
            case REFUNDED -> false;
            default -> false;
        };
    }
}
