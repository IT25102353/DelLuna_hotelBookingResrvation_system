package com.example.haseka.service;

import com.example.haseka.model.Booking;
import com.example.haseka.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookingService {
    @Autowired
    private BookingRepository repo;

    public List<Booking> getAllBookings() { return repo.findAll(); }
    public Booking getById(int id) { return repo.findById(id).orElseThrow(); }
    public void save(Booking booking) { repo.save(booking); }
    public void delete(int id) { repo.deleteById(id); }
    public boolean hasRoomConflict(int roomId, LocalDate checkInDate, LocalDate checkOutDate) {
        return repo.existsOverlappingBooking(roomId, checkInDate, checkOutDate);
    }
    public boolean hasRoomConflictExcludingBooking(int roomId, int bookingId, LocalDate checkInDate, LocalDate checkOutDate) {
        return repo.existsOverlappingBookingExcludingId(roomId, bookingId, checkInDate, checkOutDate);
    }
}
