package com.team.hotelmanagementapp.controllers.api;

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
import com.team.hotelmanagementapp.components.JwtService;
import com.team.hotelmanagementapp.pojo.Booking;
import com.team.hotelmanagementapp.pojo.Feedback;
import com.team.hotelmanagementapp.pojo.Invoice;
import com.team.hotelmanagementapp.pojo.ServiceBooking;
import com.team.hotelmanagementapp.services.BookingService;
import com.team.hotelmanagementapp.services.FeedbackService;
import com.team.hotelmanagementapp.services.InvoiceService;
import com.team.hotelmanagementapp.services.RoomService;
import com.team.hotelmanagementapp.services.ServiceBookingService;
import com.team.hotelmanagementapp.services.UserService;
import com.team.hotelmanagementapp.utils.Pagination;
import com.team.hotelmanagementapp.utils.RequestValidation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin
public class ApiBookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private InvoiceService invoiceService;
    
    @Autowired
    private ServiceBookingService serviceBookingService;

    @GetMapping
    public ResponseEntity<?> list(@RequestParam Map<String, String> params) {
        try {
            return ResponseEntity.ok().body(new Pagination<>(
                    bookingService.find(params), bookingService.countBookings(params), params));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Có lỗi xảy ra khi tải danh sách người dùng!");
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, ?> bodyData,
            HttpServletRequest request) {
        try {
            RequestValidation val = RequestValidation.getUserFromRequest(request, userService, jwtService);
            if (val.getUser() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(val.getMessage());
            }

            Booking b = new Booking();
            b.setCheckInDate(LocalDate.parse((String) bodyData.get("checkin")));
            b.setCheckOutDate(LocalDate.parse((String) bodyData.get("checkout")));
            b.setGuests(Integer.valueOf(bodyData.get("guests").toString()));
            b.setStatus(Booking.Status.PROCESSING);
            b.setUser(val.getUser());
            b.setRoom(this.roomService.getById(Integer.parseInt(bodyData.get("roomId").toString())));

            List<Map<String, Object>> services = (List<Map<String, Object>>) bodyData.get("services");
            b = this.bookingService.createOrUpdate(b);
            if (services != null) {
                b.setServices(serviceBookingService.createMulti(b, services));
            }

            return new ResponseEntity<>(b, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Lỗi tạo đơn đặt phòng: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<?> getMyBookings(HttpServletRequest request,
            @RequestParam Map<String, String> params) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Thiếu token xác thực");
            }

            String username = jwtService.getUsernameFromToken(token);
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token không hợp lệ");
            }

            List<Booking> bookings = this.bookingService.findByUsername(params, username);
            Long totalBookings = this.bookingService.countBookingsByUsername(params, username);

            return new ResponseEntity<>(new Pagination<>(bookings, totalBookings, params), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Lỗi lấy đơn đặt phòng: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/my-bookings/{id}")
    public ResponseEntity<?> retrieveMyBooking(@PathVariable("id") int id, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Thiếu token xác thực");
            }

            String username = jwtService.getUsernameFromToken(token);
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token không hợp lệ");
            }

            Booking booking = bookingService.getById(id);
            if (booking != null || username.equals(booking.getUser().getUsername())) {
                return new ResponseEntity<>(booking, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Lỗi lấy đơn đặt phòng: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/my-bookings/{id}/feedbacks")
    public ResponseEntity<?> getMyFeedbacks(@RequestParam Map<String, String> params,
            HttpServletRequest request, @PathVariable("id") int id) {
        try {
            RequestValidation val = RequestValidation.getUserFromRequest(request, userService, jwtService);

            if (val.getUser() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(val.getMessage());
            }

            List<Feedback> feedbacks = feedbackService.findByBooking(id, params);
            long totalFeedbacks = feedbackService.countFeedback(params);
            return ResponseEntity.ok(new Pagination<>(feedbacks, totalFeedbacks, params));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Có lỗi xảy ra khi tải phản hồi của bạn!"));
        }
    }

    @PostMapping("/my-bookings/{id}/feedbacks")
    public ResponseEntity<?> postMyFeedback(
            @PathVariable("id") int bookingId,
            @RequestBody Map<String, String> bodyData,
            HttpServletRequest request) {
        try {
            RequestValidation val = RequestValidation.getUserFromRequest(request, userService, jwtService);

            if (val.getUser() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(val.getMessage());
            }

            Booking booking = bookingService.getById(bookingId);
            if (booking == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy đơn đặt phòng");
            }
            String comment = bodyData.getOrDefault("comment", "").trim();
            String ratingStr = bodyData.getOrDefault("rating", "0");
            Double rating = 0.0;
            try {
                rating = Double.valueOf(ratingStr);
            } catch (NumberFormatException ignored) {
            }

            Feedback feedback = new Feedback();
            feedback.setComment(comment);
            feedback.setRating(rating);
            feedback.setUser(val.getUser());
            feedback.setBooking(booking);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    feedbackService.createOrUpdate(feedback));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Có lỗi xảy ra khi lưu phản hồi của bạn!"));
        }
    }

    @GetMapping("/my-bookings/{id}/export-invoice")
    public void exportInvoice(@PathVariable("id") Integer id,
            HttpServletResponse response,
            HttpServletRequest request) throws IOException {
        try {
            RequestValidation val = RequestValidation.getUserFromRequest(request, userService, jwtService);

            if (val.getUser() == null) {
                response.sendError(HttpStatus.UNAUTHORIZED.value(), val.getMessage());
            }

            Booking booking = bookingService.getById(id);
            if (!booking.getUser().getId().equals(val.getUser().getId())) {
                response.sendError(HttpStatus.FORBIDDEN.value(), "Bạn không có quyền xem hóa đơn này");
                return;
            }

            List<Invoice> invoices = invoiceService.findByBookingId(id, null);
            if (invoices.isEmpty()) {
                response.sendError(HttpStatus.NOT_FOUND.value(), "Không tìm thấy hóa đơn");
                return;
            }

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=invoice-" + id + ".pdf");

            InputStream fontStream = getClass().getResourceAsStream("/static/fonts/arial.ttf");
            if (fontStream == null) {
                throw new RuntimeException("Không tìm thấy font Arial!");
            }

            byte[] fontBytes = fontStream.readAllBytes();
            BaseFont bf = BaseFont.createFont(
                    "arial.ttf",
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED,
                    true,
                    fontBytes,
                    null
            );
            Font font = new Font(bf, 12, Font.NORMAL);

            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, response.getOutputStream());

            document.open();
            document.add(new Paragraph("HÓA ĐƠN THANH TOÁN", new Font(bf, 16, Font.BOLD)));
            document.add(new Paragraph("Khách hàng: " + booking.getUser().getFullName(), font));
            document.add(new Paragraph("Phòng: " + booking.getRoom().getRoomNumber(), font));
            document.add(new Paragraph("Ngày nhận: " + booking.getCheckInDate(), font));
            document.add(new Paragraph("Ngày trả: " + booking.getCheckOutDate(), font));
            document.add(new Paragraph("------------------------------------------------------------", font));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2, 3, 3, 3});

            Stream.of("Mã HĐ", "Ngày lập", "Trạng thái", "Số tiền (VND)")
                    .forEach(headerTitle -> {
                        PdfPCell header = new PdfPCell();
                        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        header.setPhrase(new Phrase(headerTitle, font));
                        header.setHorizontalAlignment(Element.ALIGN_CENTER);
                        table.addCell(header);
                    });

            double total = 0;

            for (Invoice invoice : invoices) {
                table.addCell(new Phrase(invoice.getInvoiceNumber(), font));
                table.addCell(new Phrase(invoice.getIssueAt() != null ? invoice.getIssueAt().toString() : "-", font));
                table.addCell(new Phrase(invoice.getStatus().getLabel(), font));

                double amount = booking.getRoom().getRoomType().getPricePerNight(); // hoặc lấy từ Payment nếu có
                total += amount;
                table.addCell(new Phrase(String.format("%,.0f", amount), font));
            }

            document.add(table);

            document.add(new Paragraph("------------------------------------------------------------", font));
            document.add(new Paragraph("TỔNG CỘNG: " + String.format("%,.0f", total) + " VND", new Font(bf, 14, Font.BOLD)));

            document.close();

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Có lỗi xảy ra khi xuất hóa đơn của bạn");
        }
    }

    @PutMapping("/my-bookings/{id}/cancel")
    public ResponseEntity<Map<String, Object>> cancelBooking(@PathVariable int id) {
        try {
            bookingService.cancelBooking(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Booking cancelled successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to cancel booking");
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
