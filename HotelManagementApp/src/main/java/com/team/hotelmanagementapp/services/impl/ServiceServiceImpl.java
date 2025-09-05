package com.team.hotelmanagementapp.services.impl;

import com.team.hotelmanagementapp.pojo.Service;
import com.team.hotelmanagementapp.repositories.ServiceRepository;
import com.team.hotelmanagementapp.services.ServiceService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

@org.springframework.stereotype.Service
public class ServiceServiceImpl implements ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    @Override
    public List<Service> find(Map<String, String> params) {
        return this.serviceRepository.find(params);
    }

    @Override
    public long countServices(Map<String, String> params) {
        return this.serviceRepository.countServices(params);
    }

    @Override
    public Service getById(int id) {
        return this.serviceRepository.getById(id);
    }

    @Override
    public Service createOrUpdate(Service service) {
        return this.serviceRepository.createOrUpdate(service);
    }

    @Override
    public void delete(int id) {
        this.serviceRepository.delete(id);
    }

    @Override
    public void delete(List<Integer> ids) {
        this.serviceRepository.delete(ids);
    }

    @Override
    public List<Service> getByIds(List<Integer> serviceIds) {
        return this.serviceRepository.getByIds(serviceIds);
    }
    
}
