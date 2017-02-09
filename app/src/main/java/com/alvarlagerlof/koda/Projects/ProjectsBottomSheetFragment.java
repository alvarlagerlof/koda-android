package com.alvarlagerlof.koda.Projects;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.LinearLayout;

import com.alvarlagerlof.koda.Comments.CommentsActivity;
import com.alvarlagerlof.koda.FullscreenPlay;
import com.alvarlagerlof.koda.QrCodeShare.QrViewer;
import com.alvarlagerlof.koda.R;

import io.realm.Realm;

/**
 * Created by alvar on 2016-07-03.
 */
public class ProjectsBottomSheetFragment extends BottomSheetDialogFragment {

    FragmentManager fragmentManager;

    String privateID;
    String publicID;

    String title;

    LinearLayout share;
    LinearLayout qr_share;
    LinearLayout comments;
    LinearLayout addToHomescreen;
    LinearLayout edit;
    LinearLayout delete;

    public final void passData(FragmentManager fragmentManager,
                               String privateID,
                               String publicID) {
        this.fragmentManager = fragmentManager;
        this.privateID = privateID;
        this.publicID = publicID;
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }

        }


        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            if (slideOffset < -0.15f) {
                dismiss();
            }
        }
    };


    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        final View contentView = View.inflate(getContext(), R.layout.projects_sheet, null);
        dialog.setContentView(contentView);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }

        // Get values
        Realm realm = Realm.getDefaultInstance();
        ProjectsRealmObject object = realm.where(ProjectsRealmObject.class)
                .equalTo("privateId", privateID)
                .findFirst();

        realm.beginTransaction();
        title = object.getTitle();
        realm.commitTransaction();

        share           = (LinearLayout) contentView.findViewById(R.id.share);
        qr_share        = (LinearLayout) contentView.findViewById(R.id.qr_share);
        comments        = (LinearLayout) contentView.findViewById(R.id.comments);
        addToHomescreen = (LinearLayout) contentView.findViewById(R.id.add_to_homescreen);
        edit            = (LinearLayout) contentView.findViewById(R.id.edit);
        delete          = (LinearLayout) contentView.findViewById(R.id.delete);

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, title + " på Koda.nu: https://koda.nu/arkivet/" + publicID);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });

        qr_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), QrViewer.class);
                intent.putExtra("url", "http://koda.nu/arkivet/" + publicID);
                startActivity(intent);
            }
        });

        comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CommentsActivity.class);
                intent.putExtra("publicID", publicID);
                intent.putExtra("title", title);
                getContext().startActivity(intent);
            }
        });

        addToHomescreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shortcutIntent = new Intent();
                shortcutIntent.setComponent(new ComponentName(getContext(), FullscreenPlay.class));
                shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                shortcutIntent.putExtra("publicID", publicID);
                shortcutIntent.putExtra("title", title);

                Intent addIntent = new Intent();
                addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
                addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
                addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(getContext(), R.drawable.ic_play_black));
                addIntent.putExtra("duplicate", false);
                addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
                getContext().sendBroadcast(addIntent);

                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
            }
        });




        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProjectsEditBottomSheetFragment bottomSheetFragment = new ProjectsEditBottomSheetFragment();
                bottomSheetFragment.passData(privateID, publicID);

                bottomSheetFragment.show(fragmentManager, bottomSheetFragment.getTag());
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
 
                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                alertDialog.setTitle("Är du säker?");
                alertDialog.setMessage("Vill du ta bort " + title + "? Denna operation går inte att ångra");


                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "TA BORT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ProjectsDelete deleteTask = new ProjectsDelete(getContext(), privateID);
                        deleteTask.execute();
                        dismiss();
                    }
                });

                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "AVBRYT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });

                // Showing Alert Message
                alertDialog.show();
            }
        });


    }


}