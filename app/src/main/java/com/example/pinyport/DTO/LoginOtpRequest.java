package com.example.pinyport.DTO;

public class LoginOtpRequest {
    private String user_id;
    private String otp;

    public LoginOtpRequest(String userId, String otp) {
        this.user_id = userId;
        this.otp = otp;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getOtp() {
        return otp;
    }
}
