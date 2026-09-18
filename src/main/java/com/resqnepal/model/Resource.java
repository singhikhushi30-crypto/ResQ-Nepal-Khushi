package com.resqnepal.model;

import com.resqnepal.model.enums.ResourceStatus;

public class Resource {
    private String resourceId;
    private String name;
    private String category;
    private int quantity;
    private double locX;
    private double locY;
    private ResourceStatus status;

    public Resource() {}

    public Resource(String resourceId, String name, String category, int quantity, double locX, double locY, ResourceStatus status) {
        this.resourceId = resourceId;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.locX = locX;
        this.locY = locY;
        this.status = status;
    }

    public String getResourceId() { return resourceId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getLocX() { return locX; }
    public void setLocX(double locX) { this.locX = locX; }

    public double getLocY() { return locY; }
    public void setLocY(double locY) { this.locY = locY; }

    public ResourceStatus getStatus() { return status; }
    public void setStatus(ResourceStatus status) { this.status = status; }

    @Override
    public String toString() {
        return "Resource{" +
                "resourceId='" + resourceId + '\'' +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", status=" + status +
                '}';
    }
}
