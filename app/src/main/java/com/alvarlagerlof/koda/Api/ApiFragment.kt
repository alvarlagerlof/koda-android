package com.alvarlagerlof.koda.Api

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alvarlagerlof.koda.Extensions.inflate
import com.alvarlagerlof.koda.Main.MainElevationEvent
import com.alvarlagerlof.koda.R
import com.alvarlagerlof.koda.Vars
import com.alvarlagerlof.koda.ViewPagerAdapter
import kotlinx.android.synthetic.main.api_fragment.*
import org.greenrobot.eventbus.EventBus

/**
 * Created by alvar on 2016-07-02.
 */

class ApiFragment : Fragment() {

    lateinit var adapter: ViewPagerAdapter

    lateinit var fragment2D: ApiTabFragment
    lateinit var fragment3D: ApiTabFragment


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.api_fragment)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        EventBus.getDefault().post(MainElevationEvent(elevation = 0f))


        initViewPager()

    }


    fun initViewPager() {

        adapter = ViewPagerAdapter(childFragmentManager)

        fragment2D = ApiTabFragment()
        fragment3D = ApiTabFragment()

        val bundle2D = Bundle()
        bundle2D.putString("url", Vars.URL_API_2D)

        val bundle3D = Bundle()
        bundle3D.putString("url", Vars.URL_API_3D)

        fragment2D.arguments = bundle2D
        fragment3D.arguments = bundle3D


        fragment2D.retainInstance = true
        fragment2D.retainInstance = true

        adapter.clear()

        adapter.addFragment(fragment2D, "2D")
        adapter.addFragment(fragment3D, "3D")

        view_pager.adapter = adapter
        tab_layout.setupWithViewPager(view_pager)
    }


    override fun onResume() {
        super.onResume()
        initViewPager()
    }


}

