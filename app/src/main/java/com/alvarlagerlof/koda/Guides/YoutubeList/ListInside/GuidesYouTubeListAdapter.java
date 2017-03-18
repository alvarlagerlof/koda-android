package com.alvarlagerlof.koda.Guides.YoutubeList.ListInside;

/**
 * Created by alvar on 2016-07-02.
 */

import android.content.Intent;
import android.net.Uri;
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


public class GuidesYouTubeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TYPE_VIDEO    = 0;
    private final int TYPE_PLAYLIST = 1;



    private ArrayList<Object> dataset;

    public GuidesYouTubeListAdapter(ArrayList<Object> dataset) {
        this.dataset = dataset;
    }




    private static class ViewHolderVideo extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;

         ViewHolderVideo(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            title = (TextView) itemView.findViewById(R.id.title);
        }
    }


    private static class ViewHolderPlayList extends RecyclerView.ViewHolder  {
        TextView title;

        ViewHolderPlayList(View itemView){
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
        }
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (i) {
            case TYPE_VIDEO:
                return new ViewHolderVideo(inflater.inflate(R.layout.guides_youtube_list_video_item, viewGroup, false));

            case TYPE_PLAYLIST:
                return new ViewHolderPlayList(inflater.inflate(R.layout.guides_youtube_list_playlist_item, viewGroup, false));
        }

        throw new RuntimeException("there is no type that matches the type " + i + " + make sure your using types correctly");
    }



    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolderPlayList) {

            final GuidesYouTubeListPlaylistObject object = (GuidesYouTubeListPlaylistObject) dataset.get(position);

            ((ViewHolderPlayList) holder).title.setText(object.title);

            ((ViewHolderPlayList) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(object.link));
                    ((ViewHolderPlayList) holder).itemView.getContext().startActivity(browserIntent);
                }
            });

        }

        if (holder instanceof ViewHolderVideo) {

            final GuidesYouTubeListVideoObject object = (GuidesYouTubeListVideoObject) dataset.get(position);

            // Set title
            ((ViewHolderVideo) holder).title.setText(object.title);

            // Set image
            Glide.with(((ViewHolderVideo) holder).title.getContext())
                    .load(object.imageUrl)
                    .diskCacheStrategy( DiskCacheStrategy.RESULT)
                    .into(((ViewHolderVideo) holder).image);

            // On click
            ((ViewHolderVideo) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ViewHolderVideo) holder).itemView
                            .getContext()
                            .startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(object.link)));
                }
            });

        }
    }


    @Override
    public int getItemViewType(int position) {

        if (dataset.get(position) instanceof GuidesYouTubeListVideoObject) {
            return TYPE_VIDEO;

        } else if (dataset.get(position) instanceof GuidesYouTubeListPlaylistObject) {
            return TYPE_PLAYLIST;
        }


        throw new RuntimeException("there is no type that matches the type " + position + " + make sure your using types correctly");

    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }


}
