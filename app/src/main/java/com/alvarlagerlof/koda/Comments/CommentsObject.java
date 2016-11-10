package com.alvarlagerlof.koda.Comments;

/**
 * Created by alvar on 2016-07-02.
 */

class CommentsObject {
    String by;
    String date;
    String comment;
    int type;

    CommentsObject(String by, String date, String comment, int type) {
        this.by = by;
        this.date = date;
        this.comment = comment;
        this.type = type;
    }

}