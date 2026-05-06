package com.example.haseka.model;

import jakarta.persistence.*;

@Entity
@Table(name="room")

public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String type;
    private double price;
    private double discountPercent;   // e.g. 15.0 means 15% off
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "package_id")
    private Package pkg;

    // Calculated field — not stored in DB
    public double getDiscountedPrice() {
        return price - (price * discountPercent / 100.0);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public double getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(double discountPercent) { this.discountPercent = discountPercent; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public Package getPkg() { return pkg; }
    public void setPkg(Package pkg) { this.pkg = pkg; }

}
