package com.team.hotelmanagementapp.services.impl;

import com.team.hotelmanagementapp.pojo.Invoice;
import com.team.hotelmanagementapp.pojo.Payment;
import com.team.hotelmanagementapp.repositories.InvoiceRepository;
import com.team.hotelmanagementapp.services.StatsService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatsServiceImpl implements StatsService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Override
    public double getTotalRevenue() {
        List<Invoice> invoices = invoiceRepository.findAll();
        return invoices.stream()
                .filter(inv -> inv.getStatus() == Invoice.Status.PAID)
                .mapToDouble(inv -> inv.getBooking().getRoom().getRoomType().getPricePerNight())
                .sum();
    }

    @Override
    public Map<String, Double> getRevenueByMonth() {
        List<Invoice> invoices = invoiceRepository.findAll();
        return invoices.stream()
                .filter(inv -> inv.getStatus() == Invoice.Status.PAID && inv.getIssueAt() != null)
                .collect(Collectors.groupingBy(
                        inv -> inv.getIssueAt().getMonthValue() + "-" + inv.getIssueAt().getYear(),
                        Collectors.summingDouble(inv -> inv.getBooking().getRoom().getRoomType().getPricePerNight())
                ));
    }

    @Override
    public Map<String, Double> getRevenueByPaymentMethod() {
        List<Invoice> invoices = invoiceRepository.findAll();
        return invoices.stream()
                .filter(inv -> inv.getStatus() == Invoice.Status.PAID && inv.getPayments() != null)
                .flatMap(inv -> inv.getPayments().stream()
                .filter(p -> p.getStatus() == Payment.Status.SUCCESS)
                .map(p -> Map.entry(p.getMethod().name(), p.getAmount()))
                )
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.summingDouble(Map.Entry::getValue)
                ));
    }

}
