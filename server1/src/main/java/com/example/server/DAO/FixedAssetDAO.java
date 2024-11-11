package com.example.server.DAO;

import com.example.server.Interfaces.DAO;
import com.example.server.Models.Entities.FixedAsset;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class FixedAssetDAO implements DAO<FixedAsset> {

    private final SessionFactory sessionFactory;

    public FixedAssetDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void insert(FixedAsset fixedAsset) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.save(fixedAsset);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public void update(FixedAsset fixedAsset) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.update(fixedAsset);
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
            FixedAsset fixedAsset = session.get(FixedAsset.class, id);
            if (fixedAsset != null) {
                session.delete(fixedAsset);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public FixedAsset find(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(FixedAsset.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<FixedAsset> findAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<FixedAsset> query = session.createQuery("from FixedAsset", FixedAsset.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public List<FixedAsset> searchAssets(String parameter, String value) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<FixedAsset> query = builder.createQuery(FixedAsset.class);
            Root<FixedAsset> root = query.from(FixedAsset.class);
            switch (parameter.toLowerCase()) {
                case "категория": query.select(root).where(builder.like(root.get("category"), "%" + value + "%"));
                    break;
                case "название": query.select(root).where(builder.like(root.get("name"), "%" + value + "%"));
                    break;
                case "инвентарный номер": query.select(root).where(builder.like(root.get("inventoryNumber"), "%" + value + "%"));
                    break;
                case "метод амортизации": query.select(root).where(builder.like(root.get("depreciationMethod"), "%" + value + "%"));
                    break;
                default:
                    throw new IllegalArgumentException("Неизвестный параметр: " + parameter);
            }

            return session.createQuery(query).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public List<FixedAsset> sortAssets(String parameter, String order) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<FixedAsset> query = builder.createQuery(FixedAsset.class);
            Root<FixedAsset> root = query.from(FixedAsset.class);

            Order sortOrder;
            switch (parameter.toLowerCase()) {
                case "категория":
                    sortOrder = "ASC".equalsIgnoreCase(order) ? builder.asc(root.get("category")) : builder.desc(root.get("category"));
                    break;
                case "название":
                    sortOrder = "ASC".equalsIgnoreCase(order) ? builder.asc(root.get("name")) : builder.desc(root.get("name"));
                    break;
                case "инвентарный номер":
                    sortOrder = "ASC".equalsIgnoreCase(order) ? builder.asc(root.get("inventoryNumber")) : builder.desc(root.get("inventoryNumber"));
                    break;
                case "метод амортизации":
                    sortOrder = "ASC".equalsIgnoreCase(order) ? builder.asc(root.get("depreciationMethod")) : builder.desc(root.get("depreciationMethod"));
                    break;
                case "ликвидационная стоимость":
                    sortOrder = "ASC".equalsIgnoreCase(order) ? builder.asc(root.get("residualValue")) : builder.desc(root.get("depreciationMethod"));
                    break;
                case "начальная стоимость":
                    sortOrder = "ASC".equalsIgnoreCase(order) ? builder.asc(root.get("initialCost")) : builder.desc(root.get("initialCost"));
                    break;
                case "срок полезного использования":
                    sortOrder = "ASC".equalsIgnoreCase(order) ? builder.asc(root.get("usefulLife")) : builder.desc(root.get("usefulLife"));
                    break;
                case "остаточная стоимость":
                    sortOrder = "ASC".equalsIgnoreCase(order) ? builder.asc(root.get("residualValue")) : builder.desc(root.get("residualValue"));
                    break;
                case "дата покупки":
                    sortOrder = "ASC".equalsIgnoreCase(order) ? builder.asc(root.get("purchaseDate")) : builder.desc(root.get("purchaseDate"));
                    break;
                default:
                    throw new IllegalArgumentException("Неизвестный параметр: " + parameter);
            }

            // Apply sorting order to query
            query.select(root).orderBy(sortOrder);
            return session.createQuery(query).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
