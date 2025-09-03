package com.team.hotelmanagementapp.services.impl;

import com.team.hotelmanagementapp.pojo.Invoice;
import com.team.hotelmanagementapp.repositories.InvoiceRepository;
import com.team.hotelmanagementapp.services.InvoiceService;
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
    public List<Invoice> find( Map<String, String> params) {
        return this.invoiceRepository.find(params);
    }

    @Override
    public List<Invoice> findByUserId(int id, Map<String, String> params) {
        return this.invoiceRepository.find(params);
    }

    @Override
    public Invoice createOrUpdate(Invoice payment) {
        return this.invoiceRepository.createOrUpdate(payment);
    }

    @Override
    public Invoice getById(int id) {
        return this.invoiceRepository.getById(id);
    }
    
}
