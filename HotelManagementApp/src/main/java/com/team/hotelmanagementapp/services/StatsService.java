package com.team.hotelmanagementapp.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.team.hotelmanagementapp.pojo.Booking;
import com.team.hotelmanagementapp.pojo.Room;
import com.team.hotelmanagementapp.pojo.Service;
import com.team.hotelmanagementapp.pojo.ServiceBooking;

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

    Map<String, Long> getRoomsByStatus();

    List<Booking> getRecentBookings(int limit);

    List<Service> getAllServices();
    
    List<ServiceBooking> getRecentServiceBookings(int limit);
    
    double getTotalServiceRevenue(LocalDate start, LocalDate end);
}
