package com.team.hotelmanagementapp.services.impl;

import com.team.hotelmanagementapp.pojo.Booking;
import com.team.hotelmanagementapp.pojo.ServiceBooking;
import com.team.hotelmanagementapp.repositories.ServiceBookingRepository;
import com.team.hotelmanagementapp.services.ServiceBookingService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceBookingServiceImpl implements ServiceBookingService {
    
    @Autowired
    private ServiceBookingRepository serviceBookingRepository;

    @Override
    public List<ServiceBooking> createMulti(Booking b, List<Map<String, Object>> services) {
        return serviceBookingRepository.createMulti(b, services);
    }
    
}
