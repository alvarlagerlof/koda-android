package com.alvarlagerlof.koda.Archive

/**
 * Created author alvar on 2016-07-02.
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
import com.alvarlagerlof.koda.Extensions.timeStampToDate
import com.alvarlagerlof.koda.LikeDissLike
import com.alvarlagerlof.koda.Play.PlayActivity
import com.alvarlagerlof.koda.R
import com.alvarlagerlof.koda.StandardBottomSheetFragment
import com.alvarlagerlof.koda.Vars
import java.util.*

/**
 * Created author alvar on 2015-07-10.
 */


internal class ArchiveAdapter(private val dataset: ArrayList<ArchiveObject>,
                              private val fragmentManager: FragmentManager,
                              private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {



    // Viewholders
    private class ViewHolderLoading internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView)

    private class ViewHolderItem internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var title: TextView
        internal var author: TextView
        internal var description: TextView
        internal var likesNum: TextView
        internal var commentsNum: TextView
        internal var metaText: TextView
        internal var heartImage: ImageView

        internal var likes: LinearLayout
        internal var comment: LinearLayout
        internal var more: LinearLayout

        internal var expandCollapse: LinearLayout
        internal var expandablePanel: LinearLayout
        internal var expandCollapseImage: ImageView

        init {
            title = itemView.findViewById<View>(R.id.title_input) as TextView
            author = itemView.findViewById<View>(R.id.by) as TextView
            description = itemView.findViewById<View>(R.id.description_input) as TextView
            likesNum = itemView.findViewById<View>(R.id.likes_num) as TextView
            commentsNum = itemView.findViewById<View>(R.id.comments_num) as TextView
            metaText = itemView.findViewById<View>(R.id.char_count) as TextView
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

    private class ViewHolderNoResults internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView)


    // View for viewholder
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        val loadingView = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_loading, viewGroup, false)
        val itemView = LayoutInflater.from(viewGroup.context).inflate(R.layout.archive_item, viewGroup, false)
        val offlineView = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_offline, viewGroup, false)
        val noResultsView = LayoutInflater.from(viewGroup.context).inflate(R.layout.archive_item_no_results, viewGroup, false)


        when (i) {
            TYPE_LOADING -> return ViewHolderLoading(loadingView)
            TYPE_ITEM -> return ViewHolderItem(itemView)
            TYPE_OFFLINE -> return ViewHolderOffline(offlineView)
            TYPE_NO_RESULTS -> return ViewHolderNoResults(noResultsView)
        }

        throw RuntimeException("there is no type that matches the type $i + make sure your using types correctly")
    }


    // Bind views
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is ViewHolderItem) {
            holder.title.text = dataset[position].title
            holder.author.text = dataset[position].author
            holder.description.text = dataset[position].description
            holder.likesNum.text = dataset[position].likeCount
            holder.commentsNum.text = dataset[position].commentCount
            holder.metaText.text = dataset[position].charCount + " tecken | Uppdaterad " + dataset[position].date.timeStampToDate()


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
                    val currentLikes = Integer.parseInt(holder.likesNum.text.toString())
                    holder.likesNum.text = (currentLikes - 1).toString()
                    holder.heartImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart_outline))
                    dataset[position].liked = false
                    LikeDissLike(context, Vars.URL_DISSLIKE, dataset[position].publicID).execute()

                } else {
                    val currentLikes = Integer.parseInt(holder.likesNum.text.toString())
                    holder.likesNum.text = (currentLikes + 1).toString()
                    holder.heartImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart))
                    dataset[position].liked = true
                    LikeDissLike(context, Vars.URL_LIKE, dataset[position].publicID).execute()

                }
            }

            holder.comment.setOnClickListener {
                val intent = Intent(holder.title.context, CommentsActivity::class.java)
                intent.putExtra("publicID", dataset[position].publicID)
                intent.putExtra("title", dataset[position].title)
                holder.title.context.startActivity(intent)
            }

            holder.more.setOnClickListener {
                val bottomSheetFragment = StandardBottomSheetFragment(dataset[position].publicID, dataset[position].title, dataset[position].author)

                bottomSheetFragment.show(fragmentManager, bottomSheetFragment.tag)
            }
        }

    }


    override fun getItemViewType(position: Int): Int {
        return dataset[position].type
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    companion object {

        val TYPE_LOADING = 0
        val TYPE_ITEM = 1
        val TYPE_OFFLINE = 2
        val TYPE_NO_RESULTS = 3
    }


}
