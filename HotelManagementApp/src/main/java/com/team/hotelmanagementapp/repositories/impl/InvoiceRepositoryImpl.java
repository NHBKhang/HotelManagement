package com.team.hotelmanagementapp.repositories.impl;

import com.team.hotelmanagementapp.pojo.Invoice;
import com.team.hotelmanagementapp.repositories.InvoiceRepository;
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
public class InvoiceRepositoryImpl implements InvoiceRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<Invoice> findAll() {
        Session session = this.factory.getObject().getCurrentSession();
        return session.createNamedQuery("Invoice.findAll", Invoice.class).getResultList();
    }

    @Override
    public List<Invoice> find(Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Invoice> q = b.createQuery(Invoice.class);
        Root<Invoice> root = q.from(Invoice.class);

        List<Predicate> predicates = new ArrayList<>();

        if (params != null) {
            String kw = params.get("kw");
            if (kw != null && !kw.isEmpty()) {
                predicates.add(b.like(root.get("code"), "%" + kw + "%"));
            }
        }

        if (!predicates.isEmpty()) {
            q.where(predicates.toArray(Predicate[]::new));
        }

        q.select(root).orderBy(b.desc(root.get("createdAt")));

        Query<Invoice> query = s.createQuery(q);

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
    public Invoice getById(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.find(Invoice.class, id);

    }

    @Override
    public Invoice createOrUpdate(Invoice invoice) {
        Session s = factory.getObject().getCurrentSession();

        if (invoice.getId() == null) {
            s.persist(invoice);
        } else {
            invoice = s.merge(invoice);
        }

        return invoice;
    }

}
