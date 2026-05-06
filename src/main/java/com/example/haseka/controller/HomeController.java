package com.example.haseka.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @Autowired
    private ReviewService reviewService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("reviews", reviewService.getApprovedReviews());
        return "home";
    }
}