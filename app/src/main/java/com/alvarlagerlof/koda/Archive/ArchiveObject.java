package com.alvarlagerlof.koda.Archive;

class ArchiveObject {
    String publicID;
    String title;
    String author;
    String description;
    String date;
    String likeCount;
    String commentCount;
    String charCount;
    Boolean liked;
    int type;

    ArchiveObject(String publicID, String title, String author, String description, String date, String likeCount, String commentCount, String charCount, Boolean liked, int type) {
        this.publicID = publicID;
        this.title = title;
        this.author = author;
        this.description = description;
        this.date = date;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.charCount = charCount;
        this.liked = liked;
        this.type = type;
    }

}