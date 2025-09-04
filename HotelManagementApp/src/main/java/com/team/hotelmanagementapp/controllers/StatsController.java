package com.team.hotelmanagementapp.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.hotelmanagementapp.services.StatsService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@ControllerAdvice
@RequestMapping("/stats")
public class StatsController {

    @Autowired
    private StatsService statsService;
    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/revenue")
    public String revenueStatistics(Model model) throws JsonProcessingException {
        Map<String, Double> revenueByMonth = statsService.getRevenueByMonth();

        model.addAttribute("totalRevenue", statsService.getTotalRevenue());
        model.addAttribute("revenueByMonth", revenueByMonth);
        model.addAttribute("revenueByMonthJson", objectMapper.writeValueAsString(revenueByMonth));

        return "stats";
    }

}
