package com.example.server.Services;

import com.example.server.DAO.UserDAO;
import com.example.server.Interfaces.Service;
import com.example.server.Models.Entities.User;
import com.example.server.Utility.HibernateUtil;

import javax.transaction.Transactional;
import java.util.List;

public class UserService implements Service<User> {

    private static UserDAO userDAO ;

    public UserService() {
        userDAO = new UserDAO(HibernateUtil.getSessionFactory());
    }

    @Override
    public void insert(User user) {
        userDAO.insert(user);
    }

    @Override
    public void update(User user) {
        userDAO.update(user);
    }

    @Override
    public void delete(int id) {
        userDAO.delete(id);
    }

    @Override
    public User findById(int id) {
        User user = userDAO.find(id);
        return user;
    }

    @Override
    public List<User> findAll() {
        return userDAO.findAll();
    }

    public static boolean isLoginExists(String login) {
        User existingUser = userDAO.findByLogin(login);
        return existingUser != null;
    }

    public User login(String login, String password) {
        User user = userDAO.findByLogin(login);
        if (user == null) {
            return null;
        }

        if (!user.getPassword().equals(password)) {
            return null;
        }
        return user;
    }


}
