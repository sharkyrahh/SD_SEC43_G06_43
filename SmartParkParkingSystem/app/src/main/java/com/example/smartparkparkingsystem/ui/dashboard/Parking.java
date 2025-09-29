package com.example.smartparkparkingsystem.ui.dashboard;

public class Parking {

    private String Name;
    private String Status;
    private String Location;
    private String parkingtype;
    private String Reservedby;

    public Parking() {
        // Empty constructor required for Firebase
    }

    public Parking(String Name, String Status, String Location, String parkingtype, String Reservedby) {
        this.Name = Name;
        this.Status = Status;
        this.Location = Location;
        this.parkingtype = parkingtype;
        this.Reservedby = Reservedby;
    }

    public String getName() { return Name; }
    public void setName(String Name) { this.Name = Name; }

    public String getStatus() { return Status; }
    public void setStatus(String Status) { this.Status = Status; }

    public String getLocation() { return Location; }
    public void setLocation(String Location) { this.Location = Location; }

    public String getParkingtype() { return parkingtype; }
    public void setParkingtype(String parkingtype) { this.parkingtype = parkingtype; }

    public String getReservedby() { return Reservedby; }
    public void setReservedby(String Reservedby) { this.Reservedby = Reservedby; }
}
