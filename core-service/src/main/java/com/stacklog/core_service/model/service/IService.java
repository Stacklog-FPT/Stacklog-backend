package com.stacklog.core_service.model.service;

import java.util.List;
import java.util.function.Predicate;

public interface IService<E> {
    
    public List<E> getAllByUserId(String token);

    public List<E> searchByFields(Predicate<E> p);

    public E getById(String id, String token);

    public E save(E e, String token);

    public E delete(String id, String token);

}
