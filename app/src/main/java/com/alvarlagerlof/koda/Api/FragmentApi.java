package com.alvarlagerlof.koda.Api;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alvarlagerlof.koda.MainAcitivty;
import com.alvarlagerlof.koda.PrefValues;
import com.alvarlagerlof.koda.R;
import com.alvarlagerlof.koda.ViewPagerAdapter;

/**
 * Created by alvar on 2016-07-02.
 */

public class FragmentApi extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.api_fragment, container, false);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);

        // Set up the adapter
        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());

        // 2D
        ApiTabFragment apiTabFragment2D = new ApiTabFragment();
        apiTabFragment2D.url = PrefValues.URL_API_2D;
        adapter.addFragment(apiTabFragment2D, "2D");

        // 3D
        ApiTabFragment apiTabFragment3D = new ApiTabFragment();
        apiTabFragment3D.url = PrefValues.URL_API_3D;
        adapter.addFragment(apiTabFragment3D, "3D");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }


    // The adapter


}

