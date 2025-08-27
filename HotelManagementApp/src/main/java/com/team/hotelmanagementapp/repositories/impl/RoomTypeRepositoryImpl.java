package com.team.hotelmanagementapp.repositories.impl;

import com.team.hotelmanagementapp.pojo.RoomType;
import com.team.hotelmanagementapp.repositories.RoomTypeRepository;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class RoomTypeRepositoryImpl implements RoomTypeRepository {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<RoomType> findAll() {
        Session session = sessionFactory.getCurrentSession();
        return session.createNamedQuery("RoomType.findAll", RoomType.class).getResultList();
    }

    @Override
    public RoomType findById(int id) {
        Session session = sessionFactory.getCurrentSession();
        return session.find(RoomType.class, id);
    }

    @Override
    public RoomType save(RoomType roomType) {
        Session session = sessionFactory.getCurrentSession();
        session.merge(roomType);
        return roomType;
    }

    @Override
    public void delete(int id) {
        Session session = sessionFactory.getCurrentSession();
        RoomType roomType = session.find(RoomType.class, id);
        if (roomType != null) {
            session.remove(roomType);
        }
    }
}