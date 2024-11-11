package com.example.server.Services;

import com.example.server.DAO.DepreciationCalculationDAO;
import com.example.server.Interfaces.Service;
import com.example.server.Models.Entities.DepreciationCalculation;
import com.example.server.Utility.HibernateUtil;

import java.util.List;

public class DepreciationCalculationService implements Service<DepreciationCalculation> {

    private final DepreciationCalculationDAO depreciationCalculationDAO;

    public DepreciationCalculationService() {
        this.depreciationCalculationDAO = new DepreciationCalculationDAO(HibernateUtil.getSessionFactory());
    }

    @Override
    public void insert(DepreciationCalculation calculation) {
        depreciationCalculationDAO.insert(calculation);
    }

    @Override
    public void update(DepreciationCalculation calculation) {
        depreciationCalculationDAO.update(calculation);
    }

    @Override
    public void delete(int id) {
        depreciationCalculationDAO.delete(id);
    }

    @Override
    public DepreciationCalculation findById(int id) {
        return depreciationCalculationDAO.find(id);
    }

    @Override
    public List<DepreciationCalculation> findAll() {
        return depreciationCalculationDAO.findAll();
    }

    public List<DepreciationCalculation> findByAssetId(int assetId) {
        return depreciationCalculationDAO.findByAssetId(assetId);
    }
}
