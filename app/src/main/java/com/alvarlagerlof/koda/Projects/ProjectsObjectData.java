package com.alvarlagerlof.koda.Projects;

/**
 * Created by alvar on 2016-07-02.
 */
class ProjectsObjectData {
    String privateID;
    String publicID;
    String title;
    String updated;
    String description;
    Boolean isPublic;
    String code;


    ProjectsObjectData(String privateID,
                       String publicID,
                       String title,
                       String updated,
                       String description,
                       Boolean isPublic,
                       String code) {

        this.privateID   = privateID;
        this.publicID    = publicID;
        this.title       = title;
        this.updated     = updated;
        this.description = description;
        this.isPublic    = isPublic;
        this.code        = code;

    }

}