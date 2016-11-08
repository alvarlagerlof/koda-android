package com.alvarlagerlof.koda.MyProjects;

/**
 * Created by alvar on 2016-07-02.
 */
public class MyProjectsObject {
    String privateId;
    String publicId;
    String title;
    String updated;
    String description;
    Boolean isPublic;
    String likeCount;
    String commentCount;
    String charCount;
    String code;


    public MyProjectsObject(String privateId,
                            String publicId,
                            String title,
                            String updated,
                            String description,
                            Boolean isPublic,
                            String likeCount,
                            String commentCount,
                            String charCount,
                            String code) {
        this.privateId    = privateId;
        this.publicId     = publicId;
        this.title        = title;
        this.updated      = updated;
        this.description  = description;
        this.isPublic     = isPublic;
        this.likeCount    = likeCount;
        this.commentCount = commentCount;
        this.charCount    = charCount;
        this.code         = code;
    }

}