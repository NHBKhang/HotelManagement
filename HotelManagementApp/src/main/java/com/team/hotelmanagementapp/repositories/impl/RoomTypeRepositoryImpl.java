package com.team.hotelmanagementapp.repositories.impl;

import com.team.hotelmanagementapp.pojo.RoomType;
import com.team.hotelmanagementapp.repositories.RoomTypeRepository;
import java.util.List;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class RoomTypeRepositoryImpl implements RoomTypeRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<RoomType> findAll() {
        Session session = this.factory.getObject().getCurrentSession();
        return session.createNamedQuery("RoomType.findAll", RoomType.class).getResultList();
    }

    @Override
    public RoomType findById(int id) {
        Session session = this.factory.getObject().getCurrentSession();
        return session.find(RoomType.class, id);
    }

    @Override
    public RoomType createOrUpdate(RoomType roomType) {
        Session s = this.factory.getObject().getCurrentSession();

        if (roomType.getId() == null) {
            s.persist(roomType);
        } else {
            roomType = s.merge(roomType);
        }

        return roomType;
    }

    @Override
    public void delete(int id) {
        Session session = this.factory.getObject().getCurrentSession();
        RoomType roomType = session.find(RoomType.class, id);
        if (roomType != null) {
            session.remove(roomType);
        }
    }
}