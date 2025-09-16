package com.team.hotelmanagementapp.controllers.api;

import com.team.hotelmanagementapp.services.InvoiceService;
import com.team.hotelmanagementapp.utils.Pagination;
import jakarta.servlet.http.HttpServletRequest;
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

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin
public class ApiInvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @GetMapping
    public ResponseEntity<?> list(@RequestParam Map<String, String> params) {
        try {
            return ResponseEntity.ok().body(new Pagination<>(
                    invoiceService.find(params), invoiceService.count(params), params));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Có lỗi xảy ra khi tải danh sách người dùng!");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> retrieve(@PathVariable("id") int id,
            HttpServletRequest request) {
        try {
            return new ResponseEntity<>(this.invoiceService.getById(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Lỗi tạo đơn đặt phòng: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
}
