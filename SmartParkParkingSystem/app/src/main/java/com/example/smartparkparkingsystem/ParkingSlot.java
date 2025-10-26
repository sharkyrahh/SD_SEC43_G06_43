package com.example.smartparkparkingsystem;

import androidx.annotation.NonNull;

public class ParkingSlot {

    private String name;
    private String location;
    private String status;
    private String type;
    private String reservedby;

    public ParkingSlot() {
    }

    public ParkingSlot(String name, String location, String status, String type, String reservedby) {
        this.name = name;
        this.location = location;
        this.status = status;
        this.type = type;
        this.reservedby = reservedby;
    }

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