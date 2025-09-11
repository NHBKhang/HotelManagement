package com.team.hotelmanagementapp.services.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.team.hotelmanagementapp.pojo.Booking;
import com.team.hotelmanagementapp.pojo.ServiceBooking;
import com.team.hotelmanagementapp.repositories.ServiceBookingRepository;
import com.team.hotelmanagementapp.services.ServiceBookingService;

@Service
public class ServiceBookingServiceImpl implements ServiceBookingService {
    
    @Autowired
    private ServiceBookingRepository serviceBookingRepo;

    @Override
    public List<ServiceBooking> findAll() {
        return serviceBookingRepo.findAll();
    }

    @Override
    public ServiceBooking getById(int id) {
        return serviceBookingRepo.getById(id);
    }

    @Override
    public List<ServiceBooking> find(Map<String, String> params) {
        return serviceBookingRepo.find(params);
    }

    @Override
    public List<ServiceBooking> findByBooking(int bookingId, Map<String, String> params) {
        return serviceBookingRepo.findByBooking(bookingId, params);
    }

    @Override
    public ServiceBooking createOrUpdate(ServiceBooking serviceBooking) {
        return serviceBookingRepo.createOrUpdate(serviceBooking);
    }

    @Override
    public void delete(int id) {
        serviceBookingRepo.delete(id);
    }

    @Override
    public void delete(List<Integer> ids) {
        serviceBookingRepo.delete(ids);
    }

    @Override
    public long countServiceBookings(Map<String, String> params) {
        return serviceBookingRepo.count(params);
    }

    @Override
    public List<ServiceBooking> createMulti(Booking b, List<Map<String, Object>> services) {
        return serviceBookingRepo.createMulti(b, services);
    }
    
}
