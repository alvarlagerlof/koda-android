package com.alvarlagerlof.koda.Comments;

/**
 * Created by alvar on 2016-07-02.
 */

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


class CommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<CommentsObject> dataset;
    static final int TYPE_HEADER      = 0;
    static final int TYPE_LOADING     = 1;
    static final int TYPE_ITEM        = 2;
    static final int TYPE_OFFLINE     = 3;
    static final int TYPE_NO_COMMENTS = 4;


    CommentsAdapter(ArrayList<CommentsObject> dataset) {
        this.dataset = dataset;
    }


    private static class ViewHolderHeader extends RecyclerView.ViewHolder  {
        TextView text1;

        ViewHolderHeader(View itemView){
            super(itemView);
            text1 = (TextView) itemView.findViewById(R.id.text1);
        }
    }

    private static class ViewHolderLoading extends RecyclerView.ViewHolder  {
        ViewHolderLoading(View itemView){
            super(itemView);
        }
    }

    private static class ViewHolderItem extends RecyclerView.ViewHolder {
        TextView author;
        TextView date;
        TextView comment;

        ViewHolderItem(View itemView) {
            super(itemView);
            author      = (TextView) itemView.findViewById(R.id.by);
            date = (TextView) itemView.findViewById(R.id.meta);
            comment = (TextView) itemView.findViewById(R.id.comment);
        }

    }

    private static class ViewHolderOffline extends RecyclerView.ViewHolder  {
        ViewHolderOffline(View itemView){
            super(itemView);
        }
    }

    private static class ViewHolderNoComemnts extends RecyclerView.ViewHolder  {
        ViewHolderNoComemnts(View itemView){
            super(itemView);
        }
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View headerView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_header, viewGroup, false);
        View loadingView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_loading, viewGroup, false);
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comments_item, viewGroup, false);
        View offlineView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_offline, viewGroup, false);
        View noCommentsView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comments_no_comments_item, viewGroup, false);


        switch (i) {
            case TYPE_HEADER:
                return new ViewHolderHeader(headerView);
            case TYPE_LOADING:
                return new ViewHolderLoading(loadingView);
            case TYPE_ITEM:
                return new ViewHolderItem(itemView);
            case TYPE_OFFLINE:
                return new ViewHolderOffline(offlineView);
            case TYPE_NO_COMMENTS:
                return new ViewHolderNoComemnts(noCommentsView);
        }

        throw new RuntimeException("there is no type that matches the type " + i + " + make sure your using types correctly");
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolderHeader) {

            ((ViewHolderHeader) holder).text1.setText("Alla kommentarer är publika, men man kan inte kommentera utan att vara inloggad. Om du ser en dum kommentar kontakta mig direkt på mikael@roboro.se. Dumma kommentarer tolereras inte utan leder till avstängning på livstid.");

        } else if (holder instanceof ViewHolderItem) {

            ((ViewHolderItem) holder).author.setText(dataset.get(position).author);
            ((ViewHolderItem) holder).date.setText(dataset.get(position).date);
            ((ViewHolderItem) holder).comment.setText(dataset.get(position).comment);

        }
    }



    @Override
    public int getItemViewType(int position) {

        switch (dataset.get(position).type) {
            case TYPE_HEADER: return TYPE_HEADER;
            case TYPE_LOADING: return TYPE_LOADING;
            case TYPE_ITEM: return TYPE_ITEM;
            case TYPE_OFFLINE: return TYPE_OFFLINE;
            case TYPE_NO_COMMENTS: return TYPE_NO_COMMENTS;
            default: return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }


}
