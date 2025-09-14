package com.team.hotelmanagementapp.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.team.hotelmanagementapp.pojo.Booking;
import com.team.hotelmanagementapp.pojo.Invoice;
import com.team.hotelmanagementapp.pojo.Payment;
import com.team.hotelmanagementapp.repositories.PaymentRepository;
import com.team.hotelmanagementapp.services.BookingService;
import com.team.hotelmanagementapp.services.InvoiceService;
import com.team.hotelmanagementapp.services.PaymentService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private InvoiceService invoiceService;
    @Autowired
    private Cloudinary cloudinary;

    @Override
    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    @Override
    public List<Payment> findByUserId(int id, Map<String, String> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put("userId", String.valueOf(id));
        return this.paymentRepository.find(params);
    }

    @Override
    public List<Payment> findByInvoice(int id, Map<String, String> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put("invoiceId", String.valueOf(id));
        return this.paymentRepository.find(params);
    }

    @Override
    public List<Payment> find(Map<String, String> params) {
        return this.paymentRepository.find(params);
    }

    @Override
    public Payment createOrUpdate(Payment payment) {
        if (payment.getStatus() == null || payment.getStatus().getLabel().isEmpty()) {
            payment.setStatus(Payment.Status.PENDING);
        }

        return this.paymentRepository.createOrUpdate(payment);
    }

    @Override
    public Payment getById(int id) {
        return this.paymentRepository.getById(id);
    }

    @Override
    public String createRandomCode(int len) {
        Random rnd = new Random();
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return "P" + sb.toString();
    }

    @Override
    public Payment createByRequest(Map<String, Object> bodyData, String username, Payment.Method method) {
        try {
            Payment payment = new Payment();

            if (method == Payment.Method.VNPAY) {
                Double amount = Double.parseDouble(bodyData.get("amount").toString()) / 100;

                if ("room".equals(bodyData.get("itemType").toString())) {
                    int bookingId = Integer.parseInt(bodyData.get("bookingId").toString());
                    Booking booking = this.bookingService.createByIdAndUsername(bookingId, username, method);

                    Invoice invoice = new Invoice(
                            booking, "INV-" + System.currentTimeMillis(),
                            booking.getUser().getEmail(),
                            amount, Invoice.Status.PAID, LocalDateTime.now());

                    payment.setInvoice(invoiceService.createOrUpdate(invoice));
                }

                payment.setAmount(amount);
                payment.setStatus(Payment.Status.SUCCESS);
                payment.setCode(this.paymentRepository.generateCode());
                payment.setMethod(method);
                payment.setBankCode(bodyData.get("bankCode").toString());
                payment.setTransactionNo(
                        method.toString() + "-" + bodyData.get("transactionNo").toString());
                payment.setDescription("Đã chuyển khoản vào ngày "
                        + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

                payment = this.createOrUpdate(payment);
            } else if (method == Payment.Method.TRANSFER) {
                MultipartFile file = (MultipartFile) bodyData.get("file");
                if (file != null) {
                    String contentType = file.getContentType();
                    String resourceType = "auto";

                    String publicId = "receipt_" + UUID.randomUUID();
                    if ("application/pdf".equalsIgnoreCase(contentType)) {
                        publicId += ".pdf";
                        resourceType = "raw";
                    }

                    Map res = cloudinary.uploader().upload(file.getBytes(),
                            ObjectUtils.asMap(
                                    "resource_type", resourceType,
                                    "public_id", publicId
                            ));
                    payment.setReceiptImage(res.get("secure_url").toString());
                }
                
                Double amount = Double.valueOf(bodyData.get("amount").toString());

                if ("room".equals(bodyData.get("itemType").toString())) {
                    int bookingId = Integer.parseInt(bodyData.get("bookingId").toString());
                    Booking booking = this.bookingService.createByIdAndUsername(bookingId, username, method);

                    Invoice invoice = new Invoice(
                            booking, "INV-" + System.currentTimeMillis(),
                            booking.getUser().getEmail(),
                            amount, Invoice.Status.UNPAID, LocalDateTime.now());

                    payment.setInvoice(invoiceService.createOrUpdate(invoice));
                }

                payment.setAmount(amount);
                payment.setStatus(Payment.Status.PENDING);
                payment.setCode(this.paymentRepository.generateCode());
                payment.setTransactionNo(
                        method.toString() + "-" + System.currentTimeMillis());
                payment.setMethod(method);
                payment.setDescription("Đã chuyển khoản vào ngày "
                        + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

                payment = this.createOrUpdate(payment);
            }

            return payment;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public long countByUserId(int id, Map<String, String> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put("userId", String.valueOf(id));
        return this.paymentRepository.count(params);
    }

    @Override
    public long count(Map<String, String> params) {
        return this.paymentRepository.count(params);
    }
}
