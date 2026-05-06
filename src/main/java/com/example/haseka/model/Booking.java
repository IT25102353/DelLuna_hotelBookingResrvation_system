package com.example.haseka.model;

import jakarta.persistence.*;

import java.time.LocalDate;


@Entity
@Table(name="booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String customerName;
    private String email;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String paymentStatus = "PENDING_PAYMENT";
    private String stripeSessionId;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne
    @JoinColumn(name = "package_id")
    private Package pkg;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public String getStripeSessionId() { return stripeSessionId; }
    public void setStripeSessionId(String stripeSessionId) { this.stripeSessionId = stripeSessionId; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }
    public Package getPkg() { return pkg; }
    public void setPkg(Package pkg) { this.pkg = pkg; }

    public double getTotalPrice() {
        double roomPrice = room != null ? room.getDiscountedPrice() : 0.0;
        double packagePrice = pkg != null ? pkg.getDiscountedPrice() : 0.0;
        return roomPrice + packagePrice;
    }

}
