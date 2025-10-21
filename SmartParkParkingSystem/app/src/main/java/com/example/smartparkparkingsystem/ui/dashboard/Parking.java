package com.example.smartparkparkingsystem.ui.dashboard;

public class Parking {
    private String name;
    private String location;
    private String status;
    private String reservedby;  // Note: lowercase 'b' to match Firebase
    private String type;

    // Required empty constructor
    public Parking() {
    }

    // Getters and setters - field names must match Firebase exactly
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

    public String getReservedby() {
        return reservedby;
    }

    public void setReservedby(String reservedby) {
        this.reservedby = reservedby;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}