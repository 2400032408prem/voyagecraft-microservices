package com.voyagecraft.packageservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "travel_packages")
public class TravelPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String destination;
    private double price;
    private int capacity;

    public TravelPackage() {}

    public TravelPackage(String destination, double price, int capacity) {
        this.destination = destination;
        this.price = price;
        this.capacity = capacity;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
}
