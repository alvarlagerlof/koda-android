package com.alvarlagerlof.koda;

/**
 * Created by alvar on 2016-11-03.
 */

public class PrefValues {

    // Main
    private static final String BASE_URL = "http://koda.nu/";
    private static final String API_VERSION = "api_v1/";


    // My projects
    public static final String URL_MY_PROJECTS = BASE_URL + API_VERSION + "labbet";
    public static final String URL_MY_PROJECTS_CREATE_NEW = BASE_URL + API_VERSION + "skapa/";
    public static final String URL_MY_PROJECTS_EDIT = BASE_URL + "labbet/";
    public static final String URL_MY_PROJECTS_DELETE = BASE_URL + "delete/";


    // API
    public static final String URL_API_2D = BASE_URL + API_VERSION + "api_2d";
    public static final String URL_API_3D = BASE_URL + API_VERSION + "api_3d";


    // Archive
    public static final String URL_ARCHIVE = BASE_URL + API_VERSION + "arkivet";


    // Settings
    public static final String URL_SETTINGS_SAVE_NICK = BASE_URL + "labbet";
    public static final String PREF_NICK = "nick";
    public static final String PREF_EMAIL = "email";
    public static final String PREF_NOTIFICATIONS = "notifications";
    public static final String PREF_PASSWORD = "password";


    // Comments
    public static final String URL_COMMENTS = BASE_URL + API_VERSION + "kommentera/";
    public static final String URL_COMMENTS_ADD = BASE_URL + API_VERSION + "kommentera/";


    // Profile
    public static final String URL_PROFILE = "https://ravla.org/profile.txt";  // TODO: NOT DONE YET


    // Other
    public static final String URL_LIKE = BASE_URL + API_VERSION + "plus/";
    public static final String URL_DISSLIKE = BASE_URL + API_VERSION + "minus/";

    // Login
    public static final String URL_LOGIN = BASE_URL + "login";
    public static final String URL_LOGIN_FORGOT = BASE_URL + "login";
    public static final String URL_LOGIN_CREATE = BASE_URL + "login";

    public static final String URL_LOGIN_IMAGE = "https://hd.unsplash.com/photo-1461749280684-dccba630e2f6";
    public static final String URL_LOGIN_FORGOT_IMAGE = "https://hd.unsplash.com/photo-1461632830798-3adb3034e4c8";
    public static final String URL_LOGIN_CREATE_IMAGE = "https://hd.unsplash.com/photo-1429051883746-afd9d56fbdaf";

}
