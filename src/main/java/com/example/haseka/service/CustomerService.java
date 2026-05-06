package com.example.haseka.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository repo;

    public List<Customer> getAllCustomers() {
        return repo.findAll();
    }

    public void saveCustomer(Customer customer) {
        repo.save(customer);
    }

    public Customer getById(int id) {
        return repo.findById(id).orElseThrow();
    }

    public Customer login(String email, String password) {
        return repo.findByEmail(email)
                .filter(customer -> Objects.equals(customer.getPassword(), password))
                .orElse(null);
    }

    public boolean emailExists(String email) {
        return repo.existsByEmail(email);
    }
}
