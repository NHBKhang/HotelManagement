package com.team.hotelmanagementapp.repositories.impl;

import com.team.hotelmanagementapp.pojo.Booking;
import com.team.hotelmanagementapp.pojo.Room;
import com.team.hotelmanagementapp.repositories.RoomRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
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

@Repository
@Transactional
public class RoomRepositoryImpl implements RoomRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<Room> findAll() {
        Session session = this.factory.getObject().getCurrentSession();
        return session.createNamedQuery("Room.findAll", Room.class).getResultList();
    }

    @Override
    public Room getById(int id) {
        Session session = this.factory.getObject().getCurrentSession();
        return session.find(Room.class, id);
    }

    @Override
    public List<Room> find(Map<String, String> params, Boolean available) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Room> q = b.createQuery(Room.class);
        Root<Room> root = q.from(Room.class);

        List<Predicate> predicates = new ArrayList<>();
        if (available) {
            Predicate statusAvailable = b.equal(root.get("status"), Room.Status.AVAILABLE);
            Predicate statusCleaning = b.equal(root.get("status"), Room.Status.CLEANING);

            predicates.add(b.or(statusAvailable, statusCleaning));
        }

        if (params != null) {
            String statusStr = params.get("status");
            if (statusStr != null) {
                try {
                    Room.Status status = Room.Status.valueOf(statusStr.toUpperCase());
                    predicates.add(b.equal(root.get("status"), status));
                } catch (IllegalArgumentException ignored) {
                }
            }

            String roomTypeId = params.get("roomTypeId");
            if (roomTypeId != null && !roomTypeId.isEmpty()) {
                predicates.add(b.equal(root.get("roomType").get("id"), Integer.valueOf(roomTypeId)));
            }

            String minPrice = params.get("minPrice");
            if (minPrice != null && !minPrice.isEmpty()) {
                predicates.add(b.ge(root.get("roomType").get("pricePerNight"), Double.valueOf(minPrice)));
            }

            String maxPrice = params.get("maxPrice");
            if (maxPrice != null && !maxPrice.isEmpty()) {
                predicates.add(b.le(root.get("roomType").get("pricePerNight"), Double.valueOf(maxPrice)));
            }

            String checkInStr = params.get("checkIn");
            String checkOutStr = params.get("checkOut");
            if (checkInStr != null && checkOutStr != null && !checkInStr.isEmpty() && !checkOutStr.isEmpty()) {
                LocalDate checkIn = LocalDate.parse(checkInStr);
                LocalDate checkOut = LocalDate.parse(checkOutStr);

                // Subquery loại bỏ phòng có booking trùng ngày
                Subquery<Integer> sub = q.subquery(Integer.class);
                Root<Booking> bookingRoot = sub.from(Booking.class);
                sub.select(bookingRoot.get("room").get("id"));

                Predicate bookingStatus = bookingRoot.get("status").in(
                        Booking.Status.CONFIRMED, Booking.Status.CHECKED_IN
                );
                Predicate overlap = b.and(
                        b.lessThanOrEqualTo(bookingRoot.get("checkInDate"), checkOut),
                        b.greaterThanOrEqualTo(bookingRoot.get("checkOutDate"), checkIn)
                );

                sub.where(b.and(bookingStatus, overlap));

                predicates.add(b.not(root.get("id").in(sub)));
            }
            String topRooms = params.get("topRooms");
            if (topRooms != null && Boolean.parseBoolean(topRooms) == true) {
                Subquery<Long> bookingCountSubquery = q.subquery(Long.class);
                Root<Booking> bookingRoot = bookingCountSubquery.from(Booking.class);

                bookingCountSubquery.select(b.count(bookingRoot));
                bookingCountSubquery.where(
                        b.equal(bookingRoot.get("room"), root),
                        bookingRoot.get("status").in(Booking.Status.CONFIRMED, Booking.Status.CHECKED_IN, Booking.Status.CHECKED_OUT)
                );

                q.orderBy(b.desc(bookingCountSubquery.getSelection()));
            }
        }

        q.select(root).where(predicates.toArray(Predicate[]::new));

        Query<Room> query = s.createQuery(q);

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
    public Room createOrUpdate(Room room) {
        System.out.println(room);
        Session s = this.factory.getObject().getCurrentSession();

        if (room.getId() == null) {
            room.setCode("ROOM-" + room.getRoomNumber());
            s.persist(room);
        } else {
            room = s.merge(room);
        }

        return room;
    }

    @Override
    public void delete(int id) {
        Session session = this.factory.getObject().getCurrentSession();
        Room room = session.find(Room.class, id);
        if (room != null) {
            session.remove(room);
        }
    }

    @Override
    public void delete(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        Session s = this.factory.getObject().getCurrentSession();
        String hql = "DELETE FROM Room r WHERE r.id IN (:ids)";
        int affected = s.createMutationQuery(hql)
                .setParameterList("ids", ids)
                .executeUpdate();

        if (affected != ids.size()) {
            throw new RuntimeException("Không thể xóa hết tất cả phòng!");
        }
    }

    @Override
    public long countRooms(Map<String, String> params, Boolean available) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Long> q = b.createQuery(Long.class);
        Root<Room> root = q.from(Room.class);

        List<Predicate> predicates = new ArrayList<>();
        if (available) {
            predicates.add(b.equal(root.get("status"), Room.Status.AVAILABLE));
        }

        if (params != null) {
            String statusStr = params.get("status");
            if (statusStr != null) {
                try {
                    Room.Status status = Room.Status.valueOf(statusStr.toUpperCase());
                    predicates.add(b.equal(root.get("status"), status));
                } catch (IllegalArgumentException ignored) {
                }
            }

            String roomTypeId = params.get("roomTypeId");
            if (roomTypeId != null && !roomTypeId.isEmpty()) {
                predicates.add(b.equal(root.get("roomType").get("id"), Integer.valueOf(roomTypeId)));
            }

            String minPrice = params.get("minPrice");
            if (minPrice != null && !minPrice.isEmpty()) {
                predicates.add(b.ge(root.get("roomType").get("pricePerNight"), Double.valueOf(minPrice)));
            }

            String maxPrice = params.get("maxPrice");
            if (maxPrice != null && !maxPrice.isEmpty()) {
                predicates.add(b.le(root.get("roomType").get("pricePerNight"), Double.valueOf(maxPrice)));
            }

            String checkInStr = params.get("checkIn");
            String checkOutStr = params.get("checkOut");
            if (checkInStr != null && checkOutStr != null && !checkInStr.isEmpty() && !checkOutStr.isEmpty()) {
                LocalDate checkIn = LocalDate.parse(checkInStr);
                LocalDate checkOut = LocalDate.parse(checkOutStr);

                Subquery<Integer> sub = q.subquery(Integer.class);
                Root<Booking> bookingRoot = sub.from(Booking.class);
                sub.select(bookingRoot.get("room").get("id"));

                Predicate bookingStatus = bookingRoot.get("status").in(
                        Booking.Status.CONFIRMED, Booking.Status.CHECKED_IN
                );
                Predicate overlap = b.and(
                        b.lessThanOrEqualTo(bookingRoot.get("checkInDate"), checkOut),
                        b.greaterThanOrEqualTo(bookingRoot.get("checkOutDate"), checkIn)
                );

                sub.where(b.and(bookingStatus, overlap));

                predicates.add(b.not(root.get("id").in(sub)));
            }
        }

        q.select(b.count(root)).where(predicates.toArray(Predicate[]::new));

        return s.createQuery(q).getSingleResult();
    }

    @Override
    public long countByStatus(Room.Status status) {
        Session s = factory.getObject().getCurrentSession();
        String hql = "SELECT COUNT(r) FROM Room r WHERE r.status = :status";
        Query<Long> q = s.createQuery(hql, Long.class);
        q.setParameter("status", status);
        return q.getSingleResult();
    }
}
