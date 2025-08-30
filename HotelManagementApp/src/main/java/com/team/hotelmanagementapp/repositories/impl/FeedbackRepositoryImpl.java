package com.team.hotelmanagementapp.repositories.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.team.hotelmanagementapp.pojo.Feedback;
import com.team.hotelmanagementapp.repositories.FeedbackRepository;

@Repository
@Transactional
public class FeedbackRepositoryImpl implements FeedbackRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<Feedback> find(Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Feedback> q = b.createQuery(Feedback.class);
        Root<Feedback> root = q.from(Feedback.class);
        q.orderBy(b.desc(root.get("createdAt")));
        q.select(root).distinct(true);

        List<Predicate> predicates = new ArrayList<>();
        if (params != null) {
            String kw = params.get("kw");
            String ratingStr = params.get("rating");

            if (kw != null && !kw.isEmpty()) {
                Predicate commentPredicate = b.like(root.get("comment"), "%" + kw + "%");
                predicates.add(commentPredicate);
            }

            if (ratingStr != null && !ratingStr.isEmpty()) {
                try {
                    double rating = Double.parseDouble(ratingStr);
                    predicates.add(b.greaterThanOrEqualTo(root.get("rating"), rating));
                } catch (NumberFormatException ex) {
                    // Ignore invalid rating
                }
            }

            String userIdStr = params.get("userId");
            if (userIdStr != null && !userIdStr.isEmpty()) {
                try {
                    int userId = Integer.parseInt(userIdStr);
                    predicates.add(b.equal(root.get("user").get("id"), userId));
                } catch (NumberFormatException ex) {
                    // Ignore invalid userId
                }
            }

            String bookingIdStr = params.get("bookingId");
            if (bookingIdStr != null && !bookingIdStr.isEmpty()) {
                try {
                    int bookingId = Integer.parseInt(bookingIdStr);
                    predicates.add(b.equal(root.get("booking").get("id"), bookingId));
                } catch (NumberFormatException ex) {
                    // Ignore invalid bookingId
                }
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
    public Feedback findById(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.find(Feedback.class, id);
    }

    @Override
    public List<Feedback> findByUser(int userId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Feedback> q = b.createQuery(Feedback.class);
        Root<Feedback> root = q.from(Feedback.class);
        q.select(root);
        q.where(b.equal(root.get("user").get("id"), userId));
        q.orderBy(b.desc(root.get("createdAt")));

        return s.createQuery(q).getResultList();
    }

    @Override
    public List<Feedback> findByBooking(int bookingId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Feedback> q = b.createQuery(Feedback.class);
        Root<Feedback> root = q.from(Feedback.class);
        q.select(root);
        q.where(b.equal(root.get("booking").get("id"), bookingId));
        q.orderBy(b.desc(root.get("createdAt")));

        return s.createQuery(q).getResultList();
    }

    @Override
    public Feedback createOrUpdate(Feedback feedback) {
        Session s = this.factory.getObject().getCurrentSession();

        if (feedback.getId() == null) {
            s.persist(feedback);
        } else {
            feedback = s.merge(feedback);
        }

        return feedback;
    }

    @Override
    public void delete(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        Feedback feedback = this.findById(id);
        if (feedback != null) {
            s.remove(feedback);
        }
    }

    @Override
    public void delete(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        Session s = this.factory.getObject().getCurrentSession();
        String hql = "DELETE FROM Feedback f WHERE f.id IN (:ids)";
        int affected = s.createMutationQuery(hql)
                .setParameterList("ids", ids)
                .executeUpdate();

        if (affected != ids.size()) {
            throw new RuntimeException("Không thể xóa hết tất cả phản hồi!");
        }
    }

    @Override
    public long countFeedback(Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Long> q = b.createQuery(Long.class);
        Root<Feedback> root = q.from(Feedback.class);
        q.select(b.count(root));

        List<Predicate> predicates = new ArrayList<>();
        if (params != null) {
            String kw = params.get("kw");
            String ratingStr = params.get("rating");

            if (kw != null && !kw.isEmpty()) {
                predicates.add(b.like(root.get("comment"), "%" + kw + "%"));
            }

            if (ratingStr != null && !ratingStr.isEmpty()) {
                try {
                    double rating = Double.parseDouble(ratingStr);
                    predicates.add(b.greaterThanOrEqualTo(root.get("rating"), rating));
                } catch (NumberFormatException ex) {
                    // Ignore invalid rating
                }
            }
        }

        if (!predicates.isEmpty()) {
            q.where(predicates.toArray(Predicate[]::new));
        }

        return s.createQuery(q).getSingleResult();
    }

    @Override
    public double getAverageRating() {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Double> q = b.createQuery(Double.class);
        Root<Feedback> root = q.from(Feedback.class);
        q.select(b.avg(root.get("rating")));

        Double avg = s.createQuery(q).getSingleResult();
        return avg != null ? avg : 0.0;
    }

    @Override
    public List<Feedback> findByRatingHigherThan(double rating) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Feedback> q = b.createQuery(Feedback.class);
        Root<Feedback> root = q.from(Feedback.class);
        q.select(root);
        q.where(b.greaterThanOrEqualTo(root.get("rating"), rating));
        q.orderBy(b.desc(root.get("rating")), b.desc(root.get("createdAt")));

        return s.createQuery(q).getResultList();
    }
}
