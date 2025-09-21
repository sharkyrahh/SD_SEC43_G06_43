package com.example.smartparkparkingsystem.ui.dashboard;

public class Parking {
    private String slotName;
    private String status;
    private String car;

    public Parking(String slotName, String status, String car) {
        this.slotName = slotName;
        this.status = status;
        this.car = car;
    }

    public String getSlotName() { return slotName; }
    public String getStatus() { return status; }
    public String getCar() { return car; }
}
