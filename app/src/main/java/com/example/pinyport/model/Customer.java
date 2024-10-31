package com.example.pinyport.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Customer implements Serializable {
    private String id;
    private String name;
    private String phone;
    private String rank;

    private String date_registered;

    // Constructor
    public Customer(String id, String name, String phone, String rank, String date_registered) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.rank = rank;
        this.date_registered = date_registered;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDate_registered() {
        return date_registered;
    }

    public void setDate_registered(String date_registered) {
        this.date_registered = date_registered;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRank() {
        return rank;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setRank(String rank) {
        this.rank = rank;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + phone + '\'' +
                ", rank='" + rank + '\'' +
                '}';
    }
    public List<Order> getOrderHistory() {
        List<Order> orderList = new ArrayList<>();
        orderList.add(new Order("MM0877", "17/06/2024", "15:45", "Nguyen Van A", "Preparing", 3, "30000 VND"));
        return orderList;
    }
}
