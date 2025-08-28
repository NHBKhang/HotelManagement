package com.team.hotelmanagementapp.repositories.impl;

import com.team.hotelmanagementapp.pojo.Booking;
import com.team.hotelmanagementapp.pojo.Room;
import com.team.hotelmanagementapp.repositories.RoomRepository;
import java.time.LocalDate;
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
public class RoomRepositoryImpl implements RoomRepository {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<Room> findAll() {
        Session session = sessionFactory.getCurrentSession();
        return session.createNamedQuery("Room.findAll", Room.class).getResultList();
    }

    @Override
    public Room findById(int id) {
        Session session = sessionFactory.getCurrentSession();
        return session.find(Room.class, id);
    }

    @Override
    public List<Room> findAvailableRooms(LocalDate checkIn, LocalDate checkOut, Map<String, String> params) {
        Session session = sessionFactory.getCurrentSession();
        
        StringBuilder hql = new StringBuilder("SELECT r FROM Room r WHERE r.status = :status AND r.id NOT IN " +
                "(SELECT b.room.id FROM Booking b WHERE b.status IN (:confirmedStatus, :checkedInStatus) " +
                "AND ((b.checkInDate <= :checkOut AND b.checkOutDate >= :checkIn)))");
        
        if (params.get("roomTypeId") != null) {
            hql.append(" AND r.roomType.id = :roomTypeId");
        }
        if (params.get("maxPrice") != null) {
            hql.append(" AND r.roomType.pricePerNight <= :maxPrice");
        }
        if (params.get("minPrice") != null) {
            hql.append(" AND r.roomType.pricePerNight >= :minPrice");
        }
        
        Query<Room> query = session.createQuery(hql.toString(), Room.class);
        query.setParameter("status", Room.Status.AVAILABLE);
        query.setParameter("confirmedStatus", Booking.Status.CONFIRMED);
        query.setParameter("checkedInStatus", Booking.Status.CHECKED_IN);
        query.setParameter("checkIn", checkIn);
        query.setParameter("checkOut", checkOut);
        
        if (params.get("roomTypeId") != null) {
            query.setParameter("roomTypeId", Integer.parseInt(params.get("roomTypeId")));
        }
        if (params.get("maxPrice") != null) {
            query.setParameter("maxPrice", Double.parseDouble(params.get("maxPrice")));
        }
        if (params.get("minPrice") != null) {
            query.setParameter("minPrice", Double.parseDouble(params.get("minPrice")));
        }
        
        return query.getResultList();
    }

    @Override
    public Room save(Room room) {
        Session session = sessionFactory.getCurrentSession();
        session.merge(room);
        return room;
    }

    @Override
    public void delete(int id) {
        Session session = sessionFactory.getCurrentSession();
        Room room = session.find(Room.class, id);
        if (room != null) {
            session.remove(room);
        }
    }

    @Override
    public long countAvailableRooms(LocalDate checkIn, LocalDate checkOut, Map<String, String> params) {
        Session session = sessionFactory.getCurrentSession();
        
        StringBuilder hql = new StringBuilder("SELECT COUNT(r) FROM Room r WHERE r.status = :status AND r.id NOT IN " +
                "(SELECT b.room.id FROM Booking b WHERE b.status IN (:confirmedStatus, :checkedInStatus) " +
                "AND ((b.checkInDate <= :checkOut AND b.checkOutDate >= :checkIn)))");
        
        if (params.get("roomTypeId") != null) {
            hql.append(" AND r.roomType.id = :roomTypeId");
        }
        if (params.get("maxPrice") != null) {
            hql.append(" AND r.roomType.pricePerNight <= :maxPrice");
        }
        if (params.get("minPrice") != null) {
            hql.append(" AND r.roomType.pricePerNight >= :minPrice");
        }
        
        Query<Long> query = session.createQuery(hql.toString(), Long.class);
        query.setParameter("status", Room.Status.AVAILABLE);
        query.setParameter("confirmedStatus", Booking.Status.CONFIRMED);
        query.setParameter("checkedInStatus", Booking.Status.CHECKED_IN);
        query.setParameter("checkIn", checkIn);
        query.setParameter("checkOut", checkOut);
        
        if (params.get("roomTypeId") != null) {
            query.setParameter("roomTypeId", Integer.parseInt(params.get("roomTypeId")));
        }
        if (params.get("maxPrice") != null) {
            query.setParameter("maxPrice", Double.parseDouble(params.get("maxPrice")));
        }
        if (params.get("minPrice") != null) {
            query.setParameter("minPrice", Double.parseDouble(params.get("minPrice")));
        }
        
        return query.getSingleResult();
    }
}