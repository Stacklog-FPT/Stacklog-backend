package com.stacklog.task_service.model.service;

import java.time.LocalDateTime;
import java.util.List;

public interface IService<E> {
    final LocalDateTime CURRENT_TIME = LocalDateTime.now();

    public List<E> getAll();
    
    public E getById(Long id);

    public E save(E e);

    public E remove(Long id);

}
