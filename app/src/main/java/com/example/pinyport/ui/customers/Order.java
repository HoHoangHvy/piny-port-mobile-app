package com.example.pinyport.ui.customers;

public class Order {
    private String orderId;
    private String date;
    private String time;
    private String status;
    private String amount;

    public Order(String orderId, String date, String time, String status, String amount) {
        this.orderId = orderId;
        this.date = date;
        this.time = time;
        this.status = status;
        this.amount = amount;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getStatus() {
        return status;
    }

    public String getAmount() {
        return amount;
    }
}
