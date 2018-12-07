package com.alvarlagerlof.koda.Projects

/**
 * Created by alvar on 20 16-07-02.
 */
data class ProjectsObjectData(val privateID: String,
                              val publicID: String,
                              val title: String,
                              val updated: String,
                              val description: String,
                              val isPublic: Boolean?,
                              val code: String)