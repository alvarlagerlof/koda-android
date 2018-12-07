package com.alvarlagerlof.koda.RequestQueue

import android.content.Context

import io.realm.Realm

/**
 * Created by alvar on 2017-04-08.
 */

class RequestQueueAdd(context: Context, url: String) {

    init {

        val realm = Realm.getDefaultInstance()

        realm.executeTransactionAsync(Realm.Transaction {
            val item = Realm.getDefaultInstance().createObject(RequestQueueItem::class.java)
            item.url = url
        }, Realm.Transaction.OnSuccess { RequestQueueSync(context) })


    }


}
