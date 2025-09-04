package com.team.hotelmanagementapp.services;

import com.team.hotelmanagementapp.pojo.Invoice;
import java.util.List;
import java.util.Map;

public interface InvoiceService {
    
    List<Invoice> findAll();
    
    List<Invoice> find( Map<String, String> params);

    List<Invoice> findByUserId(int id, Map<String, String> params);

    List<Invoice> findByBookingId(Integer id, Map<String, String> params);

    Invoice createOrUpdate(Invoice invoice);
    
    Invoice getById(int id);
    
}
