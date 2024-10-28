package com.example.pinyport.ui.chat;

public class Chat {
    private String name;
    private String latestMessage;

    public Chat(String name, String latestMessage) {
        this.name = name;
        this.latestMessage = latestMessage;
    }

    public String getName() {
        return name;
    }

    public String getLatestMessage() {
        return latestMessage;
    }
}