package com.alvarlagerlof.koda

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter



/**
 * Created by alvar on 2016-11-08.
 */

class ViewPagerAdapter(manager: FragmentManager) : FragmentStatePagerAdapter(manager) {
    private val fragmentList = ArrayList<Fragment>()
    private val fragmentTitleList = ArrayList<String>()

    fun addFragment(fragment: Fragment, title: String) {
        fragmentList.add(fragment)
        fragmentTitleList.add(title)
    }

    fun clear() {
        fragmentList.clear()
        fragmentTitleList.clear()
    }


    override fun getPageTitle(position: Int): CharSequence = fragmentTitleList[position]

    override fun getItem(position: Int): Fragment = fragmentList[position]

    override fun getCount(): Int = fragmentList.size



}