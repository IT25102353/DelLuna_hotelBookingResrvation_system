package com.example.haseka.service;

import com.example.haseka.model.Review;
import com.example.haseka.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository repo;

    public void saveReview(Review review) { repo.save(review); }

    public List<Review> getApprovedReviews() { return repo.findByApproved(true); }

    public List<Review> getAllReviews() { return repo.findAll(); }

    public Review getReviewById(Integer id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Review not found"));
    }

    public void approveReview(Integer id) {
        Review r = getReviewById(id);
        r.setApproved(true);
        repo.save(r);
    }

    public void deleteReview(Integer id) {
        repo.deleteById(id);
    }
}
