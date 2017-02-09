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


class ApiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Init objects
    private ArrayList<ApiObject> dataset;
    static final int TYPE_LOADING = 0;
    static final int TYPE_ITEM    = 1;
    static final int TYPE_OFFLINE = 2;


    // Get the data
    ApiAdapter(ArrayList<ApiObject> dataset) {
        this.dataset = dataset;
    }


    // View holders
    private static class ViewHolderItem extends RecyclerView.ViewHolder {
        TextView command;
        TextView description;

        ViewHolderItem(View itemView) {
            super(itemView);
            command = (TextView) itemView.findViewById(R.id.command);
            description = (TextView) itemView.findViewById(R.id.description);
        }

    }

    private static class ViewHolderLoading extends RecyclerView.ViewHolder {
        ViewHolderLoading(View itemView) {
            super(itemView);
        }
    }

    private static class ViewHolderOffline extends RecyclerView.ViewHolder {
        ViewHolderOffline(View itemView) {
            super(itemView);
        }
    }


    // View for viewholder
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View loadingView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_loading, viewGroup, false);
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.api_item, viewGroup, false);
        View offlineView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_offline, viewGroup, false);


        switch (i) {
            case TYPE_LOADING:
                return new ViewHolderLoading(loadingView);
            case TYPE_ITEM:
                return new ViewHolderItem(itemView);
            case TYPE_OFFLINE:
                return new ViewHolderOffline(offlineView);
        }

        throw new RuntimeException("there is no type that matches the type " + i + " + make sure your using types correctly");
    }


    // Populate viewholder
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolderItem) {
            Context context = ((ViewHolderItem) holder).command.getContext();

            ((ViewHolderItem) holder).command.setText(dataset.get(position).command);
            Typeface type = Typeface.createFromAsset(context.getAssets(), "SourceCodePro-Regular.ttf");
            ((ViewHolderItem) holder).command.setTypeface(type);

            ((ViewHolderItem) holder).description.setText(dataset.get(position).description);
        }
    }


    @Override
    public int getItemCount() {
        return dataset.size();
    }

    @Override

    public int getItemViewType(int position) {

        return dataset.get(position).type;

    }

}
