package com.alvarlagerlof.koda.Projects

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alvarlagerlof.koda.DividerItemDecoration
import com.alvarlagerlof.koda.Extensions.animateFadeIn
import com.alvarlagerlof.koda.Extensions.animateFadeOut
import com.alvarlagerlof.koda.Extensions.inflate
import com.alvarlagerlof.koda.Extensions.timeStampToDate
import com.alvarlagerlof.koda.Main.MainElevationEvent
import com.alvarlagerlof.koda.R
import com.arasthel.asyncjob.AsyncJob
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.Sort
import kotlinx.android.synthetic.main.projects_fragment.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*


/**
 * Created by alvar on 2016-07-02.
 */

class ProjectsFragment : Fragment() {

    private var syncVisible = false
    private var syncCounter = 1000000000


    internal lateinit var adapter: ProjectsAdapter
    internal var projectsList: ArrayList<Any> = ArrayList()






    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.projects_fragment)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        EventBus.getDefault().post(MainElevationEvent(elevation = 16f))


        showSync()

        adapter = ProjectsAdapter(projectsList, activity.supportFragmentManager)
        recycler_view.layoutManager = LinearLayoutManager(context)
        recycler_view.addItemDecoration(DividerItemDecoration(context))
        recycler_view.adapter = adapter



        // Realm on change
        val realm = Realm.getDefaultInstance()
        val realmListener = RealmChangeListener<Realm> {
            showSync()
            syncCounter = 1000
        }
        realm.addChangeListener(realmListener)


        // Sync counter
        AsyncJob.doInBackground {
            while (true) {
                try { Thread.sleep(100) } catch (ie: InterruptedException) {}
                if (syncCounter >= 0) {
                    syncCounter -= 100
                    if (syncCounter <= 0) {
                        AsyncJob.doOnMainThread { loadList() }
                    }
                }
            }
        }


        fab?.setOnClickListener {
            ProjectsAddDialog(context)
        }
    }





    fun loadList() {

        showSync()

        // Clear adapter list
        projectsList.clear()


        // Get all objects and add to list
        val realmObjects = Realm.getDefaultInstance()
                .where(ProjectsRealmObject::class.java)
                .findAll()
                .sort("updatedRealm", Sort.DESCENDING)

        for (realmObject in realmObjects) {
            projectsList.add(ProjectsObjectData(
                    realmObject.privateID,
                    realmObject.publicID,
                    realmObject.title,
                    realmObject.updatedRealm.timeStampToDate(),
                    realmObject.description,
                    realmObject.isPublic,
                    realmObject.code))
        }


        // If no objects
        if (realmObjects.size == 0) {
            projectsList.add(ProjectsObjectNoItems())
        }


        // Notify adapter
        adapter.notifyDataSetChanged()

        if (recycler_view != null) {
            recycler_view.smoothScrollToPosition(0)
        }

        hideSync()
    }



    fun showSync() {
        if (!syncVisible) {
            if (sync_view != null) {
                sync_view.visibility = View.VISIBLE
                sync_view.animateFadeIn()
                syncVisible = true
            }
        }
    }

    fun hideSync() {
        if (syncVisible) {
            if (sync_view != null) {
                sync_view.visibility = View.GONE
                sync_view.animateFadeOut()
                syncVisible = false
            }
        }

    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(@Suppress("UNUSED_PARAMETER") event: ProjectsSyncEvent) {
        when (event.message) {
            "show" -> showSync()
            "hide" -> hideSync()
            "offline" -> loadList()
        }
        loadList()
    }




    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }


}
