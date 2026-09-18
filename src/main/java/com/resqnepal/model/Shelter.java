package com.resqnepal.model;

public class Shelter {
    private String shelterId;
    private String name;
    private double locX;
    private double locY;
    private int maxCapacity;
    private int currentOccupancy;
    private boolean medicalSupport;
    private boolean foodAvailable;
    private boolean waterAvailable;

    public Shelter() {}

    public Shelter(String shelterId, String name, double locX, double locY, int maxCapacity, int currentOccupancy, boolean medicalSupport, boolean foodAvailable, boolean waterAvailable) {
        this.shelterId = shelterId;
        this.name = name;
        this.locX = locX;
        this.locY = locY;
        this.maxCapacity = maxCapacity;
        this.currentOccupancy = currentOccupancy;
        this.medicalSupport = medicalSupport;
        this.foodAvailable = foodAvailable;
        this.waterAvailable = waterAvailable;
    }

    public String getShelterId() { return shelterId; }
    public void setShelterId(String shelterId) { this.shelterId = shelterId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getLocX() { return locX; }
    public void setLocX(double locX) { this.locX = locX; }

    public double getLocY() { return locY; }
    public void setLocY(double locY) { this.locY = locY; }

    public int getMaxCapacity() { return maxCapacity; }
    public void setMaxCapacity(int maxCapacity) { this.maxCapacity = maxCapacity; }

    public int getCurrentOccupancy() { return currentOccupancy; }
    public void setCurrentOccupancy(int currentOccupancy) { this.currentOccupancy = currentOccupancy; }

    public boolean isMedicalSupport() { return medicalSupport; }
    public void setMedicalSupport(boolean medicalSupport) { this.medicalSupport = medicalSupport; }

    public boolean isFoodAvailable() { return foodAvailable; }
    public void setFoodAvailable(boolean foodAvailable) { this.foodAvailable = foodAvailable; }

    public boolean isWaterAvailable() { return waterAvailable; }
    public void setWaterAvailable(boolean waterAvailable) { this.waterAvailable = waterAvailable; }

    @Override
    public String toString() {
        return "Shelter{" +
                "shelterId='" + shelterId + '\'' +
                ", name='" + name + '\'' +
                ", maxCapacity=" + maxCapacity +
                ", currentOccupancy=" + currentOccupancy +
                '}';
    }
}
