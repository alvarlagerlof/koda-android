package com.alvarlagerlof.koda.MyProjects;

/**
 * Created by alvar on 2016-07-02.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alvarlagerlof.koda.Comments.CommentsActivity;
import com.alvarlagerlof.koda.EditorActivity;
import com.alvarlagerlof.koda.R;

import java.util.ArrayList;

/**
 * Created by alvar on 2015-07-10.
 */


public class MyProjectsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<MyProjectsObject> dataset;

    private static final int TYPE_HEADER      = 0;
    private static final int TYPE_LOADING     = 1;
    private static final int TYPE_ITEM        = 2;


    FragmentManager fragmentManager;


    public MyProjectsAdapter(ArrayList<MyProjectsObject> dataset, FragmentManager fragmentManager) {
        this.dataset = dataset;
        this.fragmentManager = fragmentManager;
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
        public final TextView date;
        //public final TextView likesNum;
        public final TextView comments_num;

        //public final LinearLayout likes;
        public final LinearLayout comment;
        public final LinearLayout more;

        public ViewHolderItem(View itemView) {
            super(itemView);
            title        = (TextView) itemView.findViewById(R.id.title);
            date           = (TextView) itemView.findViewById(R.id.date);
            //likesNum    = (TextView) itemView.findViewById(R.id.likesNum);
            comments_num = (TextView) itemView.findViewById(R.id.coments_num);

            //likes       = (LinearLayout) itemView.findViewById(R.id.likes);
            comment     = (LinearLayout) itemView.findViewById(R.id.comments);
            more        = (LinearLayout) itemView.findViewById(R.id.more);
        }

    }




    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View headerView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_header, viewGroup, false);
        View loadingView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_loading, viewGroup, false);
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.my_projects_item, viewGroup, false);

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

            ((ViewHolderHeader) holder).text1.setText("Här samlas alla din projekt");

        } else if (holder instanceof ViewHolderItem) {
            final Context context = ((ViewHolderItem) holder).title.getContext();

            ((ViewHolderItem) holder).title.setText(dataset.get(position).getTitle());
            ((ViewHolderItem) holder).date.setText(dataset.get(position).getUpdated());

            ((ViewHolderItem) holder).comments_num.setText(dataset.get(position).getCommentCount());


            ((ViewHolderItem) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EditorActivity.class);
                    intent.putExtra("private_id", dataset.get(position).getPrivateId());
                    intent.putExtra("public_id", dataset.get(position).getPublicId());
                    intent.putExtra("title", dataset.get(position).getTitle());
                    context.startActivity(intent);
                }
            });

            ((ViewHolderItem) holder).comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(((ViewHolderItem) holder).title.getContext(), CommentsActivity.class);
                    intent.putExtra("public_id", dataset.get(position).getPublicId());
                    intent.putExtra("title", dataset.get(position).getTitle());
                    ((ViewHolderItem) holder).title.getContext().startActivity(intent);
                }
            });


            ((ViewHolderItem) holder).more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyProjectsBottomSheetFragment bottomSheetFragment = new MyProjectsBottomSheetFragment();
                    bottomSheetFragment.passData(fragmentManager,
                            dataset.get(position).getPrivateId(),
                            dataset.get(position).getPublicId(),
                            dataset.get(position).getTitle(),
                            dataset.get(position).getDescription(),
                            dataset.get(position).getIsPublic(),
                            position);

                    bottomSheetFragment.show(fragmentManager, bottomSheetFragment.getTag());

                }
            });



        }
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else if (dataset.get(position).getPrivateId().equals("Loading")) {
            return TYPE_LOADING;
        }

        return TYPE_ITEM;
    }


    @Override
    public int getItemCount() {
        return dataset.size();
    }

}