package com.alvarlagerlof.koda.Projects

import android.content.Context
import com.alvarlagerlof.koda.Vars
import com.alvarlagerlof.koda.RequestQueue.RequestQueueAdd
import io.realm.Realm

/**
 * Created by alvar on 08/11/16.
 */

class ProjectsDelete internal constructor(context: Context, privateID: String) {

    init {

        Realm.getDefaultInstance().executeTransaction { realm ->
            val item = realm.where(ProjectsRealmObject::class.java).equalTo("privateID", privateID).findFirst()

            RequestQueueAdd(context, Vars.URL_PROJECTS_DELETE + item.privateID)

            item.deleteFromRealm()
        }

    }

}