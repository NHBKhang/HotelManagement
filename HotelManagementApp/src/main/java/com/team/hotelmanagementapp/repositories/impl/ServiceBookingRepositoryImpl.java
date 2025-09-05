package com.team.hotelmanagementapp.repositories.impl;

import com.team.hotelmanagementapp.pojo.Booking;
import com.team.hotelmanagementapp.pojo.Service;
import com.team.hotelmanagementapp.pojo.ServiceBooking;
import com.team.hotelmanagementapp.repositories.ServiceBookingRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class ServiceBookingRepositoryImpl implements ServiceBookingRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<ServiceBooking> createMulti(Booking b, List<Map<String, Object>> services) {
        if (services == null || services.isEmpty()) {
            return List.of();
        }

        Session s = factory.getObject().getCurrentSession();
        List<ServiceBooking> results = new ArrayList<>();

        for (Map<String, Object> sv : services) {
            Integer serviceId = Integer.valueOf(sv.get("id").toString());
            Integer quantity = Integer.valueOf(sv.getOrDefault("quantity", "1").toString());

            Service service = s.find(Service.class, serviceId);
            if (service != null) {
                ServiceBooking sb = new ServiceBooking();
                sb.setBooking(b);
                sb.setService(service);
                sb.setQuantity(quantity);
                sb.setTotalPrice(service.getPrice() * quantity);
                s.persist(sb);
                results.add(sb);
            } else {
                System.out.println("⚠ Service ID " + serviceId + " không tồn tại, bỏ qua!");
            }
        }

        return results;
    }

    @Override
    public ServiceBooking createOrUpdate(ServiceBooking serviceBooking) {
        Session s = this.factory.getObject().getCurrentSession();

        if (serviceBooking.getId() == null) {
            s.persist(serviceBooking);
        } else {
            serviceBooking = s.merge(serviceBooking);
        }

        return serviceBooking;
    }
}
