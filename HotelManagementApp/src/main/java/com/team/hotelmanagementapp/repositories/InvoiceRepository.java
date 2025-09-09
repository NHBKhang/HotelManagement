package com.team.hotelmanagementapp.repositories;

import com.team.hotelmanagementapp.pojo.Invoice;
import java.util.List;
import java.util.Map;

public interface InvoiceRepository {

    List<Invoice> findAll();

    List<Invoice> find(Map<String, String> params);

    Invoice createOrUpdate(Invoice invoice);

    Invoice getById(int id);

    long countInvoices(Map<String, String> params);

    long count(Map<String, String> params);
    
}
