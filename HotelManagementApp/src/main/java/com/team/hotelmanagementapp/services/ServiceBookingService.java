package com.team.hotelmanagementapp.services;

import com.team.hotelmanagementapp.pojo.Booking;
import com.team.hotelmanagementapp.pojo.ServiceBooking;
import java.util.List;
import java.util.Map;

public interface ServiceBookingService {

    List<ServiceBooking> createMulti(Booking b, List<Map<String, Object>> services);
    
}
