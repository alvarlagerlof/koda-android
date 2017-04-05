package com.alvarlagerlof.koda;

import android.util.Log;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

/**
 * Created by alvar on 2016-11-03.
 */

public class RemoteConfigValues {


    private static final String BASE_URL = getString("base_url");
    private static final String API_VERSION = getString("api_version");


    // My projects
    public static final String URL_MY_PROJECTS_DELETE = BASE_URL + getString("url_projects_delete");

    // My projects
    public static final String URL_SYNC = BASE_URL + API_VERSION + getString("url_sync");


    // API
    public static final String URL_API_2D = BASE_URL + API_VERSION + getString("url_api_2d");
    public static final String URL_API_3D = BASE_URL + API_VERSION + getString("url_api_3d");

    // Guides
    public static final String URL_GUIDES = getString("url_guides");


    // Archive
    public static final String URL_ARCHIVE = BASE_URL + API_VERSION + getString("url_archive");


    // Settings
    public static final String URL_SETTINGS_SAVE_NICK = BASE_URL + getString("url_settings_save_nick");
    public static final String PREF_NICK = getString("pref_nick");
    public static final String PREF_EMAIL = getString("pref_email");
    public static final String PREF_NOTIFICATIONS = getString("pref_notifications");
    public static final String PREF_PASSWORD = getString("pref_password");


    // Comments
    public static final String URL_COMMENTS = BASE_URL + API_VERSION + getString("url_comments");
    public static final String URL_COMMENTS_SEND = BASE_URL + API_VERSION + getString("url_url_comments_send");


    // Profile
    public static final String URL_PROFILE = getString("url_profile"); // TODO: NOT DONE YET


    // Other
    public static final String URL_LIKE = BASE_URL + getString("url_like");
    public static final String URL_DISSLIKE = BASE_URL + getString("url_disslike");

    // Login
    public static final String URL_LOGIN = BASE_URL + getString("url_login");
    public static final String URL_LOGIN_FORGOT = BASE_URL + getString("url_login_forgot");
    public static final String URL_LOGIN_NEW = BASE_URL + getString("url_login_new");

    // Login
    public static final String URL_LOGIN_IMAGE = getString("url_login_image");
    public static final String URL_LOGIN_FORGOT_IMAGE = getString("url_login_forgot_image");
    public static final String URL_LOGIN_NEW_IMAGE =  getString("url_login_new_image");





    // Helper
    private static String getString(String name) {

        Log.d("name", name + " " + FirebaseRemoteConfig.getInstance().getString(name));

        return FirebaseRemoteConfig.getInstance().getString(name);
    }


}
