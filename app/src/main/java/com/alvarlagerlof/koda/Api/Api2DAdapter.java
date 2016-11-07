package com.alvarlagerlof.koda.Api;

/**
 * Created by alvar on 2016-07-02.
 */

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alvarlagerlof.koda.R;

import java.util.ArrayList;

/**
 * Created by alvar on 2015-07-10.
 */


public class Api2DAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ApiObject> mDataset;
    public Api2DAdapter(ArrayList<ApiObject> myDataset) {
        mDataset = myDataset;
    }

    public final int TYPE_LOADING = 0;
    public final int TYPE_ITEM    = 1;



    public static class ViewHolderItem extends RecyclerView.ViewHolder {
        public final TextView command;
        public final TextView description;


        public ViewHolderItem(View itemView) {
            super(itemView);
            command = (TextView) itemView.findViewById(R.id.command);
            description = (TextView) itemView.findViewById(R.id.description);
        }

    }

    public static class ViewHolderLoading extends RecyclerView.ViewHolder {

        public ViewHolderLoading(View itemView) {
            super(itemView);

        }

    }




    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View loadingView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_loading, viewGroup, false);
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.api_item, viewGroup, false);

        switch (i) {
            case TYPE_LOADING:
                return new ViewHolderLoading(loadingView);
            case TYPE_ITEM:
                return new ViewHolderItem(itemView);
        }

        throw new RuntimeException("there is no type that matches the type " + i + " + make sure your using types correctly");
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolderItem) {
            Context context = ((ViewHolderItem) holder).command.getContext();

            ((ViewHolderItem) holder).command.setText(mDataset.get(position).getmCommand());
            Typeface type = Typeface.createFromAsset(context.getAssets(), "SourceCodePro-Regular.ttf");
            ((ViewHolderItem) holder).command.setTypeface(type);

            ((ViewHolderItem) holder).description.setText(mDataset.get(position).getmDescription());
        }
    }


    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mDataset.get(position).getmCommand().equals("Loading")) {
            return TYPE_LOADING;
        }
        return TYPE_ITEM;
    }

}
