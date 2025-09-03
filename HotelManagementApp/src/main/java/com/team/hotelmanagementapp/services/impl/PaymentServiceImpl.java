package com.team.hotelmanagementapp.services.impl;

import com.team.hotelmanagementapp.pojo.Booking;
import com.team.hotelmanagementapp.pojo.Payment;
import com.team.hotelmanagementapp.repositories.PaymentRepository;
import com.team.hotelmanagementapp.services.BookingService;
import com.team.hotelmanagementapp.services.PaymentService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private BookingService bookingService;

    @Override
    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    @Override
    public List<Payment> findByUserId(int id, Map<String, String> params) {
        return this.paymentRepository.findByUserId(id, params);
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
    public Payment createByRequest(Map<String, Object> bodyData, String username,
            Payment.Method method) {
        try {
            Payment payment = new Payment();
            if (method == Payment.Method.VNPAY) {
                if ("room".equals(bodyData.get("itemType").toString())) {
                    Booking b = this.bookingService.createByIdAndUsername(
                            Integer.parseInt(bodyData.get("bookingId").toString()), username, method);
                    payment.setBooking(b);
                }

                payment.setAmount(Double.parseDouble(bodyData.get("amount").toString()) / 100);
                payment.setStatus(Payment.Status.SUCCESS);
                payment.setCode(this.paymentRepository.generateCode());
                payment.setMethod(method);
                payment.setBankCode(bodyData.get("bankCode").toString());
                payment.setTransactionNo(bodyData.get("transactionNo").toString());
                payment.setDescription("Đã chuyển khoản vào ngày "
                        + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

                return this.createOrUpdate(payment);
            } else {
                return null;
            }
        } catch (Exception e) {
            System.out.print(e);
            return null;
        }
    }

    @Override
    public long countByUserId(int id, Map<String, String> params) {
        return this.paymentRepository.countByUserId(id, params);
    }
}
