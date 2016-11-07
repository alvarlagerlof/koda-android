package com.alvarlagerlof.koda.MyProjects;

/**
 * Created by alvar on 2016-07-02.
 */
public class MyProjectsObject {
    private String privateId;
    private String publicId;
    private String title;
    private String updated;
    private String description;
    private Boolean isPublic;
    private String likeCount;
    private String commentCount;
    private String charCount;
    private String code;


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


    // Private id
    public String getPrivateId() {
        return privateId;
    }

    public void setPrivateId(String privateId) {
        this.privateId = privateId;
    }


    // Public id
    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }


    // Title
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    // Date
    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
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


    // Like count
    public String getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }


    // Comment cound
    public String getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }


    // Char count
    public String getCharCount() {
        return charCount;
    }

    public void setCharCount(String charCount) {
        this.charCount = charCount;
    }


    // Code
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}