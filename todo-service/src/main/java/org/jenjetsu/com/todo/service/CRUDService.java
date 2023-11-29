package org.jenjetsu.com.todo.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface CRUDService<E, ID extends Serializable> {

    public E create(E raw);
    public List<E> createAll(Collection<E> rawCollection);
    public E readById(ID id);
    public List<E> readAll();
    public E update(E newEntity);
    public void delete(E removeEntity);
    public void deleteById(ID id);
    public boolean existsById(ID id);
}
