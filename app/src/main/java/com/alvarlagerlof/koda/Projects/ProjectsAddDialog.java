package com.alvarlagerlof.koda.Projects;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.alvarlagerlof.koda.R;
import com.google.firebase.analytics.FirebaseAnalytics;

import io.realm.Realm;

/**
 * Created by alvar on 2016-11-08.
 */

class ProjectsAddDialog {

    EditText input;

    ProjectsAddDialog(final Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.projects_add, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Vad ska den heta?");

        input = (EditText) view.findViewById(R.id.input);

        builder.setView(view);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String title = String.valueOf(input.getText()) == null || String.valueOf(input.getText()).equals("") ? "Namnlös" :  String.valueOf(input.getText());


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

                Bundle params = new Bundle();
                params.putString("title", title);
                FirebaseAnalytics.getInstance(context).logEvent("new_project", params);


            }
        });
        builder.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

}
