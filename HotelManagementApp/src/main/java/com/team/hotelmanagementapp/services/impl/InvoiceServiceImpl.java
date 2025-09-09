package com.team.hotelmanagementapp.services.impl;

import com.team.hotelmanagementapp.pojo.Invoice;
import com.team.hotelmanagementapp.pojo.Payment;
import com.team.hotelmanagementapp.repositories.InvoiceRepository;
import com.team.hotelmanagementapp.services.InvoiceService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Override
    public List<Invoice> findAll() {
        return invoiceRepository.findAll();
    }

    @Override
    public List<Invoice> find(Map<String, String> params) {
        return this.invoiceRepository.find(params);
    }

    @Override
    public List<Invoice> findByUserId(int id, Map<String, String> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put("userId", String.valueOf(id));
        return this.invoiceRepository.find(params);
    }

    @Override
    public List<Invoice> findByBookingId(Integer id, Map<String, String> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put("bookingId", String.valueOf(id));
        return this.invoiceRepository.find(params);
    }

    @Override
    public Invoice createOrUpdate(Invoice invoice) {
        if (invoice.getStatus() == null) {
            invoice.setStatus(Invoice.Status.UNPAID);
        }

        return this.invoiceRepository.createOrUpdate(invoice);
    }

    @Override
    public Invoice getById(int id) {
        return this.invoiceRepository.getById(id);
    }

    @Override
    public long count(Map<String, String> params) {
        return this.invoiceRepository.count(params);
    }

}
