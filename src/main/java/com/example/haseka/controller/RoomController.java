package com.example.haseka.controller;

import com.example.haseka.service.FileStorageService;
import com.example.haseka.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

public class RoomController {
    @Autowired
    private RoomService roomService;

    @Autowired
    private PackageService packageService; // Added this injection

    @Autowired
    private PackageRepository packageRepository; // Added this injection
    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/room")
    public String listRooms(Model model) {
        model.addAttribute("rooms", roomService.getAllRooms());
        return "room";
    }

    @GetMapping("/admin/room")
    public String manageRooms(Model model) {
        model.addAttribute("rooms", roomService.getAllRooms());
        model.addAttribute("packages", packageService.getAllPackages());
        model.addAttribute("newRoom", new Room());
        return "admin-room";
    }

    @PostMapping("/admin/room/save")
    public String saveRoom(@RequestParam String type,
                           @RequestParam double price,
                           @RequestParam double discountPercent,
                           @RequestParam int packageId,
                           @RequestParam(value = "image", required = false) MultipartFile image,
                           Model model) {
        Room room = new Room();
        room.setType(type);
        room.setPrice(price);
        room.setDiscountPercent(discountPercent);
        // Now packageRepository is available
        room.setPkg(packageRepository.findById(packageId).orElseThrow());
        try {
            String imageUrl = fileStorageService.saveImage(image, "rooms");
            if (imageUrl != null) {
                room.setImageUrl(imageUrl);
            }
        } catch (Exception e) {
            model.addAttribute("rooms", roomService.getAllRooms());
            model.addAttribute("packages", packageService.getAllPackages());
            model.addAttribute("newRoom", room);
            model.addAttribute("error", e.getMessage());
            return "admin-room";
        }
        roomService.save(room);
        return "redirect:/admin/room";
    }

    @GetMapping("/admin/room/edit/{id}")
    public String editRoom(@PathVariable int id, Model model) {
        model.addAttribute("room", roomService.getById(id));
        model.addAttribute("packages", packageService.getAllPackages());
        return "admin-edit-room";
    }

    @PostMapping("/admin/room/update/{id}")
    public String updateRoom(@PathVariable int id,
                             @RequestParam String type,
                             @RequestParam double price,
                             @RequestParam double discountPercent,
                             @RequestParam int packageId,
                             @RequestParam(value = "image", required = false) MultipartFile image,
                             Model model) {
        Room room = roomService.getById(id);
        room.setType(type);
        room.setPrice(price);
        room.setDiscountPercent(discountPercent);
        room.setPkg(packageRepository.findById(packageId).orElseThrow());
        try {
            String imageUrl = fileStorageService.saveImage(image, "rooms");
            if (imageUrl != null) {
                room.setImageUrl(imageUrl);
            }
        } catch (Exception e) {
            model.addAttribute("room", room);
            model.addAttribute("packages", packageService.getAllPackages());
            model.addAttribute("error", e.getMessage());
            return "admin-edit-room";
        }
        roomService.save(room);
        return "redirect:/admin/room";
    }

    @PostMapping("/admin/room/delete/{id}")
    public String deleteRoom(@PathVariable int id) {
        roomService.delete(id);
        return "redirect:/admin/room";
    }
}
