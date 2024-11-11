package com.example.server.DAO;

import com.example.server.Interfaces.DAO;
import com.example.server.Models.Entities.DepreciationCalculation;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class DepreciationCalculationDAO implements DAO<DepreciationCalculation> {

    private final SessionFactory sessionFactory;

    public DepreciationCalculationDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void insert(DepreciationCalculation calculation) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            System.out.println("Session opened successfully.");

            transaction = session.beginTransaction();
            System.out.println("Transaction started.");

            session.save(calculation);
            System.out.println("Entity saved successfully.");

            transaction.commit();
            System.out.println("Transaction committed successfully.");
        } catch (Exception e) {
            System.out.println("Exception occurred: " + e.getMessage());
            if (transaction != null && transaction.getStatus().canRollback()) {
                System.out.println("Rolling back transaction.");
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            System.out.println("Insert method completed.");
        }
    }

    @Override
    public void update(DepreciationCalculation calculation) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.update(calculation);
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
            DepreciationCalculation calculation = session.get(DepreciationCalculation.class, id);
            if (calculation != null) {
                session.delete(calculation);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public DepreciationCalculation find(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(DepreciationCalculation.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<DepreciationCalculation> findAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<DepreciationCalculation> query = session.createQuery("from DepreciationCalculation", DepreciationCalculation.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<DepreciationCalculation> findByAssetId(int assetId) {
        try (Session session = sessionFactory.openSession()) {
            Query<DepreciationCalculation> query = session.createQuery("from DepreciationCalculation d where d.fixedAsset.id = :assetId", DepreciationCalculation.class);
            query.setParameter("assetId", assetId);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

