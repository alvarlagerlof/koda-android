package com.alvarlagerlof.koda.Projects;

/**
 * Created by alvar on 2016-07-02.
 */
public class ProjectsObject {
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
    int type;


    public ProjectsObject(String privateId,
                          String publicId,
                          String title,
                          String updated,
                          String description,
                          Boolean isPublic,
                          String likeCount,
                          String commentCount,
                          String charCount,
                          String code,
                          int type) {

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
        this.type         = type;

    }

}