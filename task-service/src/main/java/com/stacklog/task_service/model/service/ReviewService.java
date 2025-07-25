package com.stacklog.task_service.model.service;

import java.util.List;
import java.util.function.Predicate;

import org.springframework.stereotype.Service;

import com.stacklog.core_service.model.service.IService;
import com.stacklog.task_service.model.entities.Review;

@Service
public class ReviewService implements IService<Review> {

    @Override
    public Review delete(String id, String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public List<Review> getAllByUserId(String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllByUserId'");
    }

    @Override
    public Review getById(String id, String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getById'");
    }

    @Override
    public Review save(Review e, String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }

    @Override
    public List<Review> searchByFields(Predicate<Review> p, String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchByFields'");
    }
    
}
