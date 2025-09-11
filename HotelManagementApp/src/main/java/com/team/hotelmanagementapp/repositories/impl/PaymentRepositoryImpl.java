package com.team.hotelmanagementapp.repositories.impl;

import com.team.hotelmanagementapp.pojo.Booking;
import com.team.hotelmanagementapp.pojo.Invoice;
import com.team.hotelmanagementapp.pojo.Payment;
import com.team.hotelmanagementapp.pojo.User;
import com.team.hotelmanagementapp.repositories.PaymentRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
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
public class PaymentRepositoryImpl implements PaymentRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<Payment> findAll() {
        Session session = this.factory.getObject().getCurrentSession();
        return session.createNamedQuery("Payment.findAll", Payment.class).getResultList();
    }

    @Override
    public List<Payment> find(Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Payment> q = b.createQuery(Payment.class);
        Root<Payment> root = q.from(Payment.class);

        List<Predicate> predicates = new ArrayList<>();

        if (params != null) {
            String kw = params.get("kw");
            if (kw != null && !kw.isEmpty()) {
                predicates.add(b.like(root.get("code"), "%" + kw + "%"));
            }

            String userId = params.get("userId");
            if (userId != null && !userId.isEmpty()) {
                Join<Payment, Invoice> invoiceJoin = root.join("invoice");
                Join<Invoice, Booking> bookingJoin = invoiceJoin.join("booking");
                Join<Booking, User> userJoin = bookingJoin.join("user");

                predicates.add(b.equal(userJoin.get("id"), Integer.valueOf(userId)));
            }
        }

        if (!predicates.isEmpty()) {
            q.where(predicates.toArray(Predicate[]::new));
        }

        q.select(root)
                .orderBy(b.desc(root.get("createdAt")));

        Query<Payment> query = s.createQuery(q);

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
    public Payment getById(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.find(Payment.class, id);
    }

    @Override
    public Payment createOrUpdate(Payment payment) {
        Session s = factory.getObject().getCurrentSession();

        if (payment.getId() == null) {
            s.persist(payment);
        } else {
            payment = s.merge(payment);
        }

        return payment;
    }

    @Override
    public long count(Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Long> q = b.createQuery(Long.class);
        Root<Payment> root = q.from(Payment.class);
        q.select(b.count(root));

        List<Predicate> predicates = new ArrayList<>();

        if (params != null) {
            String kw = params.get("kw");
            if (kw != null && !kw.isEmpty()) {
                Predicate codePredicate = b.like(root.get("code"), "%" + kw + "%");
                predicates.add(b.or(codePredicate));
            }

            String userId = params.get("userId");
            if (userId != null && !userId.isEmpty()) {
                Join<Payment, Invoice> invoiceJoin = root.join("invoice");
                Join<Invoice, Booking> bookingJoin = invoiceJoin.join("booking");
                Join<Booking, User> userJoin = bookingJoin.join("user");

                predicates.add(b.equal(userJoin.get("id"), Integer.valueOf(userId)));
            }

            if (!predicates.isEmpty()) {
                q.where(predicates.toArray(Predicate[]::new));
            }
        }

        return s.createQuery(q).getSingleResult();
    }

    @Override
    public String generateCode() {
        Session s = this.factory.getObject().getCurrentSession();
        Query q = s.createQuery("SELECT MAX(p.id) FROM Payment p", Integer.class);
        Integer maxId = (Integer) q.getSingleResult();

        int nextId = (maxId != null) ? maxId + 1 : 1;
        return "GD" + String.format("%07d", nextId);
    }
}
