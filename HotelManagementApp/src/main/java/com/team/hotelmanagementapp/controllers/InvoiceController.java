package com.team.hotelmanagementapp.controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.team.hotelmanagementapp.pojo.Invoice;
import com.team.hotelmanagementapp.pojo.Payment;
import com.team.hotelmanagementapp.services.InvoiceService;
import com.team.hotelmanagementapp.services.PaymentService;
import com.team.hotelmanagementapp.utils.Pagination;

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

    @GetMapping("/{id}/export-pdf")
    public ResponseEntity<byte[]> exportInvoicePdf(@PathVariable(value = "id") int id) {
        try {
            Invoice invoice = invoiceService.getById(id);
            if (invoice.getId() == null) {
                return ResponseEntity.notFound().build();
            }
            
            List<Payment> payments = paymentService.findByInvoice(id, null);
            byte[] pdfBytes = generateInvoicePdf(invoice, payments);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "invoice_" + invoice.getInvoiceNumber() + ".pdf");
            headers.setContentLength(pdfBytes.length);
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private byte[] generateInvoicePdf(Invoice invoice, List<Payment> payments) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        try {
            PdfWriter.getInstance(document, out);
            document.open();

            BaseFont baseFont = BaseFont.createFont("/static/fonts/arial.ttf",
                    BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font titleFont = new Font(baseFont, 18, Font.BOLD);
            Font headerFont = new Font(baseFont, 14, Font.BOLD);
            Font normalFont = new Font(baseFont, 12, Font.NORMAL);
            Font smallFont = new Font(baseFont, 10, Font.NORMAL);

            // Company Header
            Paragraph companyName = new Paragraph("KHÁCH SẠN ABC", titleFont);
            companyName.setAlignment(Element.ALIGN_CENTER);
            document.add(companyName);

            Paragraph companyInfo = new Paragraph("Địa chỉ: xxx Đường ABC, Quận 1, TP.HCM", normalFont);
            companyInfo.setAlignment(Element.ALIGN_CENTER);
            document.add(companyInfo);

            Paragraph contactInfo = new Paragraph("Điện thoại: 0xxxxxxxx | Email: info@abc.com", normalFont);
            contactInfo.setAlignment(Element.ALIGN_CENTER);
            document.add(contactInfo);

            document.add(new Paragraph(" ")); // Empty line

            // Invoice Title
            Paragraph invoiceTitle = new Paragraph("HÓA ĐƠN THANH TOÁN", titleFont);
            invoiceTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(invoiceTitle);

            document.add(new Paragraph(" ")); // Empty line

            // Invoice Info
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            DecimalFormat currencyFormat = new DecimalFormat("#,###");
            
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[]{1, 1});
            
            // Left column
            PdfPCell leftCell = new PdfPCell();
            leftCell.setBorder(PdfPCell.NO_BORDER);
            leftCell.addElement(new Paragraph("Số hóa đơn: " + invoice.getInvoiceNumber(), normalFont));
            leftCell.addElement(new Paragraph("Ngày tạo: " + invoice.getIssueAt().format(dateTimeFormatter), normalFont));
            leftCell.addElement(new Paragraph("Mã booking: " + invoice.getBooking().getCode(), normalFont));
            infoTable.addCell(leftCell);

            // Right column
            PdfPCell rightCell = new PdfPCell();
            rightCell.setBorder(PdfPCell.NO_BORDER);
            rightCell.addElement(new Paragraph("Trạng thái: " + invoice.getStatus().getLabel(), normalFont));
            rightCell.addElement(new Paragraph("Email: " + (invoice.getSentToEmail() != null ? invoice.getSentToEmail() : "N/A"), normalFont));
            infoTable.addCell(rightCell);

            document.add(infoTable);
            document.add(new Paragraph(" ")); // Empty line

            // Customer Info
            Paragraph customerTitle = new Paragraph("THÔNG TIN KHÁCH HÀNG", headerFont);
            document.add(customerTitle);

            PdfPTable customerTable = new PdfPTable(2);
            customerTable.setWidthPercentage(100);
            customerTable.setWidths(new float[]{1, 1});

            PdfPCell customerLeftCell = new PdfPCell();
            customerLeftCell.setBorder(PdfPCell.NO_BORDER);
            customerLeftCell.addElement(new Paragraph("Họ tên: " + invoice.getBooking().getUser().getFullName(), normalFont));
            customerLeftCell.addElement(new Paragraph("Email: " + invoice.getBooking().getUser().getEmail(), normalFont));
            customerTable.addCell(customerLeftCell);

            PdfPCell customerRightCell = new PdfPCell();
            customerRightCell.setBorder(PdfPCell.NO_BORDER);
            customerRightCell.addElement(new Paragraph("Số điện thoại: " +
                    (invoice.getBooking().getUser().getPhone() != null ? invoice.getBooking().getUser().getPhone() : "N/A"), normalFont));
            customerRightCell.addElement(new Paragraph("Username: " + invoice.getBooking().getUser().getUsername(), normalFont));
            customerTable.addCell(customerRightCell);

            document.add(customerTable);
            document.add(new Paragraph(" ")); // Empty line

            // Booking Details
            Paragraph bookingTitle = new Paragraph("CHI TIẾT BOOKING", headerFont);
            document.add(bookingTitle);

            PdfPTable bookingTable = new PdfPTable(2);
            bookingTable.setWidthPercentage(100);
            bookingTable.setWidths(new float[]{1, 1});

            PdfPCell bookingLeftCell = new PdfPCell();
            bookingLeftCell.setBorder(PdfPCell.NO_BORDER);
            bookingLeftCell.addElement(new Paragraph("Phòng: " + invoice.getBooking().getRoom().getRoomNumber(), normalFont));
            bookingLeftCell.addElement(new Paragraph("Loại phòng: " + invoice.getBooking().getRoom().getRoomType().getName(), normalFont));
            bookingTable.addCell(bookingLeftCell);

            PdfPCell bookingRightCell = new PdfPCell();
            bookingRightCell.setBorder(PdfPCell.NO_BORDER);
            bookingRightCell.addElement(new Paragraph("Check-in: " + invoice.getBooking().getCheckInDate().toString(), normalFont));
            bookingRightCell.addElement(new Paragraph("Check-out: " + invoice.getBooking().getCheckOutDate().toString(), normalFont));
            bookingTable.addCell(bookingRightCell);

            document.add(bookingTable);
            document.add(new Paragraph(" ")); // Empty line

            // Payment History
            if (!payments.isEmpty()) {
                Paragraph paymentTitle = new Paragraph("LỊCH SỬ THANH TOÁN", headerFont);
                document.add(paymentTitle);

                PdfPTable paymentTable = new PdfPTable(4);
                paymentTable.setWidthPercentage(100);
                paymentTable.setWidths(new float[]{2, 2, 2, 2});

                // Headers
                PdfPCell headerCell1 = new PdfPCell(new Phrase("Mã giao dịch", headerFont));
                headerCell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
                headerCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                paymentTable.addCell(headerCell1);

                PdfPCell headerCell2 = new PdfPCell(new Phrase("Số tiền", headerFont));
                headerCell2.setBackgroundColor(BaseColor.LIGHT_GRAY);
                headerCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
                paymentTable.addCell(headerCell2);

                PdfPCell headerCell3 = new PdfPCell(new Phrase("Phương thức", headerFont));
                headerCell3.setBackgroundColor(BaseColor.LIGHT_GRAY);
                headerCell3.setHorizontalAlignment(Element.ALIGN_CENTER);
                paymentTable.addCell(headerCell3);

                PdfPCell headerCell4 = new PdfPCell(new Phrase("Thời gian", headerFont));
                headerCell4.setBackgroundColor(BaseColor.LIGHT_GRAY);
                headerCell4.setHorizontalAlignment(Element.ALIGN_CENTER);
                paymentTable.addCell(headerCell4);

                // Payment rows
                for (Payment payment : payments) {
                    paymentTable.addCell(new Phrase(payment.getCode(), normalFont));
                    paymentTable.addCell(new Phrase(currencyFormat.format(payment.getAmount()) + " đ", normalFont));
                    paymentTable.addCell(new Phrase(payment.getMethod().toString(), normalFont));
                    paymentTable.addCell(new Phrase(payment.getCreatedAt().format(dateTimeFormatter), normalFont));
                }

                document.add(paymentTable);
                document.add(new Paragraph(" ")); // Empty line
            }

            // Total Summary
            Paragraph totalTitle = new Paragraph("TỔNG KẾT", headerFont);
            document.add(totalTitle);

            PdfPTable totalTable = new PdfPTable(2);
            totalTable.setWidthPercentage(100);
            totalTable.setWidths(new float[]{3, 1});

            PdfPCell totalLabelCell = new PdfPCell(new Phrase("Tổng tiền hóa đơn:", headerFont));
            totalLabelCell.setBorder(PdfPCell.NO_BORDER);
            totalLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalTable.addCell(totalLabelCell);

            PdfPCell totalValueCell = new PdfPCell(new Phrase(currencyFormat.format(invoice.getTotalAmount()) + " đ", headerFont));
            totalValueCell.setBorder(PdfPCell.NO_BORDER);
            totalValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalTable.addCell(totalValueCell);

            // Balance
            if (invoice.getBalance() != 0) {
                String balanceLabel = invoice.getBalance() > 0 ? "Còn thiếu:" : "Trả dư:";
                PdfPCell balanceLabelCell = new PdfPCell(new Phrase(balanceLabel, normalFont));
                balanceLabelCell.setBorder(PdfPCell.NO_BORDER);
                balanceLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                totalTable.addCell(balanceLabelCell);

                PdfPCell balanceValueCell = new PdfPCell(new Phrase(currencyFormat.format(Math.abs(invoice.getBalance())) + " đ", normalFont));
                balanceValueCell.setBorder(PdfPCell.NO_BORDER);
                balanceValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                totalTable.addCell(balanceValueCell);
            } else {
                PdfPCell paidLabelCell = new PdfPCell(new Phrase("Trạng thái:", normalFont));
                paidLabelCell.setBorder(PdfPCell.NO_BORDER);
                paidLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                totalTable.addCell(paidLabelCell);

                PdfPCell paidValueCell = new PdfPCell(new Phrase("Đã thanh toán đủ", normalFont));
                paidValueCell.setBorder(PdfPCell.NO_BORDER);
                paidValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                totalTable.addCell(paidValueCell);
            }

            document.add(totalTable);
            document.add(new Paragraph(" ")); // Empty line

            // Footer
            Paragraph footer = new Paragraph("Cảm ơn quý khách đã sử dụng dịch vụ của chúng tôi!", smallFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            Paragraph printDate = new Paragraph("Ngày in: " + LocalDateTime.now().format(dateTimeFormatter), smallFont);
            printDate.setAlignment(Element.ALIGN_RIGHT);
            document.add(printDate);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
        
        return out.toByteArray();
    }

}
