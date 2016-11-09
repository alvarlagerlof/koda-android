package com.alvarlagerlof.koda.Archive;

/**
 * Created author alvar on 2016-07-02.
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

import com.alvarlagerlof.koda.Utils.AnimationUtils;
import com.alvarlagerlof.koda.Comments.CommentsActivity;
import com.alvarlagerlof.koda.LikeDissLike;
import com.alvarlagerlof.koda.Utils.DateConversionUtils;
import com.alvarlagerlof.koda.PlayActivity;
import com.alvarlagerlof.koda.PrefValues;
import com.alvarlagerlof.koda.R;

import java.util.ArrayList;

/**
 * Created author alvar on 2015-07-10.
 */


class ArchiveAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Init objects
    private ArrayList<ArchiveObject> dataset;
    private FragmentManager fragmentManager;
    private Context context;

    private static final int TYPE_HEADER      = 0;
    private static final int TYPE_LOADING     = 1;
    private static final int TYPE_ITEM        = 2;



    // Get the data
    ArchiveAdapter(ArrayList<ArchiveObject> dataset, FragmentManager fragmentManager, Context context) {
        this.dataset = dataset;
        this.fragmentManager = fragmentManager;
        this.context = context;
    }


    // View holders
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
        TextView title;
        TextView author;
        TextView description;
        TextView likesNum;
        TextView commentsNum;
        TextView metaText;
        ImageView heartImage;

        LinearLayout likes;
        LinearLayout comment;
        LinearLayout more;

        LinearLayout expandCollapse;
        LinearLayout expandablePanel;
        ImageView expandCollapseImage;
        
        ViewHolderItem(View itemView) {
            super(itemView);
            title        = (TextView) itemView.findViewById(R.id.title);
            author = (TextView) itemView.findViewById(R.id.by);
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



    // View for viewholder
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


    // Bind views
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolderHeader) {
            ((ViewHolderHeader) holder).text1.setText("Alla som programmerar i labbet kan välja att visa upp sin skapelse här");
        } else if (holder instanceof ViewHolderItem) {
            ((ViewHolderItem) holder).title.setText(dataset.get(position).title);
            ((ViewHolderItem) holder).author.setText(dataset.get(position).author);
            ((ViewHolderItem) holder).description.setText(dataset.get(position).description);
            ((ViewHolderItem) holder).likesNum.setText(dataset.get(position).likeCount);
            ((ViewHolderItem) holder).commentsNum.setText(dataset.get(position).commentCount);
            ((ViewHolderItem) holder).metaText.setText(dataset.get(position).charCount + " tecken | Uppdaterad " + DateConversionUtils.convert(dataset.get(position).date));

            if (dataset.get(position).liked) {
                ((ViewHolderItem) holder).heartImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart));
            } else {
                ((ViewHolderItem) holder).heartImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart_outline));
            }

            ((ViewHolderItem) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(((ViewHolderItem) holder).title.getContext(), PlayActivity.class);
                    intent.putExtra("public_id", dataset.get(position).publicId);
                    intent.putExtra("title", dataset.get(position).title);
                    ((ViewHolderItem) holder).title.getContext().startActivity(intent);
                }
            });

            ((ViewHolderItem) holder).expandCollapse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((ViewHolderItem) holder).expandablePanel.getVisibility() == View.GONE) {
                        ((ViewHolderItem) holder).expandCollapseImage.setImageDrawable(ContextCompat.getDrawable(((ViewHolderItem) holder).expandablePanel.getContext(), R.drawable.ic_collapse));
                        AnimationUtils.expand(((ViewHolderItem) holder).expandablePanel);
                    } else {
                        ((ViewHolderItem) holder).expandCollapseImage.setImageDrawable(ContextCompat.getDrawable(((ViewHolderItem) holder).expandablePanel.getContext(), R.drawable.ic_expand));
                        AnimationUtils.collapse(((ViewHolderItem) holder).expandablePanel);
                    }
                }
            });


            ((ViewHolderItem) holder).likes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (dataset.get(position).liked) {

                        ((ViewHolderItem) holder).likesNum.setText(String.valueOf(Integer.parseInt(String.valueOf(Integer.parseInt(String.valueOf(((ViewHolderItem) holder).likesNum.getText())) - 1))));
                        ((ViewHolderItem) holder).heartImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart_outline));
                        dataset.get(position).liked = false;
                        new LikeDissLike(context, PrefValues.URL_DISSLIKE, dataset.get(position).publicId);

                    } else {

                        ((ViewHolderItem) holder).likesNum.setText(String.valueOf(Integer.parseInt(String.valueOf(((ViewHolderItem) holder).likesNum.getText()) + 1)));
                        ((ViewHolderItem) holder).heartImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart));
                        dataset.get(position).liked = true;
                        new LikeDissLike(context, PrefValues.URL_LIKE, dataset.get(position).publicId);

                    }

                }
            });

            ((ViewHolderItem) holder).comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(((ViewHolderItem) holder).title.getContext(), CommentsActivity.class);
                    intent.putExtra("public_id", dataset.get(position).publicId);
                    intent.putExtra("title", dataset.get(position).title);
                    ((ViewHolderItem) holder).title.getContext().startActivity(intent);
                }
            });

            ((ViewHolderItem) holder).more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArchiveBottomSheetFragment bottomSheetFragment = new ArchiveBottomSheetFragment();
                    bottomSheetFragment.passData(dataset.get(position).publicId, dataset.get(position).title, dataset.get(position).author);

                    bottomSheetFragment.show(fragmentManager, bottomSheetFragment.getTag());
                }
            });

        }
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else if (dataset.get(position).publicId.equals("Loading")) {
            return TYPE_LOADING;
        }
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }


}
