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
    private static final int TYPE_LOADING     = 0;
    private static final int TYPE_ITEM        = 1;




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



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View loadingView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_loading, viewGroup, false);
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.projects_item, viewGroup, false);

        switch (i) {
            case TYPE_LOADING:
                return new ViewHolderLoading(loadingView);
            case TYPE_ITEM:
                return new ViewHolderItem(itemView);
        }

        throw new RuntimeException("there is no type that matches the type " + i + " + make sure your using types correctly");
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
       if (holder instanceof ViewHolderItem) {
            final Context context = ((ViewHolderItem) holder).title.getContext();

            ((ViewHolderItem) holder).title.setText(dataset.get(position).title);
            ((ViewHolderItem) holder).meta.setText(dataset.get(position).updated);


            ((ViewHolderItem) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EditorActivity.class);
                    intent.putExtra("privateID", dataset.get(position).privateId);
                    intent.putExtra("publicID", dataset.get(position).publicId);
                    intent.putExtra("title", dataset.get(position).title);
                    intent.putExtra("code", dataset.get(position).code);
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
        return dataset.get(position).privateId.equals("Loading") ? TYPE_LOADING : TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

}
