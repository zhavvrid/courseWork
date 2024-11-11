package com.example.server.Interfaces;

import java.util.List;

public interface Service<T> {
    public void insert(T entity);
    public void update(T entity);
    public void delete(int id);
    public T findById(int id);
    public  List<T> findAll();
}
