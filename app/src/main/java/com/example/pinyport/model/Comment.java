package com.example.pinyport.model;

import java.io.Serializable;

public class Comment implements Serializable {
    private String id;
    private String name;
    private String comment;
    private String direction;

    public Comment(String id, String name, String comment, String direction) {
        this.id = id;
        this.name = name;
        this.direction = direction;
        this.comment = comment;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getComment() {
        return comment;
    }

    public String getDirection() {
        return direction;
    }
}
