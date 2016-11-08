package com.alvarlagerlof.koda.Archive;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.widget.LinearLayout;

import com.alvarlagerlof.koda.FullscreenPlay;
import com.alvarlagerlof.koda.Profile.ProfileActivity;
import com.alvarlagerlof.koda.QrCodeShare.QrViewer;
import com.alvarlagerlof.koda.R;

/**
 * Created author alvar on 2016-07-03.
 */
public class ArchiveBottomSheetFragment extends BottomSheetDialogFragment {

    String public_id;
    String title;
    String author;

    LinearLayout profile;
    LinearLayout share;
    LinearLayout addToHomescreen;
    LinearLayout qr_share;


    public final void passData(String public_id, String title, String author) {
        this.public_id = public_id;
        this.title     = title;
        this.author    = author;
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
        final View contentView = View.inflate(getContext(), R.layout.sheet_archive, null);
        dialog.setContentView(contentView);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }


        profile         = (LinearLayout) contentView.findViewById(R.id.profile);
        share           = (LinearLayout) contentView.findViewById(R.id.share);
        qr_share        = (LinearLayout) contentView.findViewById(R.id.qr_share);
        addToHomescreen = (LinearLayout) contentView.findViewById(R.id.add_to_homescreen);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent(getContext(), ProfileActivity.class);
                sendIntent.putExtra("author", author);
                startActivity(sendIntent);
            }
        });

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





    }

}