package com.example.vedi;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.vedi.placeholder.PlaceholderContent;
import com.example.vedi.ui.slideshow.SlideshowViewModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class VediGraphFragment extends Fragment {

    // TODO: Customize parameters
    private int mColumnCount = 1;
    private static final String PREFS = "shared_prefs";
    private static final String SAVED_GRAPHS = "saved_graphs";

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    ImageView delete;

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static VediGraphFragment newInstance(int columnCount) {
        VediGraphFragment fragment = new VediGraphFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public VediGraphFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            MyVediGraphRecyclerViewAdapter adapter = new MyVediGraphRecyclerViewAdapter(context);
            recyclerView.setAdapter(adapter);
            SlideshowViewModel savedGraphsViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(SlideshowViewModel.class);
            savedGraphsViewModel.getVediGraphs().observe(getViewLifecycleOwner(), new Observer<List<VediGraph>>() {
                @Override
                public void onChanged(List<VediGraph> graphs) {
                    adapter.setVediGraphs(graphs);
                }
            });
        }
        return view;

    }
    private ArrayList<VediGraph> getGraphs() {
        SharedPreferences pref = requireActivity().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String graphsJSON = pref.getString(SAVED_GRAPHS, "");
        Type type = new TypeToken<ArrayList<VediGraph>>(){}.getType();
        ArrayList<VediGraph> graphs = gson.fromJson(graphsJSON, type);
        if(graphs == null) return new ArrayList<>();
        else return graphs;
    }
}