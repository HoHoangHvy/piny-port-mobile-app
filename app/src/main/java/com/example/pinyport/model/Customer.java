package com.example.pinyport.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Customer implements Serializable {
    private String id;
    private String customerNumber;
    private String name;
    private String phone;
    private String rank;

    private String date_registered;
    private Integer point;
    private Float totalSpending;

    public Integer getPoint() {
        return point;
    }

    public String getPointText() {
        return point.toString();
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    public Float getTotalSpending() {
        return totalSpending;
    }
    public String getTotalSpendingText() {
        return totalSpending + " VND";
    }
    public void setTotalSpending(Float totalSpending) {
        this.totalSpending = totalSpending;
    }

    // Constructor
    public Customer(String id, String customerNumber,String name, String phone, String rank, String date_registered, Integer point, Float totalSpending) {
        this.id = id;
        this.customerNumber = customerNumber;
        this.name = name;
        this.phone = phone;
        this.rank = rank;
        this.date_registered = date_registered;
        this.point = point;
        this.totalSpending = totalSpending;
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

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
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

    public String getDateRegistered() {
        return date_registered;
    }
}
