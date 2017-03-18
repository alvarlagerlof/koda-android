package com.alvarlagerlof.koda.Projects;

import android.content.Context;

import io.realm.Realm;

/**
 * Created by alvar on 08/11/16.
 */

class ProjectsAdd {

    ProjectsAdd(Context context, final String title) {

        // Temporary privateID
        final String tmpPrivateID = "ny_" + String.valueOf(System.currentTimeMillis());


        // Add to realm
        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                ProjectsRealmObject object = realm.createObject(ProjectsRealmObject.class);
                object.setprivateID(tmpPrivateID);
                object.setpublicID(tmpPrivateID);
                object.setTitle(title);
                object.setUpdatedRealm(String.valueOf(System.currentTimeMillis() / 1000));
                object.setDescription("");
                object.setIsPublic(false);
                object.setSynced(false);
                object.setCode("<script src=\"http://koda.nu/simple.js\">\n" +
                        "\n" +
                        "  circle(100, 100, 20, \"red\");\n" +
                        "\n" +
                        "</script>");
            }
        });


        // Sync
        new ProjectsSync(context, tmpPrivateID);




    }









}