package com.alvarlagerlof.koda.Projects;

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

import com.alvarlagerlof.koda.Editor.EditorActivity;
import com.alvarlagerlof.koda.R;

import java.util.ArrayList;

/**
 * Created by alvar on 2015-07-10.
 */


public class ProjectsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private FragmentManager fragmentManager;
    private ArrayList<ProjectsObject> dataset;
    static final int TYPE_LOADING  = 0;
    static final int TYPE_ITEM     = 1;
    static final int TYPE_NO_ITEMS = 2;




    ProjectsAdapter(ArrayList<ProjectsObject> dataset, FragmentManager fragmentManager) {
        this.dataset = dataset;
        this.fragmentManager = fragmentManager;
    }


    private static class ViewHolderLoading extends RecyclerView.ViewHolder  {
        ViewHolderLoading(View itemView){
            super(itemView);
        }
    }

    private static class ViewHolderItem extends RecyclerView.ViewHolder {
        TextView title;
        TextView meta;
        LinearLayout more;

        ViewHolderItem(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            meta = (TextView) itemView.findViewById(R.id.meta);
            more = (LinearLayout) itemView.findViewById(R.id.more);
        }

    }

    private static class ViewHolderNoItems extends RecyclerView.ViewHolder {
        ViewHolderNoItems(View itemView) {
            super(itemView);
        }

    }




    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View loadingView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_loading, viewGroup, false);
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.projects_item, viewGroup, false);
        View noItemsView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.projects_item_no_items, viewGroup, false);

        switch (i) {
            case TYPE_LOADING:
                return new ViewHolderLoading(loadingView);
            case TYPE_ITEM:
                return new ViewHolderItem(itemView);
            case TYPE_NO_ITEMS:
                return new ViewHolderNoItems(noItemsView);
        }

        throw new RuntimeException("there is no type that matches the type " + i + " + make sure your using types correctly");
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
       if (holder instanceof ViewHolderItem) {
            final Context context = ((ViewHolderItem) holder).title.getContext();

            ((ViewHolderItem) holder).title.setText(dataset.get(position).title);
            ((ViewHolderItem) holder).meta.setText(dataset.get(position).charCount + " karaktärer, "
                    + dataset.get(position).likeCount + " likes, "
                    + dataset.get(position).commentCount + " kommentarer");


            ((ViewHolderItem) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EditorActivity.class);
                    intent.putExtra("privateID", dataset.get(position).privateId);
                    intent.putExtra("publicID", dataset.get(position).publicId);
                    intent.putExtra("title", dataset.get(position).title);
                    context.startActivity(intent);
                }
            });


            ((ViewHolderItem) holder).more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProjectsBottomSheetFragment bottomSheetFragment = new ProjectsBottomSheetFragment();
                    bottomSheetFragment.passData(fragmentManager,
                            dataset.get(position).privateId,
                            dataset.get(position).publicId,
                            dataset.get(position).title,
                            dataset.get(position).description,
                            dataset.get(position).isPublic,
                            position);

                    bottomSheetFragment.show(fragmentManager, bottomSheetFragment.getTag());

                }
            });



        }
    }



    @Override
    public int getItemViewType(int position) {

        switch (dataset.get(position).type) {
            case TYPE_ITEM: return TYPE_ITEM;
            case TYPE_LOADING: return TYPE_LOADING;
            case TYPE_NO_ITEMS: return TYPE_NO_ITEMS;
            default: return TYPE_ITEM;
        }

    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

}
