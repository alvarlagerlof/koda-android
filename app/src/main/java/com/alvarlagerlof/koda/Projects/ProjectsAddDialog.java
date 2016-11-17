package com.alvarlagerlof.koda.Projects;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.alvarlagerlof.koda.R;

import java.util.ArrayList;

/**
 * Created by alvar on 2016-11-08.
 */

class ProjectsAddDialog {

    ProjectsAddDialog(final Context context, final ArrayList<ProjectsObject> list, final RecyclerView.Adapter adapter) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Vad ska den heta?");


        View view = LayoutInflater.from(context).inflate(R.layout.projects_add, null);
        final EditText input = (EditText) view.findViewById(R.id.input);

        builder.setView(view);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title = String.valueOf(input.getText());

                ProjectsAdd createTask = new ProjectsAdd(context, (title == null || title.equals("")) ? "Namnlös" : title, list, adapter);
                createTask.execute();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

}