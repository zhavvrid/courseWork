package com.example.server.DAO;

import com.example.server.Interfaces.DAO;
import com.example.server.Models.Entities.Report;
import com.example.server.Models.Entities.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class ReportDAO implements DAO<Report> {

    private final SessionFactory sessionFactory;

    public ReportDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void insert(Report report) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.save(report);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public void update(Report report) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.update(report);
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
            Report report = session.get(Report.class, id);
            if (report != null) {
                session.delete(report);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public Report find(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Report.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Report> findAll() {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            List<Report> reports = session.createQuery("FROM Report r LEFT JOIN FETCH r.createdBy u", Report.class).getResultList();
            tx.commit();
            return reports;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Получение отчета по типу
    public Report findByReportType(String reportType) {
        Transaction transaction = null;
        Report report = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Query<Report> query = session.createQuery("from Report r where r.reportType = :reportType", Report.class);
            query.setParameter("reportType", reportType);
            report = query.uniqueResult();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
        return report;
    }

    // Получение отчетов по типу
    public List<Report> findReportsByType(String reportType) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            List<Report> reports = session.createQuery("FROM Report r WHERE r.reportType = :reportType", Report.class)
                    .setParameter("reportType", reportType)
                    .getResultList();
            tx.commit();
            return reports;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Получение отчетов, созданных пользователем
    public List<Report> findReportsByUser(User user) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            List<Report> reports = session.createQuery("FROM Report r WHERE r.createdBy = :user", Report.class)
                    .setParameter("user", user)
                    .getResultList();
            tx.commit();
            return reports;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
