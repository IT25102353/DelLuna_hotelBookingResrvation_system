package com.example.haseka.controller;

import com.example.haseka.model.Booking;
import com.example.haseka.repository.BookingRepository;
import com.example.haseka.repository.PackageRepository;
import com.example.haseka.repository.RoomRepository;
import com.example.haseka.service.CustomerService;
import com.example.haseka.service.StripeCheckoutService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/bookings")
public class BookingController {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired private RoomRepository roomRepository;
    @Autowired private PackageRepository packageRepository;
    @Autowired private CustomerService customerService;
    @Autowired private StripeCheckoutService stripeCheckoutService;

    @GetMapping("/new")
    public String showPackages(HttpSession session, Model model) {
        if (session.getAttribute("customerId") == null) {
            return "redirect:/customers/login";
        }
        model.addAttribute("packages", packageRepository.findAll());
        return "select-package";
    }

    @GetMapping("/select-room")
    public String showRooms(@RequestParam("packageId") int packageId, HttpSession session, Model model) {
        if (session.getAttribute("customerId") == null) {
            return "redirect:/customers/login";
        }
        Package pkg = packageRepository.findById(packageId).orElseThrow();
        model.addAttribute("selectedPackage", pkg);
        model.addAttribute("rooms", pkg.getRooms());
        model.addAttribute("booking", new Booking());
        return "booking-form";
    }

    @PostMapping("/save")
    public String saveBooking(@ModelAttribute("booking") Booking booking,
                              @RequestParam("roomId") int roomId,
                              @RequestParam("packageId") int packageId,
                              HttpSession session,
                              Model model) {
        Integer customerId = (Integer) session.getAttribute("customerId");
        if (customerId == null) {
            return "redirect:/customers/login";
        }

        Package pkg = packageRepository.findById(packageId).orElseThrow();
        if (booking.getCheckInDate() == null
                || booking.getCheckOutDate() == null
                || !booking.getCheckOutDate().isAfter(booking.getCheckInDate())) {
            model.addAttribute("selectedPackage", pkg);
            model.addAttribute("rooms", pkg.getRooms());
            model.addAttribute("booking", booking);
            model.addAttribute("error", "Checkout date must be after check-in date.");
            return "booking-form";
        }

        if (bookingRepository.existsOverlappingBooking(roomId, booking.getCheckInDate(), booking.getCheckOutDate())) {
            model.addAttribute("selectedPackage", pkg);
            model.addAttribute("rooms", pkg.getRooms());
            model.addAttribute("booking", booking);
            model.addAttribute("error", "This room is already booked for the selected dates.");
            return "booking-form";
        }

        Customer customer = customerService.getById(customerId);
        booking.setCustomer(customer);
        booking.setCustomerName(customer.getName());
        booking.setEmail(customer.getEmail());
        booking.setRoom(roomRepository.findById(roomId).orElseThrow());
        booking.setPkg(pkg);
        booking.setPaymentStatus("PENDING_PAYMENT");
        bookingRepository.save(booking);

        try {
            Session stripeSession = stripeCheckoutService.createCheckoutSession(booking);
            booking.setStripeSessionId(stripeSession.getId());
            bookingRepository.save(booking);
            return "redirect:" + stripeSession.getUrl();
        } catch (Exception e) {
            bookingRepository.delete(booking);
            model.addAttribute("selectedPackage", pkg);
            model.addAttribute("rooms", pkg.getRooms());
            model.addAttribute("booking", booking);
            model.addAttribute("error", "Payment could not be started: " + e.getMessage());
            return "booking-form";
        }
    }

    @GetMapping("/success")
    public String success() { return "redirect:/"; }

    @GetMapping("/payment-success")
    public String paymentSuccess(@RequestParam("session_id") String sessionId, Model model) {
        try {
            Session stripeSession = stripeCheckoutService.retrieveSession(sessionId);
            Booking booking = bookingRepository.findByStripeSessionId(sessionId).orElseThrow();
            if ("paid".equals(stripeSession.getPaymentStatus())) {
                booking.setPaymentStatus("PAID");
                bookingRepository.save(booking);
            }
            model.addAttribute("booking", booking);
            return "booking-success";
        } catch (Exception e) {
            model.addAttribute("error", "Payment confirmation failed: " + e.getMessage());
            return "booking-success";
        }
    }

    @GetMapping("/payment-cancel")
    public String paymentCancel(@RequestParam("bookingId") int bookingId, HttpSession session) {
        Integer customerId = (Integer) session.getAttribute("customerId");
        if (customerId == null) {
            return "redirect:/customers/login";
        }
        bookingRepository.findById(bookingId).ifPresent(booking -> {
            if (booking.getCustomer() != null
                    && booking.getCustomer().getId() == customerId
                    && !"PAID".equals(booking.getPaymentStatus())) {
                bookingRepository.delete(booking);
            }
        });
        return "redirect:/bookings/new";
    }

}
