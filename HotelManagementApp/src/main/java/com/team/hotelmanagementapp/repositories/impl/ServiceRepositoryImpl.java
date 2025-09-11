package com.team.hotelmanagementapp.repositories.impl;

import com.team.hotelmanagementapp.pojo.Service;
import com.team.hotelmanagementapp.repositories.ServiceRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collections;
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
public class ServiceRepositoryImpl implements ServiceRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<Service> find(Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Service> q = b.createQuery(Service.class);
        Root<Service> root = q.from(Service.class);
        q.orderBy(b.desc(root.get("createdAt")));
        q.select(root);

        List<Predicate> predicates = new ArrayList<>();
        if (params != null) {
            String kw = params.get("kw");

            if (kw != null && !kw.isEmpty()) {
                Predicate codePredicate = b.like(root.get("code"), "%" + kw + "%");
                Predicate namePredicate = b.like(root.get("name"), "%" + kw + "%");
                Predicate pricePredicate = b.equal(root.get("price"), "%" + kw + "%");
                predicates.add(b.or(codePredicate, namePredicate, pricePredicate));
            }

            String activeStr = params.get("active");
            if (activeStr != null && !activeStr.isEmpty()) {
                boolean active = Boolean.parseBoolean(activeStr);
                predicates.add(b.equal(root.get("active"), active));
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
    public Service createOrUpdate(Service service) {
        Session s = factory.getObject().getCurrentSession();

        if (service.getId() == null) {
            if (service.getCode() == null) {
                service.setCode(this.generateCode());
            }
            s.persist(service);
        } else {
            service = s.merge(service);
        }

        return service;
    }

    @Override
    public void delete(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        Service p = this.getById(id);
        if (p != null) {
            s.remove(p);
        }
    }

    @Override
    public void delete(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        Session s = this.factory.getObject().getCurrentSession();
        String hql = "DELETE FROM Service s WHERE s.id IN (:ids)";
        int affected = s.createMutationQuery(hql)
                .setParameterList("ids", ids)
                .executeUpdate();

        if (affected != ids.size()) {
            throw new RuntimeException("Không thể xóa hết tất cả dịch vụ!");
        }
    }

    @Override
    public long countServices(Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Long> q = b.createQuery(Long.class);
        Root<Service> root = q.from(Service.class);
        q.select(b.count(root));

        List<Predicate> predicates = new ArrayList<>();
        if (params != null) {
            String kw = params.get("kw");

            if (kw != null && !kw.isEmpty()) {
                Predicate codePredicate = b.like(root.get("code"), "%" + kw + "%");
                Predicate namePredicate = b.like(root.get("name"), "%" + kw + "%");
                Predicate pricePredicate = b.equal(root.get("price"), "%" + kw + "%");
                Predicate durationPredicate = b.like(root.get("duration"), "%" + kw + "%");
                predicates.add(b.or(codePredicate, namePredicate, pricePredicate, durationPredicate));
            }
        }

        if (!predicates.isEmpty()) {
            q.where(predicates.toArray(Predicate[]::new));
        }

        return s.createQuery(q).getSingleResult();
    }

    @Override
    public Service getById(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.find(Service.class, id);
    }

    private String generateCode() {
        Session s = this.factory.getObject().getCurrentSession();
        Query q = s.createQuery("SELECT MAX(s.id) FROM Service s", Integer.class);
        Integer maxId = (Integer) q.getSingleResult();

        int nextId = (maxId != null) ? maxId + 1 : 1;
        return "DV" + String.format("%03d", nextId);
    }

    @Override
    public List<Service> getByIds(List<Integer> serviceIds) {
        if (serviceIds == null || serviceIds.isEmpty()) {
            return Collections.emptyList();
        }
        Session s = this.factory.getObject().getCurrentSession();
        String hql = "FROM Service s WHERE s.id IN (:ids)";
        return s.createQuery(hql, Service.class)
                .setParameterList("ids", serviceIds)
                .getResultList();
    }
}
