package com.stacklog.schedule_service.Model.Repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stacklog.schedule_service.Model.Entities.Slot;

@Repository
public interface SlotRepo extends JpaRepository<Slot, String> {

    List<Slot> findByUserId(String currentUserId);
    
}
