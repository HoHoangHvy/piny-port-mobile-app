package com.example.pinyport.model;

import java.util.List;

public class OrderDetail {
    private String productName;
    private double totalPrice;
    private int quantity;
    private String size;
    private String notes;
    private List<Topping> toppings;

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    private String productImageUrl; // Add this field
    private String productId;

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public OrderDetail(String productId, String productName, double totalPrice, int quantity, List<Topping> toppings, String productImageUrl, String size, String notes) {
        this.productId = productId;
        this.productName = productName;
        this.totalPrice = totalPrice;
        this.quantity = quantity;
        this.toppings = toppings;
        this.productImageUrl = productImageUrl;
        this.size = size;
        this.notes = notes;
    }

    // Getters and setters
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public List<Topping> getToppings() {
        return toppings;
    }

    public void setToppings(List<Topping> toppings) {
        this.toppings = toppings;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

}