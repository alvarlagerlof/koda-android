package com.alvarlagerlof.koda.Guides;

/**
 * Created by alvar on 2016-07-02.
 */
public class YouTubeObject {
    private String imageUrl;
    private String title;
    private String link;


    public YouTubeObject(String imageUrl,
                         String title,
                         String link) {

        this.imageUrl = imageUrl;
        this.title = title;
        this.link = link;
    }


    // Image Url
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }



    // Title
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }



    // Link
    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}