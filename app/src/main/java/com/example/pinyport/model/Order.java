package com.example.pinyport.model;

public class Order {
    private String orderId;
    private String date;
    private String time;
    private String customerName;
    private String status;
    private int itemsCount;
    private String value;

    public Order(String orderId, String date, String time, String customerName, String status, int itemsCount, String value) {
        this.orderId = orderId;
        this.date = date;
        this.time = time;
        this.customerName = customerName;
        this.status = status;
        this.itemsCount = itemsCount;
        this.value = value;
    }

    // Getters for all fields
    public String getOrderId() {
        return orderId;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getStatus() {
        return status;
    }

    public int getItemsCount() {
        return itemsCount;
    }

    public String getValue() {
        return value;
    }

}
