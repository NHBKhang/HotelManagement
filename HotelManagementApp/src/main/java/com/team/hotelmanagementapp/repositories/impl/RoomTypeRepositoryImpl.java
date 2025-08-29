package com.team.hotelmanagementapp.repositories.impl;

import com.team.hotelmanagementapp.pojo.RoomType;
import com.team.hotelmanagementapp.repositories.RoomTypeRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
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
public class RoomTypeRepositoryImpl implements RoomTypeRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<RoomType> findAll() {
        Session s = this.factory.getObject().getCurrentSession();
        return s.createNamedQuery("RoomType.findAll", RoomType.class).getResultList();
    }

    @Override
    public List<RoomType> find(Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<RoomType> q = b.createQuery(RoomType.class);
        Root<RoomType> root = q.from(RoomType.class);

        List<Predicate> predicates = new ArrayList<>();
        if (params != null) {
            String kw = params.get("kw");

            if (kw != null && !kw.isEmpty()) {
                Predicate namePredicate = b.like(root.get("name"), "%" + kw + "%");
                Predicate pricePredicate = b.equal(root.get("price"), "%" + kw + "%");
                predicates.add(b.or(namePredicate, pricePredicate));
            }
        }

        if (!predicates.isEmpty()) {
            q.where(predicates.toArray(Predicate[]::new));
        }

        Query query = s.createQuery(q);
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
    public RoomType getById(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.find(RoomType.class, id);
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
        Session s = this.factory.getObject().getCurrentSession();
        RoomType roomType = s.find(RoomType.class, id);
        if (roomType != null) {
            s.remove(roomType);
        }
    }

    @Override
    public void delete(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        Session s = this.factory.getObject().getCurrentSession();
        String hql = "DELETE FROM RoomType r WHERE r.id IN (:ids)";
        int affected = s.createMutationQuery(hql)
                .setParameterList("ids", ids)
                .executeUpdate();

        if (affected != ids.size()) {
            throw new RuntimeException("Không thể xóa hết tất cả loại phòng!");
        }
    }

    @Override
    public long countTypes(Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Long> q = b.createQuery(Long.class);
        Root<RoomType> root = q.from(RoomType.class);
        q.select(b.count(root));

        List<Predicate> predicates = new ArrayList<>();
        if (params != null) {
            String kw = params.get("kw");

            if (kw != null && !kw.isEmpty()) {
                Predicate namePredicate = b.like(root.get("name"), "%" + kw + "%");
                Predicate pricePredicate = b.equal(root.get("price"), "%" + kw + "%");
                predicates.add(b.or(namePredicate, pricePredicate));
            }
        }

        if (!predicates.isEmpty()) {
            q.where(predicates.toArray(Predicate[]::new));
        }

        return s.createQuery(q).getSingleResult();
    }
}
