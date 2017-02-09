package com.alvarlagerlof.koda.Archive;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.alvarlagerlof.koda.DividerItemDecoration;
import com.alvarlagerlof.koda.R;

import java.util.ArrayList;

/**
 * Created author alvar on 2016-07-02.
 */
public class ArchiveFragment extends Fragment {

    ArchiveAdapter adapter;
    ArrayList<ArchiveObject> list = new ArrayList<>();
    ArrayList<ArchiveObject> listVisible = new ArrayList<>();

    boolean firstLetter = true;

    LinearLayout searchFieldLayout;
    EditText searchField;
    ImageButton clearButton;


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.archive_fragment, container, false);


        searchFieldLayout = (LinearLayout) view.findViewById(R.id.search_field_layout);
        searchField = (EditText) view.findViewById(R.id.search_field);

        clearButton = (ImageButton) view.findViewById(R.id.clear_button);



        // Recyclerview
        adapter = new ArchiveAdapter(listVisible, getActivity().getSupportFragmentManager(), getContext());

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext()));
        recyclerView.setAdapter(adapter);


        // Get data
        ArchiveGetData dataTask = new ArchiveGetData(getContext(), listVisible, adapter);
        dataTask.execute();






        // Search
        searchField.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                // Show and hide clear button
                if (s.length() > 0) {
                    clearButton.setVisibility(View.VISIBLE);
                } else {
                    clearButton.setVisibility(View.GONE);
                }


                // Clone original list
                if (firstLetter) {
                    list.addAll(listVisible);
                    firstLetter = false;
                }


                // Clear visible list
                listVisible.clear();


                // Do the search
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).type == ArchiveAdapter.TYPE_ITEM) {
                        if (list.get(i).title.toLowerCase().contains(String.valueOf(searchField.getText()).toLowerCase()) || list.get(i).author.toLowerCase().contains(String.valueOf(searchField.getText()).toLowerCase())) {
                            listVisible.add(list.get(i));
                        }
                    }

                }


                // If no results
                if (listVisible.isEmpty()) {
                    listVisible.add(new ArchiveObject("", "", "", "", "", "", "", "", false, ArchiveAdapter.TYPE_NO_RESULTS));
                }


                // Notify adapter
                adapter.notifyDataSetChanged();

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });


        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchField.setText("");
            }
        });

        return view;
    }

}
