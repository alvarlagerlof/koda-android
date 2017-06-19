package com.alvarlagerlof.koda.Profile

/**
 * Created by alvar on 2016-07-02.
 */

import android.content.Context
import android.content.Intent
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.alvarlagerlof.koda.Comments.CommentsActivity
import com.alvarlagerlof.koda.Extensions.animateCollapse
import com.alvarlagerlof.koda.Extensions.animateExpand
import com.alvarlagerlof.koda.LikeDissLike
import com.alvarlagerlof.koda.Play.PlayActivity
import com.alvarlagerlof.koda.R
import com.alvarlagerlof.koda.Vars
import java.util.*

/**
 * Created by alvar on 2015-07-10.
 */


internal class ProfileAdapter(private val dataset: ArrayList<ProfileObject>, private val fragmentManager: FragmentManager, private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private class ViewHolderHeader internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var author: TextView
        internal var numberOfProjects: TextView

        init {
            author = itemView.findViewById<View>(R.id.author) as TextView
            numberOfProjects = itemView.findViewById<View>(R.id.numberOfProjects) as TextView

        }
    }

    private class ViewHolderLoading internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView)

    private class ViewHolderItem internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var title: TextView
        internal var by: TextView
        internal var description: TextView
        internal var likesNum: TextView
        internal var commentsNum: TextView
        internal var charCount: TextView
        internal var heartImage: ImageView

        internal var likes: LinearLayout
        internal var comment: LinearLayout
        internal var more: LinearLayout

        internal var expandCollapse: LinearLayout
        internal var expandablePanel: LinearLayout
        internal var expandCollapseImage: ImageView


        init {
            title = itemView.findViewById<View>(R.id.title_input) as TextView
            by = itemView.findViewById<View>(R.id.by) as TextView
            description = itemView.findViewById<View>(R.id.description_input) as TextView
            likesNum = itemView.findViewById<View>(R.id.likes_num) as TextView
            commentsNum = itemView.findViewById<View>(R.id.comments_num) as TextView
            charCount = itemView.findViewById<View>(R.id.char_count) as TextView
            heartImage = itemView.findViewById<View>(R.id.heart_image) as ImageView

            likes = itemView.findViewById<View>(R.id.likes) as LinearLayout
            comment = itemView.findViewById<View>(R.id.comments) as LinearLayout
            more = itemView.findViewById<View>(R.id.more) as LinearLayout

            expandCollapse = itemView.findViewById<View>(R.id.expand_collapse) as LinearLayout
            expandablePanel = itemView.findViewById<View>(R.id.expandable_panel) as LinearLayout
            expandCollapseImage = itemView.findViewById<View>(R.id.expand_collapse_image) as ImageView

        }

    }

    private class ViewHolderOffline internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView)


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        val headerView = LayoutInflater.from(viewGroup.context).inflate(R.layout.profile_header, viewGroup, false)
        val loadingView = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_loading, viewGroup, false)
        val itemView = LayoutInflater.from(viewGroup.context).inflate(R.layout.archive_item, viewGroup, false)
        val offlineView = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_offline, viewGroup, false)

        when (i) {
            TYPE_HEADER -> return ViewHolderHeader(headerView)
            TYPE_LOADING -> return ViewHolderLoading(loadingView)
            TYPE_ITEM -> return ViewHolderItem(itemView)
            TYPE_OFFLINE -> return ViewHolderOffline(offlineView)
        }
        throw RuntimeException("there is no type that matches the type $i + make sure your using types correctly")
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolderHeader) {

            holder.author.text = dataset[position].author
            holder.numberOfProjects.text = dataset[position].numberOfProjects + " projekt"

        } else if (holder is ViewHolderItem) {

            holder.title.text = dataset[position].title
            holder.by.text = dataset[position].author
            holder.description.text = dataset[position].description
            holder.likesNum.text = dataset[position].likeCount
            holder.commentsNum.text = dataset[position].commentCount
            holder.charCount.text = dataset[position].charCount + " " + context.getString(R.string.characters)

            if (dataset[position].liked) {
                holder.heartImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart))
            } else {
                holder.heartImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart_outline))
            }

            holder.itemView.setOnClickListener {
                val intent = Intent(holder.title.context, PlayActivity::class.java)
                intent.putExtra("public_id", dataset[position].publicID)
                intent.putExtra("title", dataset[position].title)
                holder.title.context.startActivity(intent)
            }


            holder.expandCollapse.setOnClickListener {
                if (holder.expandablePanel.visibility == View.GONE) {
                    holder.expandCollapseImage.setImageDrawable(ContextCompat.getDrawable(holder.expandablePanel.context, R.drawable.ic_collapse))
                    holder.expandablePanel.animateExpand()
                } else {
                    holder.expandCollapseImage.setImageDrawable(ContextCompat.getDrawable(holder.expandablePanel.context, R.drawable.ic_expand))
                    holder.expandablePanel.animateCollapse()
                }
            }



            holder.likes.setOnClickListener {
                if (dataset[position].liked) {

                    holder.likesNum.text = Integer.parseInt((Integer.parseInt(holder.likesNum.text.toString()) - 1).toString()).toString()
                    holder.heartImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart_outline))
                    dataset[position].liked = false
                    LikeDissLike(context, Vars.URL_DISSLIKE, dataset[position].publicID)

                } else {

                    holder.likesNum.text = Integer.parseInt(holder.likesNum.text.toString() + 1).toString()
                    holder.heartImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart))
                    dataset[position].liked = true
                    LikeDissLike(context, Vars.URL_LIKE, dataset[position].publicID)
                }
            }

            holder.comment.setOnClickListener {
                val intent = Intent(holder.title.context, CommentsActivity::class.java)
                intent.putExtra("public_id", dataset[position].publicID)
                intent.putExtra("title", dataset[position].title)
                holder.title.context.startActivity(intent)
            }

            holder.more.setOnClickListener {
                val bottomSheetFragment = ProfileBottomSheetFragment()
                bottomSheetFragment.passData(dataset[position].publicID, dataset[position].title, dataset[position].author)

                bottomSheetFragment.show(fragmentManager, bottomSheetFragment.tag)
            }

        }
    }


    override fun getItemViewType(position: Int): Int {

        when (dataset[position].type) {
            TYPE_HEADER -> return TYPE_HEADER
            TYPE_LOADING -> return TYPE_LOADING
            TYPE_ITEM -> return TYPE_ITEM
            TYPE_OFFLINE -> return TYPE_OFFLINE
            else -> return TYPE_ITEM
        }
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    companion object {

        val TYPE_HEADER = 0
        val TYPE_LOADING = 1
        val TYPE_ITEM = 2
        val TYPE_OFFLINE = 3
    }


}
