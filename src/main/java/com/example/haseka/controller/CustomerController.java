package com.example.haseka.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "customer-register";
    }

    @PostMapping("/save")
    public String saveCustomer(@ModelAttribute("customer") Customer customer,
                               HttpSession session,
                               Model model) {
        if (customerService.emailExists(customer.getEmail())) {
            model.addAttribute("customer", customer);
            model.addAttribute("error", "An account already exists with this email.");
            return "customer-register";
        }
        customerService.saveCustomer(customer);
        session.setAttribute("customerId", customer.getId());
        session.setAttribute("customerName", customer.getName());
        return "redirect:/bookings/new";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "customer-login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        Customer customer = customerService.login(email, password);
        if (customer == null) {
            redirectAttributes.addFlashAttribute("error", "Invalid email or password.");
            return "redirect:/customers/login";
        }

        session.setAttribute("customerId", customer.getId());
        session.setAttribute("customerName", customer.getName());
        return "redirect:/bookings/new";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("customerId");
        session.removeAttribute("customerName");
        return "redirect:/";
    }
}
