package com.alvarlagerlof.koda.Archive;

public class ArchiveObject {
    String publicId;
    String title;
    String author;
    String description;
    String date;
    String likeCount;
    String commentCount;
    String charCount;
    Boolean liked;

    ArchiveObject(String publicId, String title, String author, String description, String date, String likeCount, String commentCount, String charCount, Boolean liked) {
        this.publicId = publicId;
        this.title = title;
        this.author = author;
        this.description = description;
        this.date = date;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.charCount = charCount;
        this.liked = liked;
    }

}