package com.example.server.Services;/*
package com.example.server.Services;

import server.DAO.AdminDAO;
import server.Interfaces.Service;
import server.Models.Entities.Admin;
import server.Utility.HibernateUtil;

import java.util.List;

public class AdminService implements Service<Admin> {

    private final AdminDAO adminDAO;

    public AdminService() {
        this.adminDAO = new AdminDAO(HibernateUtil.getSessionFactory());
    }

    @Override
    public void insert(Admin admin) {
        adminDAO.insert(admin);
    }

    @Override
    public void update(Admin admin) {
        adminDAO.update(admin);
    }

    @Override
    public void delete(int id) {
        adminDAO.delete(id);
    }

    @Override
    public Admin findById(int id) {
        return adminDAO.find(id);
    }

    @Override
    public List<Admin> findAll() {
        return adminDAO.findAll();
    }

    // Метод для поиска администратора по логину
    public Admin findByLogin(String login) {
        return adminDAO.findByLogin(login);
    }
}
*/
