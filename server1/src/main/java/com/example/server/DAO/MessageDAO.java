package com.example.server.DAO;

import com.example.server.Interfaces.DAO;
import com.example.server.Models.Entities.Message;
import com.example.server.Models.Entities.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class MessageDAO implements DAO<Message> {

    private final SessionFactory sessionFactory;

    public MessageDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void insert(Message message) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = sessionFactory.openSession();  // Создаем сессию
            transaction = session.beginTransaction();  // Начинаем транзакцию
            session.save(message);  // Сохраняем сообщение
            transaction.commit();  // Подтверждаем изменения
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();  // Откатываем изменения при ошибке
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();  // Закрываем сессию после работы
            }
        }
    }

    @Override
    public void update(Message message) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.update(message);
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
            Message message = session.get(Message.class, id);
            if (message != null) {
                session.delete(message);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public Message find(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Message.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    @Override
    public List<Message> findAll() {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            List<Message> messages = session.createQuery("FROM Message", Message.class).getResultList();
            tx.commit();
            return messages;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public List<Message> findMessagesBetweenUsers(User sender, User receiver) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            Query<Message> query = session.createQuery("FROM Message WHERE (sender.id = :senderId AND receiver.id = :receiverId) " +
                    "OR (sender.id = :receiverId AND receiver.id = :senderId)", Message.class);
            query.setParameter("senderId", sender.getId());
            query.setParameter("receiverId", receiver.getId());
            List<Message> messages = query.getResultList();
            tx.commit();
            return messages;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Message> findMessagesByUser(User user) {
        try (Session session = sessionFactory.openSession()) {
            Query<Message> query = session.createQuery(
                    "FROM Message WHERE sender.id = :userId OR receiver.id = :userId", Message.class);
            query.setParameter("userId", user.getId());

            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public boolean updateMessageReadStatus(int messageId, boolean isRead) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            // Обновляем статус is_read
            Query query = session.createQuery("UPDATE Message SET isReadStatus = :isRead WHERE id = :messageId");
            query.setParameter("isRead", isRead);
            query.setParameter("messageId", messageId);

            int rowsUpdated = query.executeUpdate(); // Возвращает количество измененных строк
            transaction.commit();

            return rowsUpdated > 0;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            return false;
        }
    }



}
