package com.team.hotelmanagementapp.repositories;

import com.team.hotelmanagementapp.pojo.Booking;
import java.util.List;
import java.util.Map;

public interface BookingRepository {
    
    List<Booking> findAll();
    
    Booking findById(int id);
    
    List<Booking> findByUser(int userId, Map<String, String> params);
    
    Booking save(Booking booking);
    
    void delete(int id);
    
    long countBookings(Map<String, String> params);
    
    List<Booking> findByStatus(Booking.Status status);
}