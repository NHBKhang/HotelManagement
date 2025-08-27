package com.team.hotelmanagementapp.services;

import com.team.hotelmanagementapp.pojo.Booking;
import java.util.List;
import java.util.Map;

public interface BookingService {
    
    List<Booking> findAll();
    
    Booking findById(int id);
    
    List<Booking> findByUser(int userId, Map<String, String> params);
    
    Booking createBooking(Booking booking);
    
    Booking updateBookingStatus(int bookingId, Booking.Status status);
    
    void cancelBooking(int bookingId);
    
    long countBookings(Map<String, String> params);
    
    List<Booking> findByStatus(Booking.Status status);
}