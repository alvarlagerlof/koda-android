package com.alvarlagerlof.koda.Api

/**
 * Created by alvar on 2016-07-02.
 */

import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.alvarlagerlof.koda.R
import com.alvarlagerlof.koda.Universal.UniversalLoadingObject
import com.alvarlagerlof.koda.Universal.UniversalOfflineObject


/**
 * Created by alvar on 2015-07-10.
 */


class ApiAdapter(val data: ArrayList<Any>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private class ViewHolderItem internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var command: TextView = itemView.findViewById(R.id.command)
        internal var description: TextView = itemView.findViewById(R.id.description)
    }

    private class ViewHolderLoading internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView)

    private class ViewHolderOffline internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView)


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        val loadingView = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_loading, viewGroup, false)
        val itemView = LayoutInflater.from(viewGroup.context).inflate(R.layout.api_item, viewGroup, false)
        val offlineView = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_offline, viewGroup, false)

        when (i) {
            TYPE_LOADING -> return ViewHolderLoading(loadingView)
            TYPE_ITEM -> return ViewHolderItem(itemView)
            TYPE_OFFLINE -> return ViewHolderOffline(offlineView)
        }

        throw RuntimeException("there is no type that matches the type $i + make sure your using types correctly")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolderItem) {
            val context = holder.command.context

            val (command, description) = data[position] as ApiObject

            holder.command

            holder.command.text = command
            holder.command.typeface = Typeface.createFromAsset(context.assets, "SourceCodePro-Regular.ttf")

            holder.description.text = description
        }
    }


    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is UniversalLoadingObject -> TYPE_LOADING
            is UniversalOfflineObject -> TYPE_OFFLINE
            is ApiObject -> TYPE_ITEM
            else -> TYPE_LOADING
        }
    }



    companion object {
        val TYPE_LOADING = 0
        val TYPE_ITEM = 1
        val TYPE_OFFLINE = 2
    }

}
