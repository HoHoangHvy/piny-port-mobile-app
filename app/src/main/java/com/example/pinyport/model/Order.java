package com.example.pinyport.model;

import java.io.Serializable;

public class Order implements Serializable {
    private String orderId;
    private String orderNumber;
    private String date;
    private String time;
    private String customerName;
    private String status;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    private String paymentStatus;
    private String customerId;

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    private int totalItems;

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setItemsCount(int itemsCount) {
        this.itemsCount = itemsCount;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    private int itemsCount;
    private double value;

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }


    public Order(String orderId, String orderNumber, String date, String time, String customerName, String status, int itemsCount, double value, String paymentStatus, String customerId) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.date = date;
        this.time = time;
        this.customerName = customerName;
        this.status = status;
        this.itemsCount = itemsCount;
        this.value = value;
        this.paymentStatus = paymentStatus;
        this.customerId = customerId;
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

    public double getValue() {
        return value;
    }
}
