package com.team.hotelmanagementapp.services;

import java.util.Map;

public interface StatsService {

    double getTotalRevenue();

    Map<String, Double> getRevenueByMonth();

    Map<String, Double> getRevenueByPaymentMethod();
}
