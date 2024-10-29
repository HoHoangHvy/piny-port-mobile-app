package com.example.pinyport.model;

public class Customer {
    private String id;
    private String name;
    private String email;
    private String rank;

    // Constructor
    public Customer(String id, String name, String email, String rank) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.rank = rank;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
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

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", rank='" + rank + '\'' +
                '}';
    }
}
