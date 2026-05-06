package com.example.haseka.controller;

import com.example.haseka.service.FileStorageService;
import com.example.haseka.service.PackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
@Controller
public class PackageController {
    @Autowired
    private PackageService packageService;
    @Autowired
    private FileStorageService fileStorageService;


    @GetMapping("/packages")
    public String listPackages(Model model) {
        model.addAttribute("packages", packageService.getAllPackages());
        return "packages";
    }

    // URL: /admin/packages
    @GetMapping("/admin/packages")
    public String managePackages(Model model) {
        model.addAttribute("packages", packageService.getAllPackages());
        model.addAttribute("newPackage", new Package());
        return "admin-packages";
    }

    @PostMapping("/admin/packages/save")
    public String savePackage(@ModelAttribute("newPackage") Package pkg,
                              @RequestParam(value = "image", required = false) MultipartFile image,
                              Model model) {
        try {
            String imageUrl = fileStorageService.saveImage(image, "packages");
            if (imageUrl != null) {
                pkg.setImageUrl(imageUrl);
            }
        } catch (Exception e) {
            model.addAttribute("packages", packageService.getAllPackages());
            model.addAttribute("newPackage", pkg);
            model.addAttribute("error", e.getMessage());
            return "admin-packages";
        }
        packageService.save(pkg);
        return "redirect:/admin/packages";
    }

    @GetMapping("/admin/packages/edit/{id}")
    public String editPackage(@PathVariable int id, Model model) {
        // Changed "pkg" to "package" to match common convention,
        // but "pkg" works fine if your HTML matches.
        model.addAttribute("pkg", packageService.getById(id));
        return "admin-edit-package";
    }

    @PostMapping("/admin/packages/update/{id}")
    public String updatePackage(@PathVariable int id,
                                @RequestParam String name,
                                @RequestParam double price,
                                @RequestParam String description,
                                @RequestParam double discountPercent,
                                @RequestParam(value = "image", required = false) MultipartFile image,
                                Model model) {
        Package pkg = packageService.getById(id);
        pkg.setName(name);
        pkg.setPrice(price);
        pkg.setDescription(description);
        pkg.setDiscountPercent(discountPercent);
        try {
            String imageUrl = fileStorageService.saveImage(image, "packages");
            if (imageUrl != null) {
                pkg.setImageUrl(imageUrl);
            }
        } catch (Exception e) {
            model.addAttribute("pkg", pkg);
            model.addAttribute("error", e.getMessage());
            return "admin-edit-package";
        }
        packageService.save(pkg);
        return "redirect:/admin/packages";
    }

    @PostMapping("/admin/packages/delete/{id}")
    public String deletePackage(@PathVariable int id) {
        packageService.delete(id);
        return "redirect:/admin/packages";
    }
}
