package com.alvarlagerlof.koda.Guides.TextWithTitle;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.alvarlagerlof.koda.R;

/**
 * Created by alvar on 2016-11-18.
 */

public class GuidesTextWithTitleViewHolder extends RecyclerView.ViewHolder{

    public TextView title;
    public TextView text;

    public GuidesTextWithTitleViewHolder(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.title);
        text = (TextView) itemView.findViewById(R.id.text);
    }

}
