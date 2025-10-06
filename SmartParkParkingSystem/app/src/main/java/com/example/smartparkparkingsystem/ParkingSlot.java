package com.example.smartparkparkingsystem; // ⚠️ Ubah ikut package kamu sendiri

/**
 * Model class for a parking slot.
 * Compatible with Firebase Realtime Database.
 */
public class ParkingSlot {

    public String id;        // Firebase key (unique id)
    public String code;      // Example: A1, B2, C3
    public String location;  // Example: Front, Basement, Level 2
    public String status;    // available, reserved, occupied

    // Empty constructor required by Firebase
    public ParkingSlot() {
    }

    // Constructor for easy creation
    public ParkingSlot(String id, String code, String location, String status) {
        this.id = id;
        this.code = code;
        this.location = location;
        this.status = status;
    }

    // --- Getters and Setters (optional but clean for adapter) ---
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLocation() {
        return location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // --- Helper ---
    public boolean isAvailable() {
        return status != null && status.equalsIgnoreCase("available");
    }

    @Override
    public String toString() {
        return "ParkingSlot{" +
                "id='" + id + '\'' +
                ", code='" + code + '\'' +
                ", location='" + location + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
