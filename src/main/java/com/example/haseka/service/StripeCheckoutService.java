package com.example.haseka.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeCheckoutService {
    @Value("${stripe.secret-key}")
    private String secretKey;

    @Value("${stripe.currency}")
    private String currency;

    @Value("${app.base-url}")
    private String baseUrl;

    public Session createCheckoutSession(Booking booking) throws StripeException {
        if (secretKey == null || secretKey.isBlank()) {
            throw new IllegalStateException("Stripe secret key is not configured.");
        }

        Stripe.apiKey = secretKey;
        long amountInCents = Math.round(booking.getTotalPrice() * 100);
        String description = booking.getRoom().getType() + " with " + booking.getPkg().getName()
                + " (" + booking.getCheckInDate() + " to " + booking.getCheckOutDate() + ")";

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setClientReferenceId(String.valueOf(booking.getId()))
                .setCustomerEmail(booking.getEmail())
                .setSuccessUrl(baseUrl + "/bookings/payment-success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(baseUrl + "/bookings/payment-cancel?bookingId=" + booking.getId())
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency(currency)
                                                .setUnitAmount(amountInCents)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Hotel Booking #" + booking.getId())
                                                                .setDescription(description)
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .putMetadata("bookingId", String.valueOf(booking.getId()))
                .build();

        return Session.create(params);
    }

    public Session retrieveSession(String sessionId) throws StripeException {
        if (secretKey == null || secretKey.isBlank()) {
            throw new IllegalStateException("Stripe secret key is not configured.");
        }

        Stripe.apiKey = secretKey;
        return Session.retrieve(sessionId);
    }
}
