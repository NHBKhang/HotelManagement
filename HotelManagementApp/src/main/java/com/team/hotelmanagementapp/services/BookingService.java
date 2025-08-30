package com.team.hotelmanagementapp.services;

import com.team.hotelmanagementapp.pojo.Booking;
import com.team.hotelmanagementapp.pojo.Payment;
import java.util.List;
import java.util.Map;

public interface BookingService {
    
    List<Booking> findAll();
    
    Booking getById(int id);
    
    List<Booking> find(Map<String, String> params);
    
    List<Booking> findByUsername(Map<String, String> params, String username);
    
    Booking createOrUpdate(Booking booking);
    
    void cancelBooking(int bookingId);
    
    long countBookings(Map<String, String> params);
    
    long countBookingsByUsername(Map<String, String> params, String username);
    
//    List<Booking> findByStatus(Booking.Status status);
    
    Booking createByIdAndUsername(int bookingId, String username, Payment.Method method);
}