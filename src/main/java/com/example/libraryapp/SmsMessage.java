package com.example.libraryapp;

import lombok.Data;

@Data
public class SmsMessage {
    private String phone;
    private String message;

    public SmsMessage(String phone, String message) {
        this.phone = phone;
        this.message = message;
    }
}
