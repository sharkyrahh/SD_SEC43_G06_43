package com.example.smartparkparkingsystem;

import androidx.annotation.NonNull;

/**
 * Model class for a parking slot.
 * Compatible with Firebase Realtime Database.
 */
public class ParkingSlot {

    private String name;        // Slot name (A1, A2, etc.) - matches Firebase
    private String location;    // Location description
    private String status;      // Available, Full, Reserved
    private String type;        // Car, Motorcycle, VIP
    private String reservedby;  // Plate number or reserved by info

    // Required empty constructor for Firebase
    public ParkingSlot() {
    }

    // Constructor for easy creation
    public ParkingSlot(String name, String location, String status, String type, String reservedby) {
        this.name = name;
        this.location = location;
        this.status = status;
        this.type = type;
        this.reservedby = reservedby;
    }

    // --- Getters and Setters ---
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReservedby() {
        return reservedby;
    }

    public void setReservedby(String reservedby) {
        this.reservedby = reservedby;
    }

    // --- Helper methods ---
    public boolean isAvailable() {
        return status != null && status.equalsIgnoreCase("available");
    }

    public boolean isReserved() {
        return status != null && status.equalsIgnoreCase("reserved");
    }

    public boolean isFull() {
        return status != null && status.equalsIgnoreCase("full");
    }

    @NonNull
    @Override
    public String toString() {
        return "ParkingSlot{" +
                "name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", status='" + status + '\'' +
                ", type='" + type + '\'' +
                ", reservedby='" + reservedby + '\'' +
                '}';
    }
}