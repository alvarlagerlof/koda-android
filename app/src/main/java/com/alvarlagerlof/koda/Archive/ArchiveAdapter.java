package com.alvarlagerlof.koda.Archive;

/**
 * Created by alvar on 2016-07-02.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alvarlagerlof.koda.Animations;
import com.alvarlagerlof.koda.Comments.CommentsActivity;
import com.alvarlagerlof.koda.LikeDissLike;
import com.alvarlagerlof.koda.NiceDate;
import com.alvarlagerlof.koda.PlayActivity;
import com.alvarlagerlof.koda.R;

import java.util.ArrayList;

/**
 * Created by alvar on 2015-07-10.
 */


public class ArchiveAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ArchiveObject> mDataset;

    private static final int TYPE_HEADER      = 0;
    private static final int TYPE_LOADING     = 1;
    private static final int TYPE_ITEM        = 2;

    FragmentManager fragmentManager;

    Context context;

    public ArchiveAdapter(ArrayList<ArchiveObject> mDataset, FragmentManager fragmentManager, Context context) {
        this.mDataset = mDataset;
        this.fragmentManager = fragmentManager;
        this.context = context;
    }


    public static class ViewHolderHeader extends RecyclerView.ViewHolder  {
        public final TextView text1;

        public ViewHolderHeader(View itemView){
            super(itemView);
            text1 = (TextView) itemView.findViewById(R.id.text1);
        }
    }

    public static class ViewHolderLoading extends RecyclerView.ViewHolder  {

        public ViewHolderLoading(View itemView){
            super(itemView);
        }
    }

    public static class ViewHolderItem extends RecyclerView.ViewHolder {
        public final TextView title;
        public final TextView by;
        public final TextView description;
        public final TextView likesNum;
        public final TextView commentsNum;
        public final TextView metaText;
        public final ImageView heartImage;

        public final LinearLayout likes;
        public final LinearLayout comment;
        public final LinearLayout more;

        public final LinearLayout expandCollapse;
        public final LinearLayout expandablePanel;
        public final ImageView expandCollapseImage;


        public ViewHolderItem(View itemView) {
            super(itemView);
            title        = (TextView) itemView.findViewById(R.id.title);
            by           = (TextView) itemView.findViewById(R.id.by);
            description  = (TextView) itemView.findViewById(R.id.description);
            likesNum = (TextView) itemView.findViewById(R.id.likes_num);
            commentsNum = (TextView) itemView.findViewById(R.id.comments_num);
            metaText = (TextView) itemView.findViewById(R.id.char_count);
            heartImage = (ImageView) itemView.findViewById(R.id.heart_image);

            likes       = (LinearLayout) itemView.findViewById(R.id.likes);
            comment     = (LinearLayout) itemView.findViewById(R.id.comments);
            more        = (LinearLayout) itemView.findViewById(R.id.more);

            expandCollapse = (LinearLayout) itemView.findViewById(R.id.expand_collapse);
            expandablePanel = (LinearLayout) itemView.findViewById(R.id.expandable_panel);
            expandCollapseImage = (ImageView) itemView.findViewById(R.id.expand_collapse_image);


        }

    }




    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View headerView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_header, viewGroup, false);
        View loadingView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_loading, viewGroup, false);
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.archive_item, viewGroup, false);

        switch (i) {
            case TYPE_HEADER:
                return new ViewHolderHeader(headerView);
            case TYPE_LOADING:
                return new ViewHolderLoading(loadingView);
            case TYPE_ITEM:
                return new ViewHolderItem(itemView);
        }

        throw new RuntimeException("there is no type that matches the type " + i + " + make sure your using types correctly");
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolderHeader) {
            ((ViewHolderHeader) holder).text1.setText("Alla som programmerar i labbet kan välja att visa upp sin skapelse här. Populariteten ökar om någon plussar ett spel i listan, men om du vill plussa något måste du vara inloggad.");
        } else if (holder instanceof ViewHolderItem) {
            ((ViewHolderItem) holder).title.setText(mDataset.get(position).title);
            ((ViewHolderItem) holder).by.setText(mDataset.get(position).author);
            ((ViewHolderItem) holder).description.setText(mDataset.get(position).description);
            ((ViewHolderItem) holder).likesNum.setText(mDataset.get(position).likeCount);
            ((ViewHolderItem) holder).commentsNum.setText(mDataset.get(position).commentCount);
            ((ViewHolderItem) holder).metaText.setText(mDataset.get(position).charCount + " tecken | Uppdaterad " + NiceDate.convert(mDataset.get(position).date));

            if (mDataset.get(position).liked) {
                ((ViewHolderItem) holder).heartImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart));
            } else {
                ((ViewHolderItem) holder).heartImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart_outline));
            }

            ((ViewHolderItem) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(((ViewHolderItem) holder).title.getContext(), PlayActivity.class);
                    intent.putExtra("public_id", mDataset.get(position).publicId);
                    intent.putExtra("title", mDataset.get(position).title);
                    ((ViewHolderItem) holder).title.getContext().startActivity(intent);
                }
            });


            ((ViewHolderItem) holder).expandCollapse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((ViewHolderItem) holder).expandablePanel.getVisibility() == View.GONE) {
                        ((ViewHolderItem) holder).expandCollapseImage.setImageDrawable(ContextCompat.getDrawable(((ViewHolderItem) holder).expandablePanel.getContext(), R.drawable.ic_collapse));
                        Animations.expand(((ViewHolderItem) holder).expandablePanel);
                    } else {
                        ((ViewHolderItem) holder).expandCollapseImage.setImageDrawable(ContextCompat.getDrawable(((ViewHolderItem) holder).expandablePanel.getContext(), R.drawable.ic_expand));
                        Animations.collapse(((ViewHolderItem) holder).expandablePanel);
                    }
                }
            });



            ((ViewHolderItem) holder).likes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mDataset.get(position).liked) {
                        int likes = Integer.parseInt(String.valueOf(((ViewHolderItem) holder).likesNum.getText()));
                        likes -= 1;
                        ((ViewHolderItem) holder).likesNum.setText(String.valueOf(likes));

                        ((ViewHolderItem) holder).heartImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart_outline));

                        mDataset.get(position).liked = false;

                        LikeDissLike.dislike(context, mDataset.get(position).publicId);


                    } else {
                        int likes = Integer.parseInt(String.valueOf(((ViewHolderItem) holder).likesNum.getText()));
                        likes += 1;
                        ((ViewHolderItem) holder).likesNum.setText(String.valueOf(likes));

                        ((ViewHolderItem) holder).heartImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart));
                        mDataset.get(position).liked = true;


                        LikeDissLike.like(context, mDataset.get(position).publicId);

                    }

                }
            });

            ((ViewHolderItem) holder).comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(((ViewHolderItem) holder).title.getContext(), CommentsActivity.class);
                    intent.putExtra("public_id", mDataset.get(position).publicId);
                    intent.putExtra("title", mDataset.get(position).title);
                    ((ViewHolderItem) holder).title.getContext().startActivity(intent);
                }
            });

            ((ViewHolderItem) holder).more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArchiveBottomSheetFragment bottomSheetFragment = new ArchiveBottomSheetFragment();
                    bottomSheetFragment.passData(mDataset.get(position).publicId, mDataset.get(position).title, mDataset.get(position).author);

                    bottomSheetFragment.show(fragmentManager, bottomSheetFragment.getTag());
                }
            });

        }
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else if (mDataset.get(position).publicId.equals("Loading")) {
            return TYPE_LOADING;
        }
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


}
