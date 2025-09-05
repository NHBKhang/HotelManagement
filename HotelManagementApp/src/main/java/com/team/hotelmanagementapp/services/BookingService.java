package com.team.hotelmanagementapp.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.team.hotelmanagementapp.pojo.Booking;
import com.team.hotelmanagementapp.pojo.Payment;

public interface BookingService {
    
    List<Booking> findAll();
    
    Booking getById(int id);
    
    List<Booking> find(Map<String, String> params);
    
    List<Booking> findByUsername(Map<String, String> params, String username);
    
    Booking createOrUpdate(Booking booking);
    
    void cancelBooking(int bookingId);
    
    long countBookings(Map<String, String> params);
    
    long countBookingsByUsername(Map<String, String> params, String username);
    
    boolean isRoomAvailable(int roomId, LocalDate checkIn, LocalDate checkOut, Integer excludeBookingId);
    List<Booking> findBookingsByRoomAndDateRange(int roomId, LocalDate checkIn, LocalDate checkOut);
    
//    List<Booking> findByStatus(Booking.Status status);
    
    Booking createByIdAndUsername(int bookingId, String username, Payment.Method method);
}
