package org.example.services;

import java.sql.SQLException;
import java.util.List;

public interface IServices<T> {
    void add(T t) throws SQLException;
    void delete(int id) throws SQLException;
    void update(T t) throws SQLException;
    T findById(int id) throws SQLException;
    List<T> findAll() throws SQLException;
}
