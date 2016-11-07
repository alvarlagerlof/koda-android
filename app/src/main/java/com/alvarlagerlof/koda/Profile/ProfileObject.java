package com.alvarlagerlof.koda.Profile;

public class ProfileObject {
    String publicId;
    String title;
    String author;
    String description;
    String date;
    String likeCount;
    String commentCount;
    String charCount;
    Boolean liked;
    String numberOfProjects;

    ProfileObject(String publicId, String title, String author, String description, String date, String likeCount, String commentCount, String charCount, Boolean liked, String numberOfProjects) {
        this.publicId = publicId;
        this.title = title;
        this.author = author;
        this.description = description;
        this.date = date;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.charCount = charCount;
        this.liked = liked;
        this.numberOfProjects = numberOfProjects;
    }

}