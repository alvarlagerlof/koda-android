package com.alvarlagerlof.koda.RequestQueue;

import io.realm.RealmObject;

/**
 * Created by alvar on 2017-04-08.
 */



public class RequestQueueItem extends RealmObject {
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
