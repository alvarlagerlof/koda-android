package com.alvarlagerlof.koda.Api;

/**
 * Created by alvar on 2016-07-02.
 */
class ApiObject {
    String command;
    String description;
    int type;

    ApiObject(String command, String description, int type) {
        this.command = command;
        this.description = description;
        this.type = type;
    }

}