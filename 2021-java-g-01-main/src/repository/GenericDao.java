package repository;

import java.util.List;

// generic interface of common database methods
public interface GenericDao<T> {
    public List<T> findAll();  
    public <U> T get(U id);
    public T update(T object);
    public void delete(T object);
    public void insert(T object);
    public <U> boolean exists(U id);
}
