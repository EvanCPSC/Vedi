package com.example.vedi.ui.slideshow;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.example.vedi.VediGraph;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SlideshowViewModel extends AndroidViewModel {

    private static final String PREFS = "shared_prefs";
    private static final String SAVED_GRAPHS = "saved_graphs";
    private MutableLiveData<List<VediGraph>> graphs;
    private SavedStateHandle savedStateHandle;
    private Application app;

    public SlideshowViewModel(Application app, SavedStateHandle stateHandle) {
        super(app);
        this.app = app;
        savedStateHandle = stateHandle;
        this.graphs = new MutableLiveData<>();
        savedStateHandle.set("graphs", graphs.getValue());
    }



    public MutableLiveData<List<VediGraph>> getVediGraphs() {
        return savedStateHandle.getLiveData("graphs", new ArrayList<>());
    }

    public void addVediGraph(VediGraph event) {
        List<VediGraph> eventList = getVediGraphs().getValue();
        eventList.add(event);
        graphs.setValue(eventList);
        savedStateHandle.set("graphs", eventList);

    }

    public void setValue(List<VediGraph> loadedGraphs) {
        graphs.setValue(loadedGraphs);
    }
}