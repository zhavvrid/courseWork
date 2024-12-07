package com.example.server.DAO;

import com.example.server.Interfaces.DAO;
import com.example.server.Models.Entities.Role;
import com.example.server.Models.Entities.User;
import com.example.server.Utility.HibernateUtil;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class UserDAO implements DAO<User> {

    private final SessionFactory sessionFactory;

    public UserDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void insert(User user) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.save(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }
@Override
    public void update(User user) {
    Transaction transaction = null;
    try (Session session = sessionFactory.openSession()) {
        transaction = session.beginTransaction();
        session.update(user);
        transaction.commit();
    } catch (Exception e) {
        if (transaction != null) transaction.rollback();
        e.printStackTrace();
    }
    }



    @Override
    public void delete(int id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            User user = session.get(User.class, id);
            if (user != null) {
                session.delete(user);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public User find(int id) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "FROM User u LEFT JOIN FETCH u.role WHERE u.id = :id";
            return session.createQuery(hql, User.class)
                    .setParameter("id", id)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public List<User> findAll() {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            List<User> users = session.createQuery("FROM User u LEFT JOIN FETCH u.role r LEFT JOIN FETCH r.users", User.class).getResultList();
            tx.commit();
            return users;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public User   findByLogin(String login) {
        Transaction transaction = null;
        User user = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Query<User> query = session.createQuery(
                    "from User u left join fetch u.role where u.login = :login", User.class);
            query.setParameter("login", login);
            user = query.uniqueResult();
            if (user != null) {
                // Force initialization of the role to prevent lazy loading issues
                Hibernate.initialize(user.getRole());
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
        return user;
    }

}
