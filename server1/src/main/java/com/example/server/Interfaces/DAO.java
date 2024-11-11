package com.example.server.Interfaces;

import java.util.List;

public interface DAO <T>{
    public void insert(T t);
    public void update(T t);
    public void delete(int id);
    public T find(int id);
    public List<T> findAll();
}
