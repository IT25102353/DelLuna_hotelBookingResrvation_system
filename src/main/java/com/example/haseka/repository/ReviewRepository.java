package com.example.haseka.repository;

import com.example.haseka.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByApproved(boolean approved);
}