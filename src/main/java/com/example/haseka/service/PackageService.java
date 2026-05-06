package com.example.haseka.service;

import com.example.haseka.repository.PackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class PackageService {
    @Autowired
    private PackageRepository repo;

    public List<Package> getAllPackages() { return repo.findAll(); }
    public Package getById(int id) { return repo.findById(id).orElseThrow(); }
    public void save(Package pkg) { repo.save(pkg); }
    public void delete(int id) { repo.deleteById(id); }
}
