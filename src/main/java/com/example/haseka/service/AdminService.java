package com.example.haseka.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {
    @Autowired
    private AdminRepository repo;

    public Admin login(String username, String password) {
        return repo.findByUsernameAndPassword(username, password);
    }
    public List<Admin> getAllAdmins() { return repo.findAll(); }
    public void save(Admin admin) { repo.save(admin); }
    public void delete(int id) { repo.deleteById(id); }
    public Admin getById(int id) { return repo.findById(id).orElseThrow(); }
    public boolean usernameExists(String username) { return repo.existsByUsername(username); }
}
