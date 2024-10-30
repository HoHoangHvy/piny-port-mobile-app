package com.example.pinyport.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Chat implements Serializable {
    private String id;
    private String name;
    private String latestMessage;

    private List<Comment> commentList;
    public Chat(String name, String latestMessage) {
        this.name = name;
        this.latestMessage = latestMessage;
    }

    public void loadCommentList() {
        commentList = new ArrayList<>();
        commentList.add(new Comment("John", "Hello", "incoming"));
        commentList.add(new Comment("Jane", "Hi", "outgoing"));
        commentList.add(new Comment("John", "How are you?", "incoming"));
    }

    public String getName() {
        return name;
    }

    public String getLatestMessage() {
        return latestMessage;
    }
}