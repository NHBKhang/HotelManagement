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
import com.team.hotelmanagementapp.pojo.*;
import com.team.hotelmanagementapp.services.*;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    @Autowired
    private PaymentService paymentService;

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

    @GetMapping("/{id}")
    public ResponseEntity<?> retrieve(@PathVariable("id") int id,
            HttpServletRequest request) {
        try {
            return new ResponseEntity<>(this.bookingService.getById(id), HttpStatus.OK);
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
    public ResponseEntity<byte[]> exportInvoice(@PathVariable("id") Integer id,
            HttpServletResponse response,
            HttpServletRequest request) throws IOException {
        try {
            RequestValidation val = RequestValidation.getUserFromRequest(request, userService, jwtService);

            if (val.getUser() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).build();
            }

            Booking booking = bookingService.getById(id);
            if (!booking.getUser().getId().equals(val.getUser().getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).build();
            }

            List<Invoice> invoices = invoiceService.findByBookingId(id, null);
            if (invoices.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).build();
            }
            List<Payment> payments = paymentService.findByInvoice(invoices.get(0).getId(), null);

            byte[] pdfBytes = invoiceService.generateInvoicePdf(invoices.get(0), payments);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "invoice_" + invoices.get(0).getInvoiceNumber() + ".pdf");
            headers.setContentLength(pdfBytes.length);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).build();
        }
    }

    @PutMapping("/my-bookings/{id}/cancel")
    public ResponseEntity<Map<String, Object>> cancelBooking(@PathVariable("id") int id) {
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
