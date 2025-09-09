package com.team.hotelmanagementapp.repositories.impl;

import com.team.hotelmanagementapp.pojo.Invoice;
import com.team.hotelmanagementapp.pojo.Payment;
import com.team.hotelmanagementapp.repositories.InvoiceRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

            String userIdStr = params.get("userId");
            if (userIdStr != null && !userIdStr.isEmpty()) {
                try {
                    int userId = Integer.parseInt(userIdStr);
                    predicates.add(b.equal(root.get("user").get("id"), userId));
                } catch (NumberFormatException ex) {
                }
            }

            String bookingIdStr = params.get("bookingId");
            if (bookingIdStr != null && !bookingIdStr.isEmpty()) {
                try {
                    int bookingId = Integer.parseInt(bookingIdStr);
                    predicates.add(b.equal(root.get("booking").get("id"), bookingId));
                } catch (NumberFormatException ex) {
                }
            }
        }

        if (!predicates.isEmpty()) {
            q.where(predicates.toArray(Predicate[]::new));
        }

        q.select(root).orderBy(b.desc(root.get("issueAt")));

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
        Invoice invoice = s.find(Invoice.class, id);

        if (invoice != null) {
            double paid = invoice.getPayments() != null
                    ? invoice.getPayments().stream()
                            .filter(p -> p.getStatus() == Payment.Status.SUCCESS)
                            .mapToDouble(Payment::getAmount).sum()
                    : 0.0;

            double total = invoice.getTotalAmount() != null ? invoice.getTotalAmount() : 0.0;

            invoice.setBalance(total - paid);
        }

        return invoice;
    }

    @Override
    public Invoice createOrUpdate(Invoice invoice) {
        Session s = factory.getObject().getCurrentSession();

        if (invoice.getId() == null) {
            if (invoice.getIssueAt() == null) {
                invoice.setIssueAt(LocalDateTime.now());
            }
            if (invoice.getInvoiceNumber() == null) {
                invoice.setInvoiceNumber(this.generateCode());
            }
            s.persist(invoice);
        } else {
            invoice = s.merge(invoice);
        }

        return invoice;
    }

    @Override
    public long countInvoices(Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Long> q = b.createQuery(Long.class);
        Root<Invoice> root = q.from(Invoice.class);

        List<Predicate> predicates = new ArrayList<>();

        if (params != null) {
            if (params.containsKey("start") && !params.get("start").isEmpty()) {
                LocalDate start = LocalDate.parse(params.get("start"));
                predicates.add(b.greaterThanOrEqualTo(
                        root.get("issueAt"), start.atStartOfDay()
                ));
            }

            if (params.containsKey("end") && !params.get("end").isEmpty()) {
                LocalDate end = LocalDate.parse(params.get("end"));
                predicates.add(b.lessThanOrEqualTo(
                        root.get("issueAt"), end.atTime(23, 59, 59)
                ));
            }
        }

        q.select(b.count(root)).where(predicates.toArray(Predicate[]::new));

        return s.createQuery(q).getSingleResult();
    }

    private String generateCode() {
        return "INV-" + System.currentTimeMillis() + 1;
    }

    @Override
    public long count(Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Long> q = b.createQuery(Long.class);
        Root<Invoice> root = q.from(Invoice.class);
        q.select(b.count(root));

        List<Predicate> predicates = new ArrayList<>();

        if (params != null) {
            String kw = params.get("kw");
            if (kw != null && !kw.isEmpty()) {
                predicates.add(b.like(root.get("code"), "%" + kw + "%"));
            }

            String userIdStr = params.get("userId");
            if (userIdStr != null && !userIdStr.isEmpty()) {
                try {
                    int userId = Integer.parseInt(userIdStr);
                    predicates.add(b.equal(root.get("user").get("id"), userId));
                } catch (NumberFormatException ex) {
                }
            }

            String bookingIdStr = params.get("bookingId");
            if (bookingIdStr != null && !bookingIdStr.isEmpty()) {
                try {
                    int bookingId = Integer.parseInt(bookingIdStr);
                    predicates.add(b.equal(root.get("booking").get("id"), bookingId));
                } catch (NumberFormatException ex) {
                }
            }
        }

        if (!predicates.isEmpty()) {
            q.where(predicates.toArray(Predicate[]::new));
        }

        return s.createQuery(q).getSingleResult();
    }

}
