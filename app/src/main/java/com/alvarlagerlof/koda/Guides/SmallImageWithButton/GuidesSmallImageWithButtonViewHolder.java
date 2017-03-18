package com.alvarlagerlof.koda.Guides.SmallImageWithButton;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.alvarlagerlof.koda.R;

/**
 * Created by alvar on 2016-11-18.
 */

public class GuidesSmallImageWithButtonViewHolder extends RecyclerView.ViewHolder{

    public ImageView image;
    public Button button;

    public GuidesSmallImageWithButtonViewHolder(View itemView) {
        super(itemView);
        image = (ImageView) itemView.findViewById(R.id.image);
        button = (Button) itemView.findViewById(R.id.button);
    }

}
