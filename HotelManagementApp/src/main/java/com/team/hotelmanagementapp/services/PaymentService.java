package com.team.hotelmanagementapp.services;

import com.team.hotelmanagementapp.pojo.Payment;
import java.util.List;
import java.util.Map;

public interface PaymentService {
    
    List<Payment> findAll();

    List<Payment> findByUserId(int id, Map<String, String> params);

    Payment createOrUpdate(Payment payment);
    
    Payment getById(int id);

    String createRandomCode(int len);

    Payment createByRequest(Map<String, Object> bodyData, String username, Payment.Method method);
    
    long countByUserId(int id, Map<String, String> params);

}
