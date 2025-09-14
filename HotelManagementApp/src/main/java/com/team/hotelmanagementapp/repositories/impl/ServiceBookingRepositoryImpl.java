package com.team.hotelmanagementapp.repositories.impl;

import com.team.hotelmanagementapp.pojo.Booking;
import com.team.hotelmanagementapp.pojo.Service;
import com.team.hotelmanagementapp.pojo.ServiceBooking;
import com.team.hotelmanagementapp.repositories.ServiceBookingRepository;
import com.team.hotelmanagementapp.utils.Pagination;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.LocalDate;
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
    public List<ServiceBooking> findAll() {
        Session s = this.factory.getObject().getCurrentSession();
        Query q = s.createNamedQuery("ServiceBooking.findAll", ServiceBooking.class);
        return q.getResultList();
    }

    @Override
    public List<ServiceBooking> find(Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<ServiceBooking> q = b.createQuery(ServiceBooking.class);
        Root<ServiceBooking> root = q.from(ServiceBooking.class);

        // Join with related entities for filtering and display
        Join<ServiceBooking, Booking> bookingJoin = root.join("booking", JoinType.INNER);
        Join<ServiceBooking, Service> serviceJoin = root.join("service", JoinType.INNER);

        List<Predicate> predicates = new ArrayList<>();

        if (params != null) {
            if (params.containsKey("kw") && !params.get("kw").isEmpty()) {
                String kw = "%" + params.get("kw").toLowerCase() + "%";
                Predicate p1 = b.like(b.lower(serviceJoin.get("name")), kw);
                Predicate p2 = b.like(b.lower(bookingJoin.get("code")), kw);
                predicates.add(b.or(p1, p2));
            }

            if (params.containsKey("bookingId") && !params.get("bookingId").isEmpty()) {
                predicates.add(b.equal(bookingJoin.get("id"), Integer.valueOf(params.get("bookingId"))));
            }

            if (params.containsKey("serviceId") && !params.get("serviceId").isEmpty()) {
                predicates.add(b.equal(serviceJoin.get("id"), Integer.valueOf(params.get("serviceId"))));
            }

            if (params.containsKey("start") && !params.get("start").isEmpty()) {
                LocalDate start = LocalDate.parse(params.get("start"));
                predicates.add(b.greaterThanOrEqualTo(root.get("createdAt"), start.atStartOfDay()));
            }

            if (params.containsKey("end") && !params.get("end").isEmpty()) {
                LocalDate end = LocalDate.parse(params.get("end"));
                predicates.add(b.lessThanOrEqualTo(root.get("createdAt"), end.atTime(23, 59, 59)));
            }
        }

        q.select(root).where(predicates.toArray(Predicate[]::new));
        q.orderBy(b.desc(root.get("createdAt")));

        Query query = s.createQuery(q);

        if (params != null) {
            String page = params.get("page");
            if (page != null && !page.isEmpty()) {
                int p = Integer.parseInt(page);
                int pageSize = 10; // Default page size
                int start = (p - 1) * pageSize;
                query.setFirstResult(start);
                query.setMaxResults(pageSize);
            }
        }

        return query.getResultList();
    }

    @Override
    public List<ServiceBooking> findByBooking(int bookingId, Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<ServiceBooking> q = b.createQuery(ServiceBooking.class);
        Root<ServiceBooking> root = q.from(ServiceBooking.class);

        Join<ServiceBooking, Booking> bookingJoin = root.join("booking", JoinType.INNER);
        Join<ServiceBooking, Service> serviceJoin = root.join("service", JoinType.INNER);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(b.equal(bookingJoin.get("id"), bookingId));

        if (params != null) {
            if (params.containsKey("kw") && !params.get("kw").isEmpty()) {
                String kw = "%" + params.get("kw").toLowerCase() + "%";
                predicates.add(b.like(b.lower(serviceJoin.get("name")), kw));
            }
        }

        q.select(root).where(predicates.toArray(Predicate[]::new));
        q.orderBy(b.desc(root.get("createdAt")));

        Query query = s.createQuery(q);

        if (params != null) {
            String page = params.get("page");
            if (page != null && !page.isEmpty()) {
                int p = Integer.parseInt(page);
                int pageSize = 10; // Default page size
                int start = (p - 1) * pageSize;
                query.setFirstResult(start);
                query.setMaxResults(pageSize);
            }
        }

        return query.getResultList();
    }

    @Override
    public ServiceBooking getById(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.find(ServiceBooking.class, id);
    }

    @Override
    public void delete(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        ServiceBooking serviceBooking = this.getById(id);
        if (serviceBooking != null) {
            s.remove(serviceBooking);
        }
    }

    @Override
    public void delete(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        Session s = this.factory.getObject().getCurrentSession();
        String hql = "DELETE FROM ServiceBooking s WHERE s.id IN (:ids)";
        int affected = s.createMutationQuery(hql)
                .setParameterList("ids", ids)
                .executeUpdate();

        if (affected != ids.size()) {
            throw new RuntimeException("Không thể xóa hết tất cả!");
        }
    }

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
                sb.setCode(this.generateCode());
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

        if (serviceBooking.getCode() == null) {
            serviceBooking.setCode(this.generateCode());
        }
        
        if (serviceBooking.getId() == null) {
            s.persist(serviceBooking);
        } else {
            serviceBooking = s.merge(serviceBooking);
        }

        return serviceBooking;
    }

    @Override
    public long count(Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Long> q = b.createQuery(Long.class);
        Root<ServiceBooking> root = q.from(ServiceBooking.class);

        List<Predicate> predicates = new ArrayList<>();

        if (params != null) {
            if (params.containsKey("start") && !params.get("start").isEmpty()) {
                LocalDate start = LocalDate.parse(params.get("start"));
                predicates.add(b.greaterThanOrEqualTo(
                        root.get("createdAt"), start.atStartOfDay()
                ));
            }

            if (params.containsKey("end") && !params.get("end").isEmpty()) {
                LocalDate end = LocalDate.parse(params.get("end"));
                predicates.add(b.lessThanOrEqualTo(
                        root.get("createdAt"), end.atTime(23, 59, 59)
                ));
            }
        }

        q.select(b.count(root)).where(predicates.toArray(Predicate[]::new));

        return s.createQuery(q).getSingleResult();
    }

    private String generateCode() {
        Session s = this.factory.getObject().getCurrentSession();
        Query q = s.createQuery("SELECT MAX(s.id) FROM ServiceBooking s", Integer.class);
        Integer maxId = (Integer) q.getSingleResult();

        int nextId = (maxId != null) ? maxId + 1 : 1;
        return "DDV" + String.format("%03d", nextId);
    }
}
