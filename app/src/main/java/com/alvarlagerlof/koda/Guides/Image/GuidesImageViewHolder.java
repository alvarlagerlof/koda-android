package com.alvarlagerlof.koda.Guides.Image;

import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alvarlagerlof.koda.R;

/**
 * Created by alvar on 2016-11-18.
 */

public class GuidesImageViewHolder extends RecyclerView.ViewHolder{

    public ImageView image;

    public GuidesImageViewHolder(View itemView) {
        super(itemView);
        image = (ImageView) itemView.findViewById(R.id.image);
    }

}
