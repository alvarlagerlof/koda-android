package com.alvarlagerlof.koda.Guides.Text;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.alvarlagerlof.koda.R;

/**
 * Created by alvar on 2016-11-18.
 */

public class GuidesTextViewHolder extends RecyclerView.ViewHolder{

    public TextView text;

    public GuidesTextViewHolder(View itemView) {
        super(itemView);
        text = (TextView) itemView.findViewById(R.id.text);
    }

}
