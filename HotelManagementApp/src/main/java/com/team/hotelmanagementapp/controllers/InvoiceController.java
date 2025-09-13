package com.team.hotelmanagementapp.controllers;

import com.team.hotelmanagementapp.pojo.Invoice;
import com.team.hotelmanagementapp.pojo.Payment;
import com.team.hotelmanagementapp.services.InvoiceService;
import com.team.hotelmanagementapp.services.PaymentService;
import com.team.hotelmanagementapp.utils.Pagination;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

}
