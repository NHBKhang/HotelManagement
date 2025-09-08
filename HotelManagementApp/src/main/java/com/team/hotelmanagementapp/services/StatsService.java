package com.team.hotelmanagementapp.services;

import java.time.LocalDate;
import java.util.Map;

public interface StatsService {

    double getTotalRevenue(LocalDate start, LocalDate end);

    Map<String, Double> getRevenueByMonth(LocalDate start, LocalDate end);

    Map<String, Long> getNewUsersByMonth(LocalDate start, LocalDate end);

    long countRooms();

    long countBookings(LocalDate start, LocalDate end);

    long countServiceBookings(LocalDate start, LocalDate end);

    long countInvoices(LocalDate start, LocalDate end);

    Map<String, Object> getRevenueStats(LocalDate start, LocalDate end);

    Map<String, Object> getBookingStatusStats(LocalDate start, LocalDate end);

    Map<String, Object> getNewUsersStats(LocalDate start, LocalDate end);
}
