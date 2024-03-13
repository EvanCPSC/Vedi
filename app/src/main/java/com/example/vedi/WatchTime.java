package com.example.vedi;

public class WatchTime {
    private long startTime;
    private long timeUpdate;
    private long storeTime;

    public WatchTime() {
        startTime = 0L;
        timeUpdate = 0L;
        storeTime = 0L;
    }
    public WatchTime(long updateTime){
        startTime = 0L;
        timeUpdate = 0L;
        storeTime = updateTime;
    }
    public void resetTime(){
        startTime = 0L;
        timeUpdate = 0L;
        storeTime = 0L;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getTimeUpdate() {
        return timeUpdate;
    }

    public void setTimeUpdate(long timeUpdate) {
        this.timeUpdate = timeUpdate;
    }

    public long getStoreTime() {
        return storeTime;
    }

    public void setStoreTime(long storeTime) {
        this.storeTime = storeTime;
    }
    public void addStoredTime(long milliseconds){
        storeTime += milliseconds;
    }
    public boolean isTimeRunning(){
        if(storeTime > 0)return true;
        else{ return false;}
    }
}
