package com.alvarlagerlof.koda.Comments

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.alvarlagerlof.koda.DividerItemDecoration
import com.alvarlagerlof.koda.Extensions.isConnected
import com.alvarlagerlof.koda.R
import kotlinx.android.synthetic.main.comments_activty.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by alvar on 2016-08-15.
 */

class CommentsActivity : AppCompatActivity() {

    internal var list = ArrayList<CommentsObject>()
    internal var adapter = CommentsAdapter(list)



    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.comments_activty)


        // Init toolbar
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_close_white)
        supportActionBar!!.title = "Kommentarer på " + intent.getStringExtra("title")


        // Init RecylerView
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true

        recycler_view.layoutManager = layoutManager
        recycler_view.addItemDecoration(DividerItemDecoration(this))
        recycler_view.adapter = adapter


        // Get data
        val dataTask = CommentsGetData(this, intent.getStringExtra("publicID"), list, adapter)
        dataTask.execute()

    }


    fun addComment(view: View) {

        if (applicationContext.isConnected()) {

            // Send to server
            CommentsSend(this,
                    intent.getStringExtra("publicID"),
                    comment_edittext.text.toString())


            // Add
            list.add(CommentsObject(PreferenceManager.getDefaultSharedPreferences(this).getString("nick", "Anonym"),
                    SimpleDateFormat("d MMMM, yyyy").format(Date()),
                    comment_edittext.text.toString(),
                    CommentsAdapter.TYPE_ITEM))


            // Notify
            adapter.notifyDataSetChanged()


            // Hide keyboard and clear text field
            comment_edittext.setText("")
            comment_edittext.clearFocus()
            val viewtest = this.currentFocus
            if (viewtest != null) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(viewtest.windowToken, 0)
            }


            // Scroll to bottom
            recycler_view.smoothScrollToPosition(list.size - 1)


        } else {

            // Offline alert

        }


    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


}
