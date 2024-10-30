package com.example.pinyport.model;

public class Comment {
    private String id;
    private String name;
    private String comment;

    private String direction;


    public Comment(String name, String comment, String direction) {
        this.name = name;
        this.direction = direction;
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public String getComment() {
        return comment;
    }

}
