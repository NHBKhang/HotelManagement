package com.team.hotelmanagementapp.services.impl;

import com.team.hotelmanagementapp.pojo.Payment;
import com.team.hotelmanagementapp.repositories.PaymentRepository;
import com.team.hotelmanagementapp.services.PaymentService;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

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
    public Payment createPayment(Map<String, Object> bodyData, String username, 
            Payment.Method method) {
        try {
            Payment p = new Payment();
            return this.createOrUpdate(p);
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
