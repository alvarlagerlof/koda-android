package com.alvarlagerlof.koda.MyProjects;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.LinearLayout;

import com.alvarlagerlof.koda.Cookies.PersistentCookieStore;
import com.alvarlagerlof.koda.FullscreenPlay;
import com.alvarlagerlof.koda.MainAcitivty;
import com.alvarlagerlof.koda.PrefValues;
import com.alvarlagerlof.koda.QrCodeShare.QrViewer;
import com.alvarlagerlof.koda.R;
import com.google.firebase.crash.FirebaseCrash;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.internal.JavaNetCookieJar;

/**
 * Created by alvar on 2016-07-03.
 */
public class MyProjectsBottomSheetFragment extends BottomSheetDialogFragment {

    FragmentManager fragmentManager;

    String private_id;
    String public_id;

    String title;
    String description;

    Boolean isPublic;
    int position;

    LinearLayout share;
    LinearLayout qr_share;
    LinearLayout addToHomescreen;
    LinearLayout edit;
    LinearLayout delete;

    public final void passData(FragmentManager fragmentManager,
                               String private_id,
                               String public_id,
                               String title,
                               String description,
                               Boolean isPublic,
                               int position) {
        this.fragmentManager = fragmentManager;
        this.private_id = private_id;
        this.public_id = public_id;
        this.title = title;
        this.description = description;
        this.isPublic = isPublic;
        this.position = position;
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
        final View contentView = View.inflate(getContext(), R.layout.sheet_my_projects, null);
        dialog.setContentView(contentView);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }

        share           = (LinearLayout) contentView.findViewById(R.id.share);
        qr_share        = (LinearLayout) contentView.findViewById(R.id.qr_share);
        addToHomescreen = (LinearLayout) contentView.findViewById(R.id.add_to_homescreen);
        edit            = (LinearLayout) contentView.findViewById(R.id.edit);
        delete          = (LinearLayout) contentView.findViewById(R.id.delete);

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, title + " på Koda.nu: https://koda.nu/arkivet/" + public_id);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });

        qr_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), QrViewer.class);
                intent.putExtra("url", "http://koda.nu/arkivet/" + public_id);
                startActivity(intent);
            }
        });

        addToHomescreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shortcutIntent = new Intent();
                shortcutIntent.setComponent(new ComponentName(getContext(), FullscreenPlay.class));
                shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                shortcutIntent.putExtra("public_id", public_id);
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
                MyProjectsEditBottomSheetFragment bottomSheetFragment = new MyProjectsEditBottomSheetFragment();
                bottomSheetFragment.passData(private_id, public_id, title, description, isPublic);

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
                        new delete().execute(private_id);
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


    class delete extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MainAcitivty.fragmentMyProjects.removeItemAt(position);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                CookieHandler cookieHandler = new CookieManager(
                        new PersistentCookieStore(getContext()), CookiePolicy.ACCEPT_ALL);

                OkHttpClient client = new OkHttpClient.Builder()
                        .cookieJar(new JavaNetCookieJar(cookieHandler))
                        .build();


                Request request = new Request.Builder()
                        .url(PrefValues.URL_MY_PROJECTS_DELETE + strings[0])
                        .build();
                client.newCall(request).execute();

            } catch (Exception e) {
                e.printStackTrace();
                FirebaseCrash.report(e);
            }

            return "";
        }


    }

}