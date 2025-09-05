package com.team.hotelmanagementapp.repositories;

import com.team.hotelmanagementapp.pojo.Booking;
import com.team.hotelmanagementapp.pojo.ServiceBooking;
import java.util.List;
import java.util.Map;

public interface ServiceBookingRepository {

    List<ServiceBooking> createMulti(Booking b, List<Map<String, Object>> services);

    ServiceBooking createOrUpdate(ServiceBooking serviceBooking);
    
}
