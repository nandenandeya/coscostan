package coscostan.dao;

import java.util.List;

public interface DAO<T> {
    // CREATE
    boolean insert(T entity);
    
    // READ
    T getById(int id);
    List<T> getAll();
    
    // UPDATE
    boolean update(T entity);
    
    // DELETE
    boolean delete(int id);
}