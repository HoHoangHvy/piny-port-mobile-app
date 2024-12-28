package com.example.pinyport.DTO;

public class CustomerRequest {
    private String full_name;
    private String phone_number;
    private String gender;

    public CustomerRequest(String name, String phone, String gender) {
        this.full_name = name;
        this.phone_number = phone;
        this.gender = gender;
    }

    // Getters and setters (optional, but recommended)
    public String getName() {
        return full_name;
    }

    public void setName(String name) {
        this.full_name = name;
    }

    public String getPhone() {
        return phone_number;
    }

    public void setPhone(String phone) {
        this.phone_number = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}