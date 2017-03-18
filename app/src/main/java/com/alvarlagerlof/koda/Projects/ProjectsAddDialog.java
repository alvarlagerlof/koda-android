package com.alvarlagerlof.koda.Projects;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.alvarlagerlof.koda.R;

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
                String title = String.valueOf(input.getText());

                new ProjectsAdd(context, (title == null || title.equals("")) ? "Namnlös" : title);

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
