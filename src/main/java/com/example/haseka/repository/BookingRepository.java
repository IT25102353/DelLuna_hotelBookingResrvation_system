package com.example.haseka.repository;

import com.example.haseka.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    Optional<Booking> findByStripeSessionId(String stripeSessionId);

    @Query("""
            select count(b) > 0
            from Booking b
            where b.room.id = :roomId
              and b.checkInDate < :checkOutDate
              and b.checkOutDate > :checkInDate
            """)
    boolean existsOverlappingBooking(@Param("roomId") int roomId,
                                     @Param("checkInDate") LocalDate checkInDate,
                                     @Param("checkOutDate") LocalDate checkOutDate);

    @Query("""
            select count(b) > 0
            from Booking b
            where b.room.id = :roomId
              and b.id <> :bookingId
              and b.checkInDate < :checkOutDate
              and b.checkOutDate > :checkInDate
            """)
    boolean existsOverlappingBookingExcludingId(@Param("roomId") int roomId,
                                                @Param("bookingId") int bookingId,
                                                @Param("checkInDate") LocalDate checkInDate,
                                                @Param("checkOutDate") LocalDate checkOutDate);
}
