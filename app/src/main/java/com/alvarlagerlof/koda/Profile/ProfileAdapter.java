package com.alvarlagerlof.koda.Profile;

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
import com.alvarlagerlof.koda.PlayActivity;
import com.alvarlagerlof.koda.R;

import java.util.ArrayList;

/**
 * Created by alvar on 2015-07-10.
 */


public class ProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private FragmentManager fragmentManager;

    private ArrayList<ProfileObject> dataset;

    private Context context;

    private static final int TYPE_HEADER      = 0;
    private static final int TYPE_LOADING     = 1;
    private static final int TYPE_ITEM        = 2;


    public ProfileAdapter(ArrayList<ProfileObject> dataset, FragmentManager fragmentManager, Context context) {
        this.dataset = dataset;
        this.fragmentManager = fragmentManager;
        this.context = context;
    }


    public static class ViewHolderHeader extends RecyclerView.ViewHolder  {
        public final TextView author;
        public final TextView numberOfProjects;

        public ViewHolderHeader(View itemView){
            super(itemView);
            author = (TextView) itemView.findViewById(R.id.author);
            numberOfProjects = (TextView) itemView.findViewById(R.id.numberOfProjects);

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
        public final TextView charCount;
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
            likesNum     = (TextView) itemView.findViewById(R.id.likes_num);
            commentsNum  = (TextView) itemView.findViewById(R.id.comments_num);
            charCount    = (TextView) itemView.findViewById(R.id.char_count);
            heartImage   = (ImageView) itemView.findViewById(R.id.heart_image);

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
        View headerView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.profile_header, viewGroup, false);
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

            ((ViewHolderHeader) holder).author.setText(dataset.get(position).author);
            ((ViewHolderHeader) holder).numberOfProjects.setText(dataset.get(position).numberOfProjects + " projekt");

        } else if (holder instanceof ViewHolderItem) {

            ((ProfileAdapter.ViewHolderItem) holder).title.setText(dataset.get(position).title);
            ((ProfileAdapter.ViewHolderItem) holder).by.setText(dataset.get(position).author);
            ((ProfileAdapter.ViewHolderItem) holder).description.setText(dataset.get(position).description);
            ((ProfileAdapter.ViewHolderItem) holder).likesNum.setText(dataset.get(position).likeCount);
            ((ProfileAdapter.ViewHolderItem) holder).commentsNum.setText(dataset.get(position).commentCount);
            ((ProfileAdapter.ViewHolderItem) holder).charCount.setText(dataset.get(position).charCount + " " + context.getString(R.string.characters));

            if (dataset.get(position).liked) {
                ((ProfileAdapter.ViewHolderItem) holder).heartImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart));
            } else {
                ((ProfileAdapter.ViewHolderItem) holder).heartImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart_outline));
            }

            ((ProfileAdapter.ViewHolderItem) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(((ProfileAdapter.ViewHolderItem) holder).title.getContext(), PlayActivity.class);
                    intent.putExtra("public_id", dataset.get(position).publicId);
                    intent.putExtra("title", dataset.get(position).title);
                    ((ProfileAdapter.ViewHolderItem) holder).title.getContext().startActivity(intent);
                }
            });


            ((ProfileAdapter.ViewHolderItem) holder).expandCollapse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((ProfileAdapter.ViewHolderItem) holder).expandablePanel.getVisibility() == View.GONE) {
                        ((ProfileAdapter.ViewHolderItem) holder).expandCollapseImage.setImageDrawable(ContextCompat.getDrawable(((ProfileAdapter.ViewHolderItem) holder).expandablePanel.getContext(), R.drawable.ic_collapse));
                        Animations.expand(((ProfileAdapter.ViewHolderItem) holder).expandablePanel);
                    } else {
                        ((ProfileAdapter.ViewHolderItem) holder).expandCollapseImage.setImageDrawable(ContextCompat.getDrawable(((ProfileAdapter.ViewHolderItem) holder).expandablePanel.getContext(), R.drawable.ic_expand));
                        Animations.collapse(((ProfileAdapter.ViewHolderItem) holder).expandablePanel);
                    }
                }
            });



            ((ProfileAdapter.ViewHolderItem) holder).likes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (dataset.get(position).liked) {
                        int likes = Integer.parseInt(String.valueOf(((ProfileAdapter.ViewHolderItem) holder).likesNum.getText()));
                        likes -= 1;
                        ((ProfileAdapter.ViewHolderItem) holder).likesNum.setText(String.valueOf(likes));
                        ((ProfileAdapter.ViewHolderItem) holder).heartImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart_outline));

                        dataset.get(position).liked = false;

                        LikeDissLike.dislike(context, dataset.get(position).publicId);

                    } else {
                        int likes = Integer.parseInt(String.valueOf(((ProfileAdapter.ViewHolderItem) holder).likesNum.getText()));
                        likes += 1;
                        ((ProfileAdapter.ViewHolderItem) holder).likesNum.setText(String.valueOf(likes));
                        ((ProfileAdapter.ViewHolderItem) holder).heartImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart));

                        dataset.get(position).liked = true;

                        LikeDissLike.like(context, dataset.get(position).publicId);
                    }

                }
            });

            ((ProfileAdapter.ViewHolderItem) holder).comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(((ProfileAdapter.ViewHolderItem) holder).title.getContext(), CommentsActivity.class);
                    intent.putExtra("public_id", dataset.get(position).publicId);
                    intent.putExtra("title", dataset.get(position).title);
                    ((ProfileAdapter.ViewHolderItem) holder).title.getContext().startActivity(intent);
                }
            });

            ((ProfileAdapter.ViewHolderItem) holder).more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProfileBottomSheetFragment bottomSheetFragment = new ProfileBottomSheetFragment();
                    bottomSheetFragment.passData(dataset.get(position).publicId, dataset.get(position).title, dataset.get(position).author);

                    bottomSheetFragment.show(fragmentManager, bottomSheetFragment.getTag());
                }
            });

        }
    }


    @Override
    public int getItemViewType(int position) {
        if (!dataset.get(position).numberOfProjects.equals("")) {
            return TYPE_HEADER;
        } else if (dataset.get(position).author.equals("Loading")) {
            return TYPE_LOADING;
        }
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }


}
