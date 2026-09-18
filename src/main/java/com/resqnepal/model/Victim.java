package com.resqnepal.model;

public class Victim {
    private int id;
    private String name;
    private int age;
    private String phone;
    private double locationX;
    private double locationY;

    public Victim() {}

    public Victim(int id, String name, int age, String phone, double locationX, double locationY) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.phone = phone;
        this.locationX = locationX;
        this.locationY = locationY;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public double getLocationX() { return locationX; }
    public void setLocationX(double locationX) { this.locationX = locationX; }

    public double getLocationY() { return locationY; }
    public void setLocationY(double locationY) { this.locationY = locationY; }

    @Override
    public String toString() {
        return "Victim{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", phone='" + phone + '\'' +
                ", locationX=" + locationX +
                ", locationY=" + locationY +
                '}';
    }
}
