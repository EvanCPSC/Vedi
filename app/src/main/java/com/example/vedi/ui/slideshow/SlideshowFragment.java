package com.example.vedi.ui.slideshow;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vedi.MyVediGraphRecyclerViewAdapter;
import com.example.vedi.R;
import com.example.vedi.VediGraph;
import com.example.vedi.databinding.FragmentSlideshowBinding;
import com.example.vedi.ui.home.HomeViewModel;

import java.util.List;

public class SlideshowFragment extends Fragment {

    private FragmentSlideshowBinding binding;
    private SlideshowViewModel slideshowViewModel;
    private HomeViewModel homeViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        slideshowViewModel =
                new ViewModelProvider(requireActivity()).get(SlideshowViewModel.class);
        homeViewModel =
                new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        List<VediGraph> graphs = homeViewModel.getGraphs().getValue();

        final MyVediGraphRecyclerViewAdapter adapter = new MyVediGraphRecyclerViewAdapter(getContext());

        RecyclerView recyclerView = binding.recyclerview.getRoot();
        recyclerView.setAdapter(adapter);
        homeViewModel.getGraphs().observe(getViewLifecycleOwner(), new Observer<List<VediGraph>>() {
            @Override
            public void onChanged(List<VediGraph> vediGraphs) {
                adapter.setVediGraphs(vediGraphs);
                //NavHostFragment.findNavController(); https://developer.android.com/guide/navigation/navigation-navigate
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

    }

}