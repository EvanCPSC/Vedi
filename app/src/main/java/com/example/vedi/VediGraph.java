package com.example.vedi;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;


public class VediGraph implements Parcelable {
    private List<Double> vData;
    private List<Double> aData;


    public void setDistance(Double distance) {
        this.distance = distance;
    }

    private Double distance;
    private int delay;
    private boolean unit;
    private boolean convertableUnit;

    public VediGraph(List<Double> v, List<Double> a, double d, int del, boolean u) {
        delay = del;
        //dataSize = v.size();
        vData = new ArrayList<>(v);
        aData = new ArrayList<>(a);
        distance = d;
        unit = u;
        convertableUnit = u;
    }
    public VediGraph(){
    }

    protected VediGraph(Parcel in) {
        if (in.readByte() == 0) {
            distance = null;
        } else {
            distance = in.readDouble();
        }
        delay = in.readInt();
    }


    public static final Creator<VediGraph> CREATOR = new Creator<VediGraph>() {
        @Override
        public VediGraph createFromParcel(Parcel in) {
            return new VediGraph(in);
        }

        @Override
        public VediGraph[] newArray(int size) {
            return new VediGraph[size];
        }
    };

    public List<Double> getvData() {
        return vData;
    }

    public List<Double> getaData() {
        return aData;
    }

    public LineGraphSeries<DataPoint> getvSeries() {
        LineGraphSeries<DataPoint> vSeries = new LineGraphSeries<>();
        for (int i = 0; i < vData.size(); i++) {
            vSeries.appendData(new DataPoint(delay * 0.001 * i, vData.get(i)), true, vData.size());
        }
        return vSeries;
    }

    public LineGraphSeries<DataPoint> getaSeries() {
        LineGraphSeries<DataPoint> aSeries = new LineGraphSeries<>();
        for (int i = 0; i < aData.size(); i++) {
            aSeries.appendData(new DataPoint(delay * 0.001 * (i+1), aData.get(i)), true, aData.size());
        }
        return aSeries;
    }

    public double getDistance() {
        return distance;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void setvData(List<Double> vData) {
        this.vData = vData;
    }

    public void setaData(List<Double> aData) {
        this.aData = aData;
    }


    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {

        if (distance == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(distance);
        }
        dest.writeInt(delay);
    }

    public boolean getConvertableUnit() {
        return this.unit;
    }
    public void setConvertableUnit(boolean u) {
        this.unit = u;
    }
}
