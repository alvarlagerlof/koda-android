package com.alvarlagerlof.koda.Api

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alvarlagerlof.koda.Extensions.inflate
import com.alvarlagerlof.koda.R
import kotlinx.android.synthetic.main.api_tab.*
import java.util.*

/**
 * Created by alvar on 2016-07-02.
 */
class ApiTabFragment() : Fragment() {

    lateinit var url: String

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        url = arguments.getString("url")
        return container?.inflate(R.layout.api_tab)
    }


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val list = ArrayList<Any>()
        val adapter = ApiAdapter(list)

        recycler_view.layoutManager = LinearLayoutManager(context)
        recycler_view.adapter = adapter


        // Get data
        ApiGetData(context, url, list, adapter)
    }


}
