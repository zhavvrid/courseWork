package com.example.server.Services;

import com.example.server.DAO.MessageDAO;
import com.example.server.Interfaces.Service;
import com.example.server.Models.Entities.Message;
import com.example.server.Models.Entities.Message;
import com.example.server.Models.Entities.MessageIterator;
import com.example.server.Models.Entities.User;
import com.example.server.Utility.HibernateUtil;

import java.util.List;

public class MessageService implements Service<Message> {
    static MessageDAO messageDAO;

    public MessageService() {
        messageDAO = new MessageDAO(HibernateUtil.getSessionFactory());
    }

    public void insert(Message message) {
        messageDAO.insert(message);
    }

    public void update(Message message) {
        messageDAO.update(message);
    }

    public void delete(int id) {
        messageDAO.delete(id);
    }

    public Message findById(int id) {
        return messageDAO.find(id);
    }

    public List<Message> findAll() {
        return messageDAO.findAll();
    }

    public List<Message> getMessagesBetweenUsers(User sender, User receiver) {
        return messageDAO.findMessagesBetweenUsers(sender, receiver);
    }
    public List<Message> findMessagesByUser(User user) {
        return messageDAO.findMessagesByUser(user);  // Этот метод должен искать все сообщения для данного пользователя
    }


    public boolean markMessageAsRead(int messageId) {
        return messageDAO.updateMessageReadStatus(messageId, true);
    }


}
