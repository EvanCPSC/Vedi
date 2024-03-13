package com.example.vedi;

import java.util.ArrayList;
import java.util.List;

public class VediGraphDB {
    public List<VediGraph> db;

    public VediGraphDB() {
        this.db = new ArrayList<>();
    }
    public void add(VediGraph v) {
        db.add(v);
    }

}
