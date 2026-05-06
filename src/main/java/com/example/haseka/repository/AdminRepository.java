package com.example.haseka.repository;

import com.example.haseka.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Integer> {
    Admin findByUsernameAndPassword(String username, String password);
    boolean existsByUsername(String username);
}