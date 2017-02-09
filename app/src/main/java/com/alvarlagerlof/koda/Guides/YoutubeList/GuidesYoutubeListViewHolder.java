package com.alvarlagerlof.koda.Guides.YoutubeList;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alvarlagerlof.koda.R;

/**
 * Created by alvar on 2016-11-18.
 */

public class GuidesYoutubeListViewHolder extends RecyclerView.ViewHolder{

    public RecyclerView recyclerView;

    public GuidesYoutubeListViewHolder(View itemView) {
        super(itemView);
        recyclerView = (RecyclerView) itemView.findViewById(R.id.recycler_view);
    }

}
