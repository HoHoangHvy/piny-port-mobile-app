package com.example.pinyport.model;

import com.google.gson.annotations.SerializedName;

public class Topping {
    @SerializedName("topping_id")
    private String toppingId;

    @SerializedName("name")
    private String name;

    @SerializedName("price")
    private double price;

    // Constructor
    public Topping(String toppingId, String name, double price) {
        this.toppingId = toppingId;
        this.name = name;
        this.price = price;
    }

    // Getters and Setters
    public String getId() {
        return toppingId;
    }

    public void setId(String toppingId) {
        this.toppingId = toppingId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Topping{" +
                "toppingId='" + toppingId + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}