package com.example.haseka.model;

import jakarta.persistence.*;

import java.util.List;
@Entity
@Table(name="package")git
public class Package {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private double price;
    private String description;
    private double discountPercent;   // e.g. 10.0 means 10% off
    private String imageUrl;

    @OneToMany(mappedBy = "pkg", cascade = CascadeType.ALL)
    private List<Room> rooms;

    // Calculated field — not stored in DB
    public double getDiscountedPrice() {
        return price - (price * discountPercent / 100.0);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(double discountPercent) { this.discountPercent = discountPercent; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public List<Room> getRooms() { return rooms; }
    public void setRooms(List<Room> rooms) { this.rooms = rooms; }
}
