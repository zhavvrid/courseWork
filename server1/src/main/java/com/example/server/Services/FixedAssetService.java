package com.example.server.Services;

import com.example.server.DAO.RoleDAO;
import com.example.server.Interfaces.Service;
import com.example.server.Models.Entities.FixedAsset;
import com.example.server.DAO.FixedAssetDAO;
import com.example.server.Utility.HibernateUtil;

import java.util.List;

public class FixedAssetService implements Service<FixedAsset> {

    private final FixedAssetDAO fixedAssetDAO;

    public FixedAssetService(FixedAssetDAO fixedAssetDAO) {
        this.fixedAssetDAO = fixedAssetDAO;
    }

    public FixedAssetService() {
        fixedAssetDAO = new FixedAssetDAO(HibernateUtil.getSessionFactory());

    }

    @Override
    public void insert(FixedAsset fixedAsset) {
        fixedAssetDAO.insert(fixedAsset);
    }

    @Override
    public void update(FixedAsset fixedAsset) {
        fixedAssetDAO.update(fixedAsset);
    }

    @Override
    public void delete(int id) {
        fixedAssetDAO.delete(id);
    }

    @Override
    public FixedAsset findById(int id) {
        return fixedAssetDAO.find(id);
    }

    @Override
    public List<FixedAsset> findAll() {
        return fixedAssetDAO.findAll();
    }
    public List<FixedAsset> searchAssets(String parameter, String value) {
        return fixedAssetDAO.searchAssets(parameter, value);
    }


    public List<FixedAsset> sortAssets(String sortBy, String sortOrder) {
        return fixedAssetDAO.sortAssets(sortBy,sortOrder);
    }
}
