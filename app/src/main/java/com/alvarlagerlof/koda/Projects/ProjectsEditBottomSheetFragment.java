package com.alvarlagerlof.koda.Projects;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.alvarlagerlof.koda.Cookies.PersistentCookieStore;
import com.alvarlagerlof.koda.PrefValues;
import com.alvarlagerlof.koda.R;
import com.alvarlagerlof.koda.Utils.AnimationUtils;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.JavaNetCookieJar;

/**
 * Created by alvar on 2016-07-03.
 */
public class ProjectsEditBottomSheetFragment extends BottomSheetDialogFragment {

    String privateID;
    String publicID;

    String title;
    String description;
    Boolean isPublic;

    LinearLayout content;
    LinearLayout processing;
    LinearLayout saved;

    TextInputEditText titleInput;
    TextInputEditText descriptionInput;

    LinearLayout publicLayout;
    AppCompatCheckBox publicCheckbox;

    Button cancel;
    Button save;


    public final void passData(String privateID,
                               String publicID) {
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
        final View contentView = View.inflate(getContext(), R.layout.projects_edit_sheet, null);
        dialog.setContentView(contentView);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }


        content = (LinearLayout) contentView.findViewById(R.id.content);
        processing = (LinearLayout) contentView.findViewById(R.id.processing);
        saved = (LinearLayout) contentView.findViewById(R.id.saved);


        titleInput = (TextInputEditText) contentView.findViewById(R.id.title);
        descriptionInput = (TextInputEditText) contentView.findViewById(R.id.description);

        publicLayout = (LinearLayout) contentView.findViewById(R.id.public_layout);
        publicCheckbox = (AppCompatCheckBox) contentView.findViewById(R.id.public_checkbox);

        cancel = (Button) contentView.findViewById(R.id.cancel);
        save = (Button) contentView.findViewById(R.id.save);


        // Get and set values
        Realm realm = Realm.getDefaultInstance();
        ProjectsRealmObject object = realm.where(ProjectsRealmObject.class)
                .equalTo("privateId", privateID)
                .findFirst();

        realm.beginTransaction();
        titleInput.setText(object.getTitle());
        descriptionInput.setText(object.getDescription());
        publicCheckbox.setChecked( object.getIsPublic());
        realm.commitTransaction();




        // Keyboard
        titleInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(titleInput, InputMethodManager.SHOW_FORCED);
                } else {
                    ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(titleInput.getWindowToken(), 0);
                }
            }
        });

        descriptionInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(descriptionInput, InputMethodManager.SHOW_FORCED);
                } else {
                    ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(descriptionInput.getWindowToken(), 0);
                }
            }
        });


        // Click listners
        publicLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publicCheckbox.toggle();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAsync saveAsync = new saveAsync();
                saveAsync.execute();

            }
        });

    }


    // TODO: IF OPENED AGAIN, NEW DATA NOT THERE

    class saveAsync extends AsyncTask<Void, Integer, String> {

        String titleString;
        String descriptionString;
        Boolean checked;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            titleString = titleInput.getText().toString();
            descriptionString = descriptionInput.getText().toString();
            checked = publicCheckbox.isChecked();

            if (titleString.equals("")) {
                titleString = getString(R.string.unnamed);
            }


            titleInput.setCursorVisible(false);
            titleInput.setFocusable(false);
            titleInput.setFocusableInTouchMode(false);

            descriptionInput.setCursorVisible(false);
            descriptionInput.setFocusable(false);
            descriptionInput.setFocusableInTouchMode(false);

            titleInput.clearFocus();
            descriptionInput.clearFocus();

            //AnimationUtils.fadeOut(content);
            AnimationUtils.fadeIn(processing);

        }


        protected String doInBackground(Void...arg0) {

            CookieHandler cookieHandler = new CookieManager(
                    new PersistentCookieStore(getContext()), CookiePolicy.ACCEPT_ALL);

            OkHttpClient client = new OkHttpClient.Builder()
                    .cookieJar(new JavaNetCookieJar(cookieHandler))
                    .build();

            RequestBody formBody = new FormBody.Builder()
                    .add("title", titleString)
                    .add("description", descriptionString)
                    .add("author", "")
                    .add("publicOrNot", checked ? "CHECKED" : "")
                    .build();


            Request request = new Request.Builder()
                    .url(PrefValues.URL_MY_PROJECTS_EDIT + privateID)
                    .post(formBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                Log.d("response", response.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }


            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            // Save to realm
            Realm realm = Realm.getDefaultInstance();
            ProjectsRealmObject object = realm.where(ProjectsRealmObject.class)
                    .equalTo("privateId", privateID)
                    .findFirst();

            realm.beginTransaction();
            object.setTitle(titleString);
            object.setDescription(descriptionString);
            object.setIsPublic(publicCheckbox.isChecked());
            object.setUpdated(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + "");
            realm.commitTransaction();

            AnimationUtils.fadeIn(saved);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("update", String.valueOf(System.currentTimeMillis()/1000));
                    editor.commit();
                    dismiss();
                }
            }, 1000);


        }
    }




}