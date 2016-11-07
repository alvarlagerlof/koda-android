package com.alvarlagerlof.koda.Comments;

/**
 * Created by alvar on 2016-07-02.
 */
public class CommentsObject {
    private String mBy;
    private String mDate;
    private String mComment;

    public CommentsObject(String by, String date, String comment) {
        mBy = by;
        mDate = date;
        mComment = comment;
    }



    // Date
    public String getmDate() {
        return mDate;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }


    // By
    public String getmBy() {
        return mBy;
    }

    public void setmBy(String mBy) {
        this.mBy = mBy;
    }


    // Comment
    public String getmComment() {
        return mComment;
    }

    public void setmComment(String mComment) {
        this.mComment = mComment;
    }




}