package com.example.vedi.ui.home;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vedi.VediGraph;
import com.example.vedi.WatchTime;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    private final MutableLiveData<String> time;
    private final MutableLiveData<Long> timeInMilli;
    private final MutableLiveData<Long> storedTime;
    //private volatile long timeInMilli;
    private final MutableLiveData<Integer> velRiemannSumInt;

    private MutableLiveData<List<VediGraph>> graphs = new MutableLiveData<>();
    public HomeViewModel(@NonNull Application application) {
        super(application);
        time = new MutableLiveData<>();
        time.setValue("00 : 00 : 00");
        timeInMilli = new MutableLiveData<>();
        timeInMilli.setValue(0L);
        storedTime = new MutableLiveData<>();
        storedTime.setValue(0L);
        velRiemannSumInt = new MutableLiveData<>();
        velRiemannSumInt.setValue(0);
        graphs.setValue(new ArrayList<>());
    }

    public long getTimeInMilli() {
        return timeInMilli.getValue();
    }

    public void setTimeInMilli(long timeInMilli) {
        this.timeInMilli.setValue(timeInMilli);
    }

    public void addGraph(VediGraph vediGraph){
        List<VediGraph> vediGraphs = graphs.getValue();
        vediGraphs.add(vediGraph);
        graphs.setValue(vediGraphs);

    }
    public void remove(VediGraph vediGraph){
        List<VediGraph> vediGraphs = graphs.getValue();
        vediGraphs.remove(vediGraph);
        graphs.setValue(vediGraphs);
    }
    public MutableLiveData<List<VediGraph>> getGraphs() {
        return graphs;
    }

    public LiveData<Long> getStoredTime(){
        return storedTime;
    }
    public void setStoredTime(Long time){
        storedTime.setValue(time);
    }
    public LiveData<String> getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time.setValue(time);
    }
    public LiveData<Integer> getVelRiemannSumInt() {
        return velRiemannSumInt;
    }

    public void setVelRiemannSumInt(int time) {
        Log.d("TAG", "setVelRiemannSumInt ");
        this.velRiemannSumInt.setValue(time);
    }

}