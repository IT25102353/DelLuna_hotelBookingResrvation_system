package com.example.haseka.controller;

import com.example.haseka.model.Review;
import com.example.haseka.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
@Controller
public class ReviewController {
    @Autowired
    private ReviewService reviewService;


    @GetMapping("/reviews/new")
    public String showReviewForm(Model model) {
        model.addAttribute("review", new Review());
        return "review-form";
    }

    @PostMapping("/reviews/save")
    public String saveReview(@ModelAttribute("review") Review review) {
        review.setApproved(false);
        reviewService.saveReview(review);
        return "redirect:/reviews/success";
    }

    @GetMapping("/reviews/success")
    public String success() {
        return "review-success";
    }



    @PostMapping("/admin/reviews/approve/{id}")
    public String approveReview(@PathVariable Integer id) {
        reviewService.approveReview(id);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/admin/reviews/delete/{id}")
    public String deleteReview(@PathVariable Integer id) {
        reviewService.deleteReview(id);
        return "redirect:/admin/dashboard";
    }
}
