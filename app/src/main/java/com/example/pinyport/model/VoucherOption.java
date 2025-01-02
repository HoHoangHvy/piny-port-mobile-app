package com.example.pinyport.model;

public class VoucherOption {
    private String id;
    private String name;

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDiscount_amount() {
        return discount_amount;
    }

    public void setDiscount_amount(double discount_amount) {
        this.discount_amount = discount_amount;
    }

    public double getDiscount_percent() {
        return discount_percent;
    }

    public void setDiscount_percent(double discount_percent) {
        this.discount_percent = discount_percent;
    }

    public String getDiscount_type() {
        return discount_type;
    }

    public void setDiscount_type(String discount_type) {
        this.discount_type = discount_type;
    }

    public void setDiscount(double discount) {
        if(discount_type.equals("percent")) {
            this.discount_percent = discount;
        } else {
            this.discount_amount = discount;
        }
    }

    private double discount_amount;
    private double discount_percent;
    private String discount_type;

    public VoucherOption(String id, String name, double discount_amount, double discount_percent, String discount_type) {
        this.id = id;
        this.name = name;
        this.discount_amount = discount_amount;
        this.discount_percent = discount_percent;
        this.discount_type = discount_type;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getDiscount() {
        if(discount_type.equals("percent")) {
            return discount_percent;
        } else {
            return discount_amount;
        }
    }
}