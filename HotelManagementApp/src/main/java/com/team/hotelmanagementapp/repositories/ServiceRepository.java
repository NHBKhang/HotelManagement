package com.team.hotelmanagementapp.repositories;

import com.team.hotelmanagementapp.pojo.Service;
import java.util.List;
import java.util.Map;

public interface ServiceRepository {
    
    List<Service> find(Map<String, String> params);

    Service getById(int id);

    Service createOrUpdate(Service service);

    void delete(int id);
    
    void delete(List<Integer> ids);

    long countServices(Map<String, String> params);

    List<Service> getByIds(List<Integer> serviceIds);
 
}
