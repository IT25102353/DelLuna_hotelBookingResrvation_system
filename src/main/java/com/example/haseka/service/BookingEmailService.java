package com.example.haseka.service;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.Locale;
@Service
public class BookingEmailService {

    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    public BookingEmailService(ObjectProvider<JavaMailSender> mailSenderProvider) {
        this.mailSenderProvider = mailSenderProvider;
    }

    public void sendConfirmationEmail(Booking booking) {
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            throw new IllegalStateException("Email is not configured. Add spring.mail settings in application.properties.");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(booking.getEmail());
        message.setSubject("Booking Confirmation");
        message.setText(buildEmailBody(booking));
        mailSender.send(message);
    }

    private String buildEmailBody(Booking booking) {
        Room room = booking.getRoom();
        Package pkg = booking.getPkg();

        double roomPrice = room != null ? room.getDiscountedPrice() : 0.0;
        double packagePrice = pkg != null ? pkg.getDiscountedPrice() : 0.0;
        NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.US);

        StringBuilder body = new StringBuilder();
        body.append("Dear ").append(booking.getCustomerName()).append(",\n\n");
        body.append("Your booking has been confirmed.\n\n");
        body.append("Booking details:\n");
        body.append("Booking ID: ").append(booking.getId()).append("\n");
        body.append("Package: ").append(pkg != null ? pkg.getName() : "Not selected")
                .append(" - ").append(currency.format(packagePrice)).append("\n");
        body.append("Room: ").append(room != null ? room.getType() : "Not selected")
                .append(" - ").append(currency.format(roomPrice)).append("\n");
        body.append("Total price: ").append(currency.format(booking.getTotalPrice())).append("\n\n");
        body.append("Thank you for choosing our hotel.");
        return body.toString();
    }
}
