package com.team.hotelmanagementapp.services;

import java.util.List;
import java.util.Map;

import com.team.hotelmanagementapp.pojo.Booking;
import com.team.hotelmanagementapp.pojo.ServiceBooking;

public interface ServiceBookingService {

    List<ServiceBooking> findAll();

    ServiceBooking getById(int id);

    List<ServiceBooking> find(Map<String, String> params);

    List<ServiceBooking> findByBooking(int bookingId, Map<String, String> params);

    ServiceBooking createOrUpdate(ServiceBooking serviceBooking);

    void delete(int id);

    void delete(List<Integer> ids);

    long countServiceBookings(Map<String, String> params);

    List<ServiceBooking> createMulti(Booking b, List<Map<String, Object>> services);
    
}
