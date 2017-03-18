package com.alvarlagerlof.koda.Comments;

/**
 * Created by alvar on 2016-07-02.
 */

class CommentsObject {
    String author;
    String date;
    String comment;
    int type;

    CommentsObject(String author, String date, String comment, int type) {
        this.author = author;
        this.date = date;
        this.comment = comment;
        this.type = type;
    }

}