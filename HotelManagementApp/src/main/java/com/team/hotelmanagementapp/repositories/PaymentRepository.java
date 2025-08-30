package com.team.hotelmanagementapp.repositories;

import com.team.hotelmanagementapp.pojo.Payment;
import java.util.List;
import java.util.Map;

public interface PaymentRepository {

    List<Payment> findByUserId(int id, Map<String, String> params);

    Payment createOrUpdate(Payment payment);

    Payment getById(int id);

    long countByUserId(int id, Map<String, String> params);

    String generateCode();

}
