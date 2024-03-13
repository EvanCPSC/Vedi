package com.example.vedi.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.vedi.MainActivity;
import com.example.vedi.R;
import com.example.vedi.VediGraph;
import com.example.vedi.VediGraphDB;
import com.example.vedi.databinding.FragmentGalleryBinding;
import com.example.vedi.ui.home.HomeViewModel;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.List;
import java.util.Locale;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(requireActivity()).get(GalleryViewModel.class);
        HomeViewModel homeViewModel =
                new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        String m;
        if (MainActivity.pref.getBoolean(getString(R.string.convert_key), false)) {
            m = "km";
        }
        else {
            m = "mi";
        }
        List<VediGraph> db = homeViewModel.getGraphs().getValue();
        if (db.size() > 0) {
            VediGraph dbLast = db.get(db.size()-1);
            LineGraphSeries<DataPoint> series = dbLast.getvSeries();
            binding.graphV.addSeries(series);
            series = dbLast.getaSeries();
            binding.graphA.addSeries(series);
            binding.txtD.setText("Distance: " + String.format(Locale.US, "%.2f", dbLast.getDistance()) + m);
        } else {
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
            series.appendData(new DataPoint(.2, 0), true, 1);
            binding.graphV.addSeries(series);
            series.appendData(new DataPoint(.2, 0), true, 1);
            binding.graphA.addSeries(series);
            binding.txtD.setText("Distance: 0");
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}