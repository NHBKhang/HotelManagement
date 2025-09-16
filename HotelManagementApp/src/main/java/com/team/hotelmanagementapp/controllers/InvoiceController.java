package com.team.hotelmanagementapp.controllers;

import com.team.hotelmanagementapp.pojo.Invoice;
import com.team.hotelmanagementapp.pojo.Payment;
import com.team.hotelmanagementapp.services.InvoiceService;
import com.team.hotelmanagementapp.services.PaymentService;
import com.team.hotelmanagementapp.utils.Pagination;
import jakarta.servlet.http.HttpServletRequest;
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
@RequestMapping("/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;
    @Autowired
    private PaymentService paymentService;

    @GetMapping
    public String invoices(Model model, @RequestParam Map<String, String> params,
            RedirectAttributes redirectAttributes) {
        try {
            long totalInvoices = invoiceService.count(params);
            List<Invoice> invoices = invoiceService.find(params);

            model.addAttribute("rows", invoices);
            model.addAttribute("totalInvoices", totalInvoices);
            model.addAttribute("pagination", new Pagination(totalInvoices, params));
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra, vui lòng thử lại!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi tải danh sách!");
        }
        return "payment/invoices";
    }

    @GetMapping("/{id}")
    public String invoiceDetail(@PathVariable(value = "id") int id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Invoice invoice = invoiceService.getById(id);
            if (invoice.getId() == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy hóa đơn với ID: " + id);
                return "redirect:/invoices";
            }
            List<Payment> payments = paymentService.findByInvoice(id, null);

            model.addAttribute("invoice", invoice);
            model.addAttribute("payments", payments);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi tải thông tin!");
            e.printStackTrace();
        }
        return "payment/invoice_detail";
    }
    @GetMapping("/add")
    public String showAddInvoiceForm(Model model) {
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber("INV-" + System.currentTimeMillis());
        
        model.addAttribute("invoice", invoice);
        return "payment/invoice_form";
    }

    @PostMapping("/save")
    public String saveInvoice(@Valid @ModelAttribute("invoice") Invoice invoice, BindingResult result,
            Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("invoice", invoice);
            redirectAttributes.addFlashAttribute("error", "Lỗi hệ thống!");
            if (invoice.getId() == null) {
                return "redirect:/invoices/add";
            } else {
                return "redirect:/invoices/" + invoice.getId();
            }
        }

        try {
            Invoice i;
            if (invoice.getId() == null) {
                i = invoiceService.createOrUpdate(invoice);
                redirectAttributes.addFlashAttribute("success", "Thêm mới thành công!");
            } else {
                i = invoiceService.createOrUpdate(invoice);
                redirectAttributes.addFlashAttribute("success", "Cập nhật thành công!");
            }
            return "redirect:/invoices/" + i.getId();
        } catch (org.hibernate.exception.ConstraintViolationException e) {
            redirectAttributes.addFlashAttribute("error", "Hóa đơn đã tồn tại!");
            return "redirect:/invoices/add";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra, vui lòng thử lại!");
            return "redirect:/payments/add";
        }
    }

    @PostMapping("/payments/bulk-status")
    public String bulkUpdateStatus(@RequestParam("ids") List<Integer> ids,
            @RequestParam("status") Payment.Status status,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        int updated = paymentService.updateStatuses(ids, status);
        redirectAttributes.addFlashAttribute("success",
                String.format("Đã cập nhật trạng thái cho %d giao dịch.", updated));

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/invoices");
    }
}
