package com.team.hotelmanagementapp.controllers.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin
public class ApiInvoiceController {
//    @Autowired
//    private InvoiceService invoiceService;
//
//    @PostMapping("/booking/{bookingId}")
//    public ResponseEntity<Invoice> createInvoice(@PathVariable Integer bookingId) {
//        Invoice invoice = invoiceService.generateInvoice(bookingId);
//        return ResponseEntity.ok(invoice);
//    }
//
//    @GetMapping("/{id}/download")
//    public void downloadInvoice(@PathVariable Integer id, HttpServletResponse response) throws IOException {
//        byte[] pdfBytes = invoiceService.generateInvoicePdf(id);
//
//        response.setContentType("application/pdf");
//        response.setHeader("Content-Disposition", "attachment; filename=invoice-" + id + ".pdf");
//        response.getOutputStream().write(pdfBytes);
//    }
}
