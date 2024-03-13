package com.example.vedi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.WindowManager;
import android.widget.TextView;
import android.view.View;

import com.example.vedi.ui.slideshow.SlideshowViewModel;
import com.example.vedi.ui.home.HomeFragment;
import com.google.android.material.navigation.NavigationView;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.example.vedi.databinding.ActivityMainBinding;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity{

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    public static VediGraphDB db;
    public static SharedPreferences pref;

    private static final String PREFS = "shared_prefs";
    private static final String SAVED_GRAPHS = "saved_graphs";
    SlideshowViewModel slideshowViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);

        pref = PreferenceManager.getDefaultSharedPreferences(this);

        slideshowViewModel.setValue(loadVediGraphs());
        db = new VediGraphDB();

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_settings)
                .setOpenableLayout(drawer)
                .build();
        NavHostFragment navFrag = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        NavController navController = navFrag.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


    }

    private void saveGraphs() {
        SharedPreferences pref = getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        /*Log.d("Values", Arrays.toString(mValues.get(0).getaData().toArray()));*/
        String eventJSON = gson.toJson(slideshowViewModel.getVediGraphs());
        pref.edit().putString(SAVED_GRAPHS, eventJSON).commit();
    }

    public List<VediGraph> loadVediGraphs() {
        SharedPreferences pref = getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String graphsJSON = pref.getString(SAVED_GRAPHS, "");
        Type type = new TypeToken<ArrayList<VediGraph>>(){}.getType();
        ArrayList<VediGraph> graphs = gson.fromJson(graphsJSON, type);
        if(graphsJSON.equals("")) return new ArrayList<>();
        else return graphs;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return false;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveGraphs();
    }
}