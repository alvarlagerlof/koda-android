package com.alvarlagerlof.koda.Guides;

/**
 * Created by alvar on 2016-07-02.
 */

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alvarlagerlof.koda.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

/**
 * Created by alvar on 2015-07-10.
 */


public class YouTubeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<YouTubeObject> mDataset;

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_MORE = 1;

    FragmentManager fragmentManager;


    public YouTubeAdapter(ArrayList<YouTubeObject> myDataset) {
        mDataset = myDataset;
    }


    public static class ViewHolderMore extends RecyclerView.ViewHolder  {

        public ViewHolderMore(View itemView){
            super(itemView);

        }
    }

    public static class ViewHolderItem extends RecyclerView.ViewHolder {
        public final ImageView image;
        public final TextView title;

        public ViewHolderItem(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            title = (TextView) itemView.findViewById(R.id.title);
        }

    }




    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.guides_item, viewGroup, false);
        View moreView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.guides_more_item, viewGroup, false);

        switch (i) {
            case TYPE_ITEM:
                return new ViewHolderItem(itemView);
            case TYPE_MORE:
                return new ViewHolderMore(moreView);
        }

        throw new RuntimeException("there is no type that matches the type " + i + " + make sure your using types correctly");
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolderMore) {

            ((ViewHolderMore) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/playlist?list=PLacLTA7npkEaxlsKIL06aLqtLVKIKurRN"));
                    ((ViewHolderMore) holder).itemView.getContext().startActivity(browserIntent);
                }
            });

        } else {

            ((ViewHolderItem) holder).title.setText(mDataset.get(position).getTitle());

            Glide.with(((ViewHolderItem) holder).title.getContext())
                    .load(mDataset.get(position).getImageUrl())
                    .diskCacheStrategy( DiskCacheStrategy.RESULT)
                    .into(((ViewHolderItem) holder).image);

            ((ViewHolderItem) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mDataset.get(position).getLink()));
                    ((ViewHolderItem) holder).itemView.getContext().startActivity(browserIntent);
                }
            });

        }
    }


    @Override
    public int getItemViewType(int position) {
        if (position == mDataset.size() - 1) {
            return TYPE_MORE;
        }
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


}
