package com.team.hotelmanagementapp.repositories.impl;

import com.team.hotelmanagementapp.pojo.Booking;
import com.team.hotelmanagementapp.repositories.BookingRepository;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class BookingRepositoryImpl implements BookingRepository {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<Booking> findAll() {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("SELECT DISTINCT b FROM Booking b LEFT JOIN FETCH b.feedbacks", Booking.class).getResultList();
    }

    @Override
    public Booking findById(int id) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("SELECT b FROM Booking b LEFT JOIN FETCH b.feedbacks WHERE b.id = :id", Booking.class)
                .setParameter("id", id)
                .uniqueResult();
    }

    @Override
    public List<Booking> findByUser(int userId, Map<String, String> params) {
        Session session = sessionFactory.getCurrentSession();
        
        StringBuilder hql = new StringBuilder("SELECT DISTINCT b FROM Booking b LEFT JOIN FETCH b.feedbacks WHERE b.user.id = :userId");
        
        if (params.get("status") != null) {
            hql.append(" AND b.status = :status");
        }
        
        hql.append(" ORDER BY b.createdAt DESC");
        
        Query<Booking> query = session.createQuery(hql.toString(), Booking.class);
        query.setParameter("userId", userId);
        
        if (params.get("status") != null) {
            query.setParameter("status", Booking.Status.valueOf(params.get("status").toUpperCase()));
        }
        
        return query.getResultList();
    }

    @Override
    public Booking save(Booking booking) {
        Session session = sessionFactory.getCurrentSession();
        session.merge(booking);
        return booking;
    }

    @Override
    public void delete(int id) {
        Session session = sessionFactory.getCurrentSession();
        Booking booking = session.find(Booking.class, id);
        if (booking != null) {
            session.remove(booking);
        }
    }

    @Override
    public long countBookings(Map<String, String> params) {
        Session session = sessionFactory.getCurrentSession();
        
        StringBuilder hql = new StringBuilder("SELECT COUNT(b) FROM Booking b WHERE 1=1");
        
        if (params.get("status") != null) {
            hql.append(" AND b.status = :status");
        }
        if (params.get("userId") != null) {
            hql.append(" AND b.user.id = :userId");
        }
        
        Query<Long> query = session.createQuery(hql.toString(), Long.class);
        
        if (params.get("status") != null) {
            query.setParameter("status", Booking.Status.valueOf(params.get("status").toUpperCase()));
        }
        if (params.get("userId") != null) {
            query.setParameter("userId", Integer.parseInt(params.get("userId")));
        }
        
        return query.getSingleResult();
    }

    @Override
    public List<Booking> findByStatus(Booking.Status status) {
        Session session = sessionFactory.getCurrentSession();
        Query<Booking> query = session.createQuery("SELECT DISTINCT b FROM Booking b LEFT JOIN FETCH b.feedbacks WHERE b.status = :status", Booking.class);
        query.setParameter("status", status);
        return query.getResultList();
    }
}