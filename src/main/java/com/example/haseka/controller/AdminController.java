package com.example.haseka.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class AdminController {

    @Autowired
    private AdminService adminService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private PackageService packageService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private PackageRepository packageRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private BookingEmailService bookingEmailService;

    // Authorization
    @GetMapping("/admin")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, Model model) {
        Admin user = adminService.login(username, password);
        if (user != null) return "redirect:/admin/dashboard";
        model.addAttribute("error", "Invalid credentials");
        return "login";
    }
    @GetMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();


        return "redirect:/";
    }

    @GetMapping("/admin/admin")
    public String manageAdmins(Model model) {
        model.addAttribute("admins", adminService.getAllAdmins());
        model.addAttribute("newAdmin", new Admin());
        return "admin-admin";
    }

    @PostMapping("/admin/admin/save")
    public String saveAdmin(@ModelAttribute("newAdmin") Admin admin, Model model) {
        if (adminService.usernameExists(admin.getUsername())) {
            model.addAttribute("admins", adminService.getAllAdmins());
            model.addAttribute("newAdmin", admin);
            model.addAttribute("error", "Username already exists!");
            return "admin-admin";
        }
        adminService.save(admin);
        return "redirect:/admin/admin";
    }

    @GetMapping("/admin/admin/edit/{id}")
    public String editAdmin(@PathVariable int id, Model model) {
        model.addAttribute("admin", adminService.getById(id));
        return "admin-edit-admin";
    }

    @PostMapping("/admin/admin/update/{id}")
    public String updateAdmin(@PathVariable int id,
                              @RequestParam String username,
                              @RequestParam String password) {
        Admin admin = adminService.getById(id);
        admin.setUsername(username);
        admin.setPassword(password);
        adminService.save(admin);
        return "redirect:/admin/admin";
    }

    @PostMapping("/admin/admin/delete/{id}")
    public String deleteAdmin(@PathVariable int id) {
        adminService.delete(id);
        return "redirect:/admin/admin";
    }

    // Dashboard
    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("bookings", bookingService.getAllBookings());
        model.addAttribute("reviews", reviewService.getAllReviews());
        model.addAllAttributes(buildAnalyticsData());
        return "admin";
    }

    @GetMapping("/admin/analytics")
    @ResponseBody
    public Map<String, Object> analytics() {
        return buildAnalyticsData();
    }

    private Map<String, Object> buildAnalyticsData() {
        List<Booking> bookings = bookingService.getAllBookings();
        List<Review> reviews = reviewService.getAllReviews();
        List<Package> packages = packageService.getAllPackages();
        List<Room> rooms = roomService.getAllRooms();

        double totalRevenue = bookings.stream().mapToDouble(Booking::getTotalPrice).sum();
        long approvedReviews = reviews.stream().filter(Review::isApproved).count();
        long pendingReviews = reviews.size() - approvedReviews;
        double averageRating = reviews.stream()
                .filter(review -> review.getRating() != null)
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
        double averageBookingValue = bookings.isEmpty() ? 0.0 : totalRevenue / bookings.size();

        Map<String, Long> packageBreakdown = bookings.stream()
                .collect(Collectors.groupingBy(
                        booking -> booking.getPkg() != null ? booking.getPkg().getName() : "No package",
                        LinkedHashMap::new,
                        Collectors.counting()
                ));
        Map<String, Long> topRooms = bookings.stream()
                .collect(Collectors.groupingBy(
                        booking -> booking.getRoom() != null ? booking.getRoom().getType() : "No room",
                        Collectors.counting()
                ))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
                .limit(5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (first, second) -> first,
                        LinkedHashMap::new
                ));

        Map<String, Object> analyticsData = new LinkedHashMap<>();
        analyticsData.put("bookingCount", bookings.size());
        analyticsData.put("packageCount", packages.size());
        analyticsData.put("roomCount", rooms.size());
        analyticsData.put("reviewCount", reviews.size());
        analyticsData.put("approvedReviews", approvedReviews);
        analyticsData.put("pendingReviews", pendingReviews);
        analyticsData.put("totalRevenue", totalRevenue);
        analyticsData.put("averageRating", averageRating);
        analyticsData.put("averageBookingValue", averageBookingValue);
        analyticsData.put("packageBreakdown", packageBreakdown);
        analyticsData.put("topRooms", topRooms);
        return analyticsData;
    }


    @GetMapping("/admin/bookings/edit/{id}")
    public String editBooking(@PathVariable int id, Model model) {
        model.addAttribute("booking", bookingService.getById(id));
        model.addAttribute("packages", packageService.getAllPackages());
        model.addAttribute("rooms", roomService.getAllRooms());
        return "admin-edit-booking";
    }

    @PostMapping("/admin/bookings/update/{id}")
    public String updateBooking(@PathVariable int id,
                                @RequestParam String customerName,
                                @RequestParam String email,
                                @RequestParam int roomId,
                                @RequestParam int packageId,
                                @RequestParam LocalDate checkInDate,
                                @RequestParam LocalDate checkOutDate,
                                Model model) {
        Booking b = bookingService.getById(id);
        if (!checkOutDate.isAfter(checkInDate)) {
            model.addAttribute("booking", b);
            model.addAttribute("packages", packageService.getAllPackages());
            model.addAttribute("rooms", roomService.getAllRooms());
            model.addAttribute("error", "Checkout date must be after check-in date.");
            return "admin-edit-booking";
        }
        if (bookingService.hasRoomConflictExcludingBooking(roomId, id, checkInDate, checkOutDate)) {
            model.addAttribute("booking", b);
            model.addAttribute("packages", packageService.getAllPackages());
            model.addAttribute("rooms", roomService.getAllRooms());
            model.addAttribute("error", "This room is already booked for the selected dates.");
            return "admin-edit-booking";
        }
        b.setCustomerName(customerName);
        b.setEmail(email);
        b.setCheckInDate(checkInDate);
        b.setCheckOutDate(checkOutDate);
        b.setRoom(roomService.getById(roomId));
        b.setPkg(packageService.getById(packageId));
        bookingService.save(b);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/admin/bookings/confirm-email/{id}")
    public String sendBookingConfirmationEmail(@PathVariable int id,
                                               RedirectAttributes redirectAttributes) {
        Booking booking = bookingService.getById(id);

        if (!"PAID".equals(booking.getPaymentStatus())) {
            redirectAttributes.addFlashAttribute("emailError", "Confirmation email can only be sent after payment is completed.");
            return "redirect:/admin/dashboard";
        }

        if (booking.getEmail() == null || booking.getEmail().isBlank()) {
            redirectAttributes.addFlashAttribute("emailError", "Booking does not have a customer email address.");
            return "redirect:/admin/dashboard";
        }

        try {
            bookingEmailService.sendConfirmationEmail(booking);
            redirectAttributes.addFlashAttribute("emailSuccess", "Confirmation email sent to " + booking.getEmail() + ".");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("emailError", "Could not send confirmation email: " + e.getMessage());
        }

        return "redirect:/admin/dashboard";
    }

    @PostMapping("/admin/bookings/delete/{id}")
    public String deleteBooking(@PathVariable int id) {
        bookingService.delete(id);
        return "redirect:/admin/dashboard";
    }
}
