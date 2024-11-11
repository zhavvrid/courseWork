package com.example.server.Services;

import com.example.server.DAO.RoleDAO;
import com.example.server.Interfaces.Service;
import com.example.server.Models.Entities.Role;
import com.example.server.Utility.HibernateUtil;

import java.util.List;

public class RoleService implements Service<Role> {

    private final RoleDAO roleDAO;

    public RoleService() {
        roleDAO = new RoleDAO(HibernateUtil.getSessionFactory());
    }

    @Override
    public void insert(Role role) {
        roleDAO.insert(role);
    }

    @Override
    public void update(Role role) {
        roleDAO.update(role);
    }

    @Override
    public void delete(int id) {
        roleDAO.delete(id);
    }

    @Override
    public Role findById(int id) {
        return roleDAO.find(id);
    }

    @Override
    public List<Role> findAll() {
        return roleDAO.findAll();
    }
}
