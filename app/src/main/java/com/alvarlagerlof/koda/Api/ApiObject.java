package com.alvarlagerlof.koda.Api;

/**
 * Created by alvar on 2016-07-02.
 */
public class ApiObject {
    private String mCommand;
    private String mDescription;

    public ApiObject(String command, String description) {
        mCommand = command;
        mDescription = description;
    }


    // Command
    public String getmCommand() {
        return mCommand;
    }

    public void setmCommand(String mCommand) {
        this.mCommand = mCommand;
    }


    // Description
    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }
}