package com.example.pinyport.DTO;

public class GenOtpRequest {
    private String mobile_no;

    // Constructor
    public GenOtpRequest(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    // Getter and Setter for mobile_no
    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }
}
