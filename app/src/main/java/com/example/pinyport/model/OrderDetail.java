package com.example.pinyport.model;

import java.util.List;

public class OrderDetail {
    private String productName;
    private String productPrice;
    private int quantity;
    private List<Topping> toppings;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
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

    public OrderDetail(String productName, String productPrice, int quantity, List<Topping> toppings) {
        this.productName = productName;
        this.productPrice = productPrice;
        this.quantity = quantity;
        this.toppings = toppings;
    }

    // Getters and setters

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
