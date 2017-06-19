package com.alvarlagerlof.koda

import com.google.firebase.remoteconfig.FirebaseRemoteConfig

/**
 * Created by alvar on 2016-11-03.
 */

object Vars {


    val BASE_URL = getString("base_url")
    val API_VERSION = getString("api_version")


    // My projects
    val URL_PROJECTS_SYNC = BASE_URL + API_VERSION + getString("url_sync")
    val URL_PROJECTS_DELETE = BASE_URL + getString("url_projects_delete")


    // API
    val URL_API_2D = BASE_URL + API_VERSION + getString("url_api_2d")
    val URL_API_3D = BASE_URL + API_VERSION + getString("url_api_3d")

    // Guides
    val URL_GUIDES = getString("url_guides")


    // Archive
    val URL_ARCHIVE = BASE_URL + API_VERSION + getString("url_archive")


    // Settings
    val URL_SETTINGS_SAVE_NICK = BASE_URL + getString("url_settings_save_nick")
    val PREF_NICK = "nick"
    val PREF_EMAIL = "email"
    val PREF_NOTIFICATIONS = "notifications"
    val PREF_PASSWORD = "password"
    val PREF_FONTSIZE = "font_size"
    var PREF_TAB_ID = "tab_id"


    // Comments
    val URL_COMMENTS = BASE_URL + API_VERSION + getString("url_comments")
    val URL_COMMENTS_SEND = BASE_URL + API_VERSION + getString("url_url_comments_send")


    // Profile
    val URL_PROFILE = getString("url_profile") // TODO: NOT DONE YET


    // Other
    val URL_LIKE = BASE_URL + getString("url_like")
    val URL_DISSLIKE = BASE_URL + getString("url_disslike")

    // Login
    val URL_LOGIN = BASE_URL + getString("url_login")
    val URL_LOGIN_FORGOT = BASE_URL + getString("url_login_forgot")
    val URL_LOGIN_NEW = BASE_URL + getString("url_login_new")

    // Login
    val URL_LOGIN_IMAGE = getString("url_login_image")
    val URL_LOGIN_FORGOT_IMAGE = getString("url_login_forgot_image")
    val URL_LOGIN_NEW_IMAGE = getString("url_login_new_image")


    // Killswitch
    val KILLSWITCH_ON = getBoolean("killswitch_on")
    val KILLSWITCH_TITLE = getString("killswitch_title")
    val KILLSWITCH_DESCRIPTION = getString("killswitch_description")
    val KILLSWITCH_ACTION = getString("killswitch_action")


    // Helpers
    private fun getString(name: String): String {
        return FirebaseRemoteConfig.getInstance().getString(name)
    }

    private fun getBoolean(name: String): Boolean {
        return FirebaseRemoteConfig.getInstance().getBoolean(name)
    }


}
