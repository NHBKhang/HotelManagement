package com.team.hotelmanagementapp.repositories.impl;

import com.team.hotelmanagementapp.pojo.User;
import com.team.hotelmanagementapp.repositories.UserRepository;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class UserRepositoryImpl implements UserRepository {

    @Autowired
    private LocalSessionFactoryBean factory;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public List<User> findAll() {
        Session session = this.factory.getObject().getCurrentSession();
        return session.createNamedQuery("User.findAll", User.class).getResultList();
    }

    @Override
    public List<User> find(Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<User> q = b.createQuery(User.class);
        Root<User> root = q.from(User.class);
        q.orderBy(b.desc(root.get("createdAt")));
        q.select(root);

        List<Predicate> predicates = new ArrayList<>();
        if (params != null) {
            String kw = params.get("kw");

            if (kw != null && !kw.isEmpty()) {
                Predicate fullNamePredicate = b.like(
                        b.concat(root.get("firstName"), b.concat(" ", root.get("lastName"))),
                        "%" + kw + "%"
                );
                Predicate usernamePredicate = b.like(root.get("username"), "%" + kw + "%");
                Predicate emailPredicate = b.like(root.get("email"), "%" + kw + "%");
                Predicate phonePredicate = b.like(root.get("phone"), "%" + kw + "%");
                predicates.add(b.or(fullNamePredicate, usernamePredicate, emailPredicate, phonePredicate));
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
    public User getByUsername(String username) {
        try {
            Session s = this.factory.getObject().getCurrentSession();
            Query q = s.createNamedQuery("User.findByUsername", User.class);
            q.setParameter("username", username);

            return (User) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public User getById(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.find(User.class, id);
    }

    @Override
    public User createOrUpdate(User u) {
        Session s = factory.getObject().getCurrentSession();

        if (u.getId() != null) {
            s.merge(u);
        } else {
            s.persist(u);
        }

        return u;
    }

    @Override
    public long countUsers(Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Long> q = b.createQuery(Long.class);
        Root<User> root = q.from(User.class);
        q.select(b.count(root));

        List<Predicate> predicates = new ArrayList<>();
        if (params != null) {
            String kw = params.get("kw");

            if (kw != null && !kw.isEmpty()) {
                Predicate fullNamePredicate = b.like(
                        b.concat(root.get("firstName"), b.concat(" ", root.get("lastName"))),
                        "%" + kw + "%"
                );
                Predicate usernamePredicate = b.like(root.get("username"), "%" + kw + "%");
                Predicate emailPredicate = b.like(root.get("email"), "%" + kw + "%");
                Predicate phonePredicate = b.like(root.get("phone"), "%" + kw + "%");
                predicates.add(b.or(fullNamePredicate, usernamePredicate, emailPredicate, phonePredicate));
            }
        }

        if (!predicates.isEmpty()) {
            q.where(predicates.toArray(Predicate[]::new));
        }

        return s.createQuery(q).getSingleResult();
    }

    @Override
    public boolean authUser(String username, String password) {
        User u = this.getByUsername(username);
        if (u == null) {
            throw new UsernameNotFoundException("Không tìm thấy người dùng!");
        }

        return this.passwordEncoder.matches(password, u.getPassword());
    }

    @Override
    public void delete(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        User user = this.getById(id);
        if (user != null) {
            s.remove(user);
        }
    }

    @Override
    @Transactional
    public void delete(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        Session s = this.factory.getObject().getCurrentSession();
        String hql = "DELETE FROM User u WHERE u.id IN (:ids)";
        int affected = s.createMutationQuery(hql)
                .setParameterList("ids", ids)
                .executeUpdate();

        if (affected != ids.size()) {
            throw new RuntimeException("Không thể xóa hết tất cả khách hàng!");
        }
    }
}
