package com.example.changetogether;

public class Post {

    private String content;

    public Post() {
        // Empty constructor needed for Firestore
    }

    public Post(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}