package com.alvarlagerlof.koda.Projects

/**
 * Created by alvar on 2016-07-02.
 */

import android.content.Intent
import android.preference.PreferenceManager
import android.support.v4.app.FragmentManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.alvarlagerlof.koda.Editor.EditorActivity
import com.alvarlagerlof.koda.R
import com.alvarlagerlof.koda.Vars
import com.alvarlagerlof.koda.Universal.UniversalLoadingObject
import com.alvarlagerlof.koda.Universal.UniversalLodingViewHolder
import java.util.*

/**
 * Created by alvar on 2015-07-10.
 */


internal class ProjectsAdapter(private val data: ArrayList<Any>, private val fragmentManager: FragmentManager) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private class ViewHolderItem internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var title: TextView = itemView.findViewById(R.id.title_input)
        internal var meta: TextView = itemView.findViewById(R.id.meta)
        internal var more: LinearLayout = itemView.findViewById(R.id.more)
    }

    private class ViewHolderNoItems internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView)



    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        val loadingView = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_loading, viewGroup, false)
        val itemView = LayoutInflater.from(viewGroup.context).inflate(R.layout.projects_item, viewGroup, false)
        val noItemsView = LayoutInflater.from(viewGroup.context).inflate(R.layout.projects_item_no_items, viewGroup, false)

        when (i) {
            TYPE_LOADING -> return UniversalLodingViewHolder(loadingView)
            TYPE_ITEM -> return ViewHolderItem(itemView)
            TYPE_NO_ITEMS -> return ViewHolderNoItems(noItemsView)
        }

        throw RuntimeException("there is no type that matches the type $i + make sure your using types correctly")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is ViewHolderItem) {
            val context = holder.title.context

            val (privateID, publicID, title, updated) = data[position] as ProjectsObjectData


            holder.title.text = title
            holder.meta.text = updated


            holder.itemView.setOnClickListener {
                val intent = Intent(context, EditorActivity::class.java)
                intent.putExtra("privateID", privateID)
                context.startActivity(intent)
            }


            holder.more.setOnClickListener {
                val bottomSheetFragment = ProjectsBottomSheetFragment(
                        privateID,
                        publicID,
                        title,
                        PreferenceManager.getDefaultSharedPreferences(context).getString(Vars.PREF_NICK, ""))

                bottomSheetFragment.show(fragmentManager, bottomSheetFragment.tag)
            }


        }
    }


    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is UniversalLoadingObject -> TYPE_LOADING
            is ProjectsObjectNoItems -> TYPE_NO_ITEMS
            is ProjectsObjectData -> TYPE_ITEM
            else -> TYPE_LOADING
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    companion object {
        val TYPE_LOADING = 0
        val TYPE_ITEM = 1
        val TYPE_NO_ITEMS = 2
    }

}
