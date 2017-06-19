package com.alvarlagerlof.koda.Guides;

/**
 * Created by alvar on 2016-07-02.
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alvarlagerlof.koda.Guides.Image.GuidesImageObject;
import com.alvarlagerlof.koda.Guides.Image.GuidesImageViewHolder;
import com.alvarlagerlof.koda.Guides.Line.GuidesLineHolder;
import com.alvarlagerlof.koda.Guides.Line.GuidesLineObject;
import com.alvarlagerlof.koda.Guides.SmallImageWithButton.GuidesSmallImageWithButtonObject;
import com.alvarlagerlof.koda.Guides.SmallImageWithButton.GuidesSmallImageWithButtonViewHolder;
import com.alvarlagerlof.koda.Guides.Text.GuidesTextObject;
import com.alvarlagerlof.koda.Guides.Text.GuidesTextViewHolder;
import com.alvarlagerlof.koda.Guides.TextWithTitle.GuidesTextWithTitleObject;
import com.alvarlagerlof.koda.Guides.TextWithTitle.GuidesTextWithTitleViewHolder;
import com.alvarlagerlof.koda.Guides.YoutubeList.GuidesYouTubeListObject;
import com.alvarlagerlof.koda.Guides.YoutubeList.GuidesYoutubeListViewHolder;
import com.alvarlagerlof.koda.Guides.YoutubeList.ListInside.GuidesYouTubeListAdapter;
import com.alvarlagerlof.koda.Guides.YoutubeList.ListInside.GuidesYouTubeListPlaylistObject;
import com.alvarlagerlof.koda.Guides.YoutubeList.ListInside.GuidesYouTubeListVideoObject;
import com.alvarlagerlof.koda.R;
import com.alvarlagerlof.koda.Universal.UniversalLoadingObject;
import com.alvarlagerlof.koda.Universal.UniversalLodingViewHolder;
import com.alvarlagerlof.koda.Universal.UniversalOfflineObject;
import com.alvarlagerlof.koda.Universal.UniversalOfflineViewHolder;
import com.bumptech.glide.Glide;
import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by alvar on 2015-07-10.
 */


public class GuidesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TYPE_LOADING                 = 0;
    private final int TYPE_OFFLINE                 = 1;
    private final int TYPE_LINE                    = 2;
    private final int TYPE_TEXT                    = 3;
    private final int TYPE_TEXT_WITH_TITLE         = 4;
    private final int TYPE_IMAGE                   = 5;
    private final int TYPE_SMALL_IMAGE_WITH_BUTTON = 6;
    private final int TYPE_YOUTUBE_LIST            = 7;

    private Context context;
    private ArrayList<Object> dataset;


    GuidesAdapter(Context context, ArrayList<Object> dataset) {
        this.context = context;
        this.dataset = dataset;
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (i) {
            // Universal
            case TYPE_LOADING:
                return new UniversalLodingViewHolder(inflater.inflate(R.layout.item_loading, viewGroup, false));

            case TYPE_OFFLINE:
                return new UniversalOfflineViewHolder(inflater.inflate(R.layout.item_offline, viewGroup, false));



            // Specific
            case TYPE_LINE:
                return new GuidesLineHolder(inflater.inflate(R.layout.guides_line_item, viewGroup, false));

            case TYPE_TEXT:
                return new GuidesTextViewHolder(inflater.inflate(R.layout.guides_text_item, viewGroup, false));

            case TYPE_TEXT_WITH_TITLE:
                return new GuidesTextWithTitleViewHolder(inflater.inflate(R.layout.guides_text_with_title_item, viewGroup, false));

            case TYPE_IMAGE:
                return new GuidesImageViewHolder(inflater.inflate(R.layout.guides_image_item, viewGroup, false));

            case TYPE_SMALL_IMAGE_WITH_BUTTON:
                return new GuidesSmallImageWithButtonViewHolder(inflater.inflate(R.layout.guides_small_image_with_button_item, viewGroup, false));

            case TYPE_YOUTUBE_LIST:
                return new GuidesYoutubeListViewHolder(inflater.inflate(R.layout.guides_youtube_list_item, viewGroup, false));

        }

        throw new RuntimeException("there is no type that matches the type " + i + " + make sure your using types correctly");
    }






    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        // Items such as loading and offline don't need
        // be here because their views are not changed
        // depending on the data


        // Text
        if (holder instanceof GuidesTextViewHolder) {
            GuidesTextObject object = (GuidesTextObject) dataset.get(position);

            ((GuidesTextViewHolder) holder).text.setText(object.text);
        }



        // Text with title
        if (holder instanceof GuidesTextWithTitleViewHolder) {
            GuidesTextWithTitleObject object = (GuidesTextWithTitleObject) dataset.get(position);

            ((GuidesTextWithTitleViewHolder) holder).title.setText(object.title);
            ((GuidesTextWithTitleViewHolder) holder).text.setText(object.text);
        }



        // Image
        if (holder instanceof GuidesImageViewHolder) {
            final GuidesImageObject object = (GuidesImageObject) dataset.get(position);

            // Image
            Glide.with(((GuidesImageViewHolder) holder).image.getContext())
                    .load(object.imageUrl)
                    .into(((GuidesImageViewHolder) holder).image);

            // On click
            ((GuidesImageViewHolder) holder).image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((GuidesImageViewHolder) holder).itemView
                            .getContext()
                            .startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(object.link)));
                }
            });
        }



        // Small image with button
        if (holder instanceof GuidesSmallImageWithButtonViewHolder) {
            final GuidesSmallImageWithButtonObject object = (GuidesSmallImageWithButtonObject) dataset.get(position);

            // Image
            Glide.with(((GuidesSmallImageWithButtonViewHolder) holder).image.getContext())
                    .load(object.imageUrl)
                    .into(((GuidesSmallImageWithButtonViewHolder) holder).image);

            // Set button text
            ((GuidesSmallImageWithButtonViewHolder) holder).button.setText(object.text);

            // On button click
            ((GuidesSmallImageWithButtonViewHolder) holder).button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((GuidesSmallImageWithButtonViewHolder) holder).itemView
                            .getContext()
                            .startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(object.link)));
                }
            });
        }



        // Youtube list
        if (holder instanceof GuidesYoutubeListViewHolder) {
            GuidesYouTubeListObject object = (GuidesYouTubeListObject) dataset.get(position);

            // Create list
            ArrayList<Object> list = new ArrayList<>();

            // Add all items
            for (int i = 0; i < object.videos.length(); i++) {
                try {
                    switch (object.videos.getJSONObject(i).getString("type")) {
                        case "video":
                            list.add(new GuidesYouTubeListVideoObject(object.videos.getJSONObject(i).getJSONObject("content").getString("link"),
                                                                      object.videos.getJSONObject(i).getJSONObject("content").getString("imageUrl"),
                                                                      object.videos.getJSONObject(i).getJSONObject("content").getString("title")));
                            break;

                        case "playlist":
                            list.add(new GuidesYouTubeListPlaylistObject(object.videos.getJSONObject(i).getJSONObject("content").getString("link"),
                                                                         object.videos.getJSONObject(i).getJSONObject("content").getString("title")));
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    FirebaseCrash.report(e);
                }
            }

            // Set LayoutManager and Adapter
            ((GuidesYoutubeListViewHolder) holder).recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            ((GuidesYoutubeListViewHolder) holder).recyclerView.setAdapter(new GuidesYouTubeListAdapter(list));

        }




    }



    @Override
    public int getItemViewType(int position) {

        // Universal
        if (dataset.get(position) instanceof UniversalLoadingObject) {
            return TYPE_LOADING;
        }

        if (dataset.get(position) instanceof UniversalOfflineObject) {
            return TYPE_OFFLINE;
        }


        // Specific
        if (dataset.get(position) instanceof GuidesLineObject) {
            return TYPE_LINE;
        }

        if (dataset.get(position) instanceof GuidesTextObject) {
            return TYPE_TEXT;
        }

        if (dataset.get(position) instanceof GuidesTextWithTitleObject) {
            return TYPE_TEXT_WITH_TITLE;
        }

        if (dataset.get(position) instanceof GuidesImageObject) {
            return TYPE_IMAGE;
        }

        if (dataset.get(position) instanceof GuidesSmallImageWithButtonObject) {
            return TYPE_SMALL_IMAGE_WITH_BUTTON;
        }

        if (dataset.get(position) instanceof GuidesYouTubeListObject) {
            return TYPE_YOUTUBE_LIST;
        }



        throw new RuntimeException("there is no type that matches the type " + position + " + make sure your using types correctly");

    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }


}
