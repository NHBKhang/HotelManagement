package com.team.hotelmanagementapp.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.hotelmanagementapp.services.StatsService;
import java.time.LocalDate;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@ControllerAdvice
@RequestMapping("/stats")
public class StatsController {

    @Autowired
    private StatsService statsService;
    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping
    public String statisticIndex(
            @RequestParam(value = "start", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(value = "end", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            Model model) throws JsonProcessingException {

        if (start == null) {
            start = LocalDate.now().withDayOfYear(1);
        }
        if (end == null) {
            end = LocalDate.now();
        }

        double totalRevenue = statsService.getTotalRevenue(start, end);
        long totalBookings = statsService.countBookings(start, end);
        long totalRooms = statsService.countRooms();
        long totalServiceBookings = statsService.countServiceBookings(start, end);

        Map<String, Object> revenueStats = statsService.getRevenueStats(start, end);
        Map<String, Object> bookingStatusStats = statsService.getBookingStatusStats(start, end);
        Map<String, Object> newUsersStats = statsService.getNewUsersStats(start, end);

        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalBookings", totalBookings);
        model.addAttribute("totalRooms", totalRooms);
        model.addAttribute("totalServiceBookings", totalServiceBookings);

        model.addAttribute("revenueStats", revenueStats);
        model.addAttribute("bookingStatusStats", bookingStatusStats);
        model.addAttribute("newUsersStats", newUsersStats);

        model.addAttribute("start", start);
        model.addAttribute("end", end);

        return "stats/index";
    }

    @GetMapping("/revenue")
    public String revenueStatistics(
            @RequestParam(value = "start", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(value = "end", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            Model model) throws JsonProcessingException {

        if (start == null || end == null) {
            LocalDate now = LocalDate.now();
            end = now.withDayOfMonth(now.lengthOfMonth());
            start = now.minusMonths(5).withDayOfMonth(1);   
        }

        Map<String, Double> revenueByMonth = statsService.getRevenueByMonth(start, end);
        double totalRevenue = statsService.getTotalRevenue(start, end);

        long totalInvoices = statsService.countInvoices(start, end);

        double avgInvoice = totalInvoices > 0 ? totalRevenue / totalInvoices : 0;

        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalInvoices", totalInvoices);
        model.addAttribute("avgInvoice", avgInvoice);
        model.addAttribute("revenueByMonth", revenueByMonth);
        model.addAttribute("revenueByMonthJson", objectMapper.writeValueAsString(revenueByMonth));
        model.addAttribute("start", start);
        model.addAttribute("end", end);

        return "stats/revenue";
    }

}
