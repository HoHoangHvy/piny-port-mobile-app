package com.example.pinyport.model;

import java.util.List;

public class OrderDetail {
    private String productName;
    private double productPrice;
    private int quantity;
    private List<Topping> toppings;
    private String productImageUrl; // Add this field

    public OrderDetail(String productName, double productPrice, int quantity, List<Topping> toppings, String productImageUrl) {
        this.productName = productName;
        this.productPrice = productPrice;
        this.quantity = quantity;
        this.toppings = toppings;
        this.productImageUrl = productImageUrl;
    }

    // Getters and setters
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
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

    public static class Topping {
        private String name;
        private String price;

        public Topping(String name, String price) {
            this.name = name;
            this.price = price;
        }

        // Getters and setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }
    }
}