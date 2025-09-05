package com.team.hotelmanagementapp.repositories.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.team.hotelmanagementapp.pojo.Booking;
import com.team.hotelmanagementapp.repositories.BookingRepository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Repository
@Transactional
public class BookingRepositoryImpl implements BookingRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<Booking> findAll() {
        Session s = this.factory.getObject().getCurrentSession();
        return s.createQuery("SELECT DISTINCT b FROM Booking b LEFT JOIN FETCH b.feedbacks", Booking.class).getResultList();
    }

    @Override
    public Booking getById(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.find(Booking.class, id);
    }

    @Override
    public List<Booking> findByUsername(Map<String, String> params, String username) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Booking> q = b.createQuery(Booking.class);
        Root<Booking> root = q.from(Booking.class);

        List<Predicate> predicates = new ArrayList<>();

        if (username != null) {
            predicates.add(b.equal(root.get("user").get("username"), username));
        }

        if (params != null && params.get("status") != null) {
            try {
                Booking.Status status = Booking.Status.valueOf(params.get("status").toUpperCase());
                predicates.add(b.equal(root.get("status"), status));
            } catch (IllegalArgumentException ex) {
            }
        }

        if (!predicates.isEmpty()) {
            q.where(predicates.toArray(Predicate[]::new));
        }

        q.orderBy(b.desc(root.get("createdAt")));

        Query<Booking> query = s.createQuery(q);

        if (params != null) {
            int page;
            int pageSize = Integer.parseInt(params.getOrDefault("pageSize", "10"));
            try {
                page = Integer.parseInt(params.getOrDefault("page", "1"));
            } catch (NumberFormatException e) {
                page = 1;
            }
            int start = (page - 1) * pageSize;
            query.setFirstResult(start);
            query.setMaxResults(pageSize);
        }

        return query.getResultList();
    }

    @Override
    public Booking createOrUpdate(Booking booking) {
        Session s = this.factory.getObject().getCurrentSession();

        if (booking.getId() == null) {
            booking.setCode(this.generateCode());
            s.persist(booking);
        } else {
            booking = s.merge(booking);
        }

        return booking;
    }

    @Override
    public void delete(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        Booking booking = s.find(Booking.class, id);
        if (booking != null) {
            s.remove(booking);
        }
    }

    @Override
    public long countBookingsByUsername(Map<String, String> params, String username) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Long> q = b.createQuery(Long.class);
        Root<Booking> root = q.from(Booking.class);

        List<Predicate> predicates = new ArrayList<>();

        if (username != null) {
            predicates.add(b.equal(root.get("user").get("username"), username));
        }

        if (params != null && params.get("status") != null) {
            try {
                Booking.Status status = Booking.Status.valueOf(params.get("status").toUpperCase());
                predicates.add(b.equal(root.get("status"), status));
            } catch (IllegalArgumentException ignored) {
            }
        }

        q.select(b.count(root));

        if (!predicates.isEmpty()) {
            q.where(predicates.toArray(Predicate[]::new));
        }

        Query<Long> query = s.createQuery(q);
        return query.getSingleResult();
    }

    @Override
    public List<Booking> find(Map<String, String> params) {
        return findByUsername(params, null);
    }

    @Override
    public long countBookings(Map<String, String> params) {
        return countBookingsByUsername(params, null);
    }

    @Override
    public List<Booking> findBookingsByRoom(int roomId) {
        Session s = this.factory.getObject().getCurrentSession();
        Query<Booking> query = s.createQuery("SELECT DISTINCT b FROM Booking b LEFT JOIN FETCH b.feedbacks WHERE b.room.id = :roomId", Booking.class);
        query.setParameter("roomId", roomId);
        return query.getResultList();
    }

    @Override
    public List<Booking> findBookingsByRoomAndDateRange(int roomId, LocalDate checkIn, LocalDate checkOut) {
        Session s = this.factory.getObject().getCurrentSession();
        Query<Booking> query = s.createQuery(
                "SELECT DISTINCT b FROM Booking b LEFT JOIN FETCH b.feedbacks WHERE b.room.id = :roomId AND b.status IN (:activeStatuses) AND ((b.checkInDate < :checkOut AND b.checkOutDate > :checkIn))",
                Booking.class
        );
        query.setParameter("roomId", roomId);
        query.setParameter("checkIn", checkIn);
        query.setParameter("checkOut", checkOut);

        // Only consider bookings that are confirmed, checked in, or checked out (not cancelled or pending)
        List<Booking.Status> activeStatuses = List.of(
                Booking.Status.CONFIRMED,
                Booking.Status.CHECKED_IN,
                Booking.Status.CHECKED_OUT,
                Booking.Status.PROCESSING
        );
        query.setParameter("activeStatuses", activeStatuses);

        return query.getResultList();
    }

    @Override
    public boolean isRoomAvailable(int roomId, LocalDate checkIn, LocalDate checkOut) {
        return isRoomAvailable(roomId, checkIn, checkOut, null);
    }

    @Override
    public boolean isRoomAvailable(int roomId, LocalDate checkIn, LocalDate checkOut, Integer excludeBookingId) {
        Session s = this.factory.getObject().getCurrentSession();

        // Check if there are any overlapping bookings
        String hql = "SELECT COUNT(b) FROM Booking b WHERE b.room.id = :roomId AND b.status IN (:activeStatuses) AND ((b.checkInDate < :checkOut AND b.checkOutDate > :checkIn))";
        
        if (excludeBookingId != null) {
            hql += " AND b.id != :excludeBookingId";
        }

        Query<Long> query = s.createQuery(hql, Long.class);
        query.setParameter("roomId", roomId);
        query.setParameter("checkIn", checkIn);
        query.setParameter("checkOut", checkOut);

        if (excludeBookingId != null) {
            query.setParameter("excludeBookingId", excludeBookingId);
        }

        // Only consider bookings that are confirmed, checked in, or checked out (not cancelled or pending)
        List<Booking.Status> activeStatuses = List.of(
                Booking.Status.CONFIRMED,
                Booking.Status.CHECKED_IN,
                Booking.Status.CHECKED_OUT,
                Booking.Status.PROCESSING
        );
        query.setParameter("activeStatuses", activeStatuses);

        Long count = query.getSingleResult();
        return count == 0; // Room is available if no overlapping bookings
    }
    private String generateCode() {
        Session s = this.factory.getObject().getCurrentSession();
        Query q = s.createQuery("SELECT MAX(b.id) FROM Booking b", Integer.class);
        Integer maxId = (Integer) q.getSingleResult();

        int nextId = (maxId != null) ? maxId + 1 : 1;
        return "B" + String.format("%04d", nextId);
    }
}
