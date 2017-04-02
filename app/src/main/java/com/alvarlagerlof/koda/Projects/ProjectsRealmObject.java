package com.alvarlagerlof.koda.Projects;

import io.realm.RealmObject;

/**
 * Created by alvar on 2016-07-02.
 */
public class ProjectsRealmObject extends RealmObject {
    private String privateID;
    private String publicID;
    private String title;
    private String updatedRealm;
    private String updatedServer;
    private String description;
    private Boolean isPublic;
    private String code;
    private Boolean synced;



    // Private id
    public String getprivateID() {
        return privateID;
    }

    public void setprivateID(String privateID) {
        this.privateID = privateID;
    }


    // Public id
    public String getpublicID() {
        return publicID;
    }

    public void setpublicID(String publicID) {
        this.publicID = publicID;
    }


    // Title
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    // Date
    public String getUpdatedServer() {
        return updatedServer;
    }

    public void setUpdatedServer(String updatedServer) {
        this.updatedServer = updatedServer;
    }


    // Date
    public String getUpdatedRealm() {
        return updatedRealm;
    }

    public void setUpdatedRealm(String updatedRealm) {
        this.updatedRealm = updatedRealm;
    }


    // Description
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    // Is Public
    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }


    // Code
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    // Synced
    public Boolean getSynced() {
        return synced;
    }

    public void setSynced(Boolean synced) {
        this.synced = synced;
    }
}