package com.team.hotelmanagementapp.services.impl;

import com.team.hotelmanagementapp.pojo.Booking;
import com.team.hotelmanagementapp.pojo.Invoice;
import com.team.hotelmanagementapp.repositories.BookingRepository;
import com.team.hotelmanagementapp.repositories.InvoiceRepository;
import com.team.hotelmanagementapp.repositories.RoomRepository;
import com.team.hotelmanagementapp.repositories.ServiceBookingRepository;
import com.team.hotelmanagementapp.repositories.UserRepository;
import com.team.hotelmanagementapp.services.StatsService;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatsServiceImpl implements StatsService {

    @Autowired
    private InvoiceRepository invoiceRepo;

    @Autowired
    private BookingRepository bookingRepo;

    @Autowired
    private RoomRepository roomRepo;

    @Autowired
    private ServiceBookingRepository serviceBookingRepo;

    @Autowired
    private UserRepository userRepo;

    private Stream<Invoice> getPaidInvoices() {
        return invoiceRepo.findAll().stream()
                .filter(inv -> inv.getStatus() == Invoice.Status.PAID);
    }

    @Override
    public double getTotalRevenue(LocalDate start, LocalDate end) {
        return getPaidInvoices()
                .filter(inv -> inv.getIssueAt() != null
                && !inv.getIssueAt().toLocalDate().isBefore(start)
                && !inv.getIssueAt().toLocalDate().isAfter(end))
                .mapToDouble(Invoice::getTotalAmount)
                .sum();
    }

    @Override
    public Map<String, Double> getRevenueByMonth(LocalDate start, LocalDate end) {
        return getPaidInvoices()
                .filter(inv -> inv.getIssueAt() != null
                && !inv.getIssueAt().toLocalDate().isBefore(start)
                && !inv.getIssueAt().toLocalDate().isAfter(end))
                .collect(Collectors.groupingBy(
                        inv -> String.format("%d-%02d", inv.getIssueAt().getYear(), inv.getIssueAt().getMonthValue()),
                        Collectors.summingDouble(Invoice::getTotalAmount)
                ));
    }

    @Override
    public Map<String, Long> getNewUsersByMonth(LocalDate start, LocalDate end) {
        return userRepo.findAll().stream()
                .filter(u -> u.getCreatedAt() != null
                && !u.getCreatedAt().toLocalDate().isBefore(start)
                && !u.getCreatedAt().toLocalDate().isAfter(end))
                .collect(Collectors.groupingBy(
                        u -> String.format("%d-%02d", u.getCreatedAt().getYear(), u.getCreatedAt().getMonthValue()),
                        Collectors.counting()
                ));
    }

    @Override
    public long countBookings(LocalDate start, LocalDate end) {
        Map<String, String> params = new HashMap<>();
        params.put("start", String.valueOf(start));
        params.put("end", String.valueOf(end));
        return bookingRepo.countBookings(params);
    }

    @Override
    public long countRooms() {
        return roomRepo.countRooms(null, Boolean.FALSE);
    }

    @Override
    public long countServiceBookings(LocalDate start, LocalDate end) {
        Map<String, String> params = new HashMap<>();
        params.put("start", String.valueOf(start));
        params.put("end", String.valueOf(end));
        return serviceBookingRepo.count(params);
    }

    @Override
    public long countInvoices(LocalDate start, LocalDate end) {
        Map<String, String> params = new HashMap<>();
        params.put("start", String.valueOf(start));
        params.put("end", String.valueOf(end));
        return invoiceRepo.countInvoices(params);
    }

    @Override
    public Map<String, Object> getRevenueStats(LocalDate start, LocalDate end) {
        Map<String, Object> data = new HashMap<>();
        List<String> months = new ArrayList<>();
        List<Double> values = new ArrayList<>();

        YearMonth now = YearMonth.now();
        if (start == null) {
            start = now.minusMonths(5).atDay(1);
        }
        if (end == null) {
            end = now.atEndOfMonth();
        }

        Map<String, Double> revenueByMonth = getRevenueByMonth(start, end);

        for (int i = 5; i >= 0; i--) {
            YearMonth ym = now.minusMonths(i);
            String key = String.format("%d-%02d", ym.getYear(), ym.getMonthValue());
            months.add("T" + ym.getMonthValue());
            values.add(revenueByMonth.getOrDefault(key, 0.0));
        }

        data.put("months", months);
        data.put("values", values);
        return data;
    }

    @Override
    public Map<String, Object> getBookingStatusStats(LocalDate start, LocalDate end) {
        Map<String, Object> data = new HashMap<>();

        long confirmed = bookingRepo.countByStatus(Booking.Status.CONFIRMED, start, end);
        long pending = bookingRepo.countByStatus(Booking.Status.PENDING, start, end);
        long cancelled = bookingRepo.countByStatus(Booking.Status.CANCELLED, start, end);
        long checkIn = bookingRepo.countByStatus(Booking.Status.CHECKED_IN, start, end);
        long checkOut = bookingRepo.countByStatus(Booking.Status.CHECKED_OUT, start, end);
        long processing = bookingRepo.countByStatus(Booking.Status.PROCESSING, start, end);

        data.put("status", Arrays.asList(
                Booking.Status.CONFIRMED.getDescription(),
                Booking.Status.PENDING.getDescription(),
                Booking.Status.CANCELLED.getDescription(),
                Booking.Status.CHECKED_IN.getDescription(),
                Booking.Status.CHECKED_OUT.getDescription(),
                Booking.Status.PROCESSING.getDescription()
        ));
        data.put("values", Arrays.asList(confirmed, pending, cancelled, checkIn, checkOut, processing));

        return data;
    }

    @Override
    public Map<String, Object> getNewUsersStats(LocalDate start, LocalDate end) {
        Map<String, Object> data = new HashMap<>();
        List<String> months = new ArrayList<>();
        List<Long> values = new ArrayList<>();

        YearMonth now = YearMonth.now();
        if (start == null) {
            start = now.minusMonths(5).atDay(1);
        }
        if (end == null) {
            end = now.atEndOfMonth();
        }

        Map<String, Long> newUserByMonth = getNewUsersByMonth(start, end);

        for (int i = 5; i >= 0; i--) {
            YearMonth ym = now.minusMonths(i);
            String key = String.format("%d-%02d", ym.getYear(), ym.getMonthValue());
            months.add("T" + ym.getMonthValue());
            values.add(newUserByMonth.getOrDefault(key, Long.valueOf(0)));
        }

        data.put("months", months);
        data.put("values", values);
        return data;
    }
}
