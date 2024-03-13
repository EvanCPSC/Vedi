package com.example.vedi.ui.home;

import static android.content.Context.SENSOR_SERVICE;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

import com.example.vedi.Acceleration;

import com.example.vedi.Acceleration;
import com.example.vedi.MainActivity;
import com.example.vedi.R;
import com.example.vedi.VediGraph;
import com.example.vedi.VediGraphDB;
import com.example.vedi.WatchTime;
import com.example.vedi.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class HomeFragment extends Fragment {

    private static int MS_DELAY_NUM_VEL;
    private WatchTime watchTime;

    private volatile long timeInMilli = 0L;

    private boolean stopped = true;
    private double temp = 0;
    private int count = 0;
    private boolean unit;

    //millisecond delay number ()
    public final int MS_DELAY_NUM = 1;

    //to display the velocity
    private double velocityRiemannSum, distanceRiemannSum, averageAccel;
    private int velocityRiemannSumInteger, distanceRiemannSumInteger, tempMax;
    private String velocityRiemannSumString;
    ScheduledFuture<?> repeatedFuture, velocityFuture;

    ScheduledExecutorService scheduledExecutorService;
    private FragmentHomeBinding binding;

    //Sensor Managers
    private SensorManager sensorManager;
    private SensorEventListener sensorEventListener;
    private List<Double> vdata = new ArrayList<>(), adata = new ArrayList<>(), tempdata = new ArrayList<>();
    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        scheduledExecutorService = Executors.newScheduledThreadPool(2);

        sensorManager = (SensorManager) requireActivity().getSystemService(SENSOR_SERVICE);
        sensorEventListener = new Acceleration.ShakeListener();
        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.txtTime;
        final TextView textView1 = binding.txtSpeed2;
        homeViewModel.getTime().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                textView.setText(s);
            }
        });
        homeViewModel.getVelRiemannSumInt().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                velocityRiemannSumString = integer + " mph";
                textView1.setText(velocityRiemannSumString);
                Log.d("TAG", "Velocity Observed");
            }
        });
        binding.btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStop();
            }
        });


        //homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        binding.btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });
        binding.btnRestart.setEnabled(false);
        //watchTime = new WatchTime();
        String string = homeViewModel.getStoredTime().getValue().toString();
        watchTime = new WatchTime(homeViewModel.getStoredTime().getValue());
        return root;
    }

    private void startStop() {
        MS_DELAY_NUM_VEL = MainActivity.pref.getInt(getString(R.string.increment_key), 200);
        Log.d("2", "startStop: first");
        if (stopped) {
            lockDeviceRotation(true);//disables rotation
            long delay = 0L;
            stopped = false;
            binding.btnStart.setText(R.string.stop_);
            binding.btnRestart.setEnabled(false);
            watchTime.setStartTime(SystemClock.uptimeMillis() + delay);
            repeatedFuture = scheduledExecutorService.scheduleWithFixedDelay(updateTimerRunnable,delay, MS_DELAY_NUM,TimeUnit.MILLISECONDS);
            velocityFuture = scheduledExecutorService.scheduleWithFixedDelay(velocityTimer,delay, MS_DELAY_NUM * MS_DELAY_NUM_VEL, TimeUnit.MILLISECONDS);
            //repeatedFuture = Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(updateTimerRunnable, delay, MS_DELAY_NUM, TimeUnit.MILLISECONDS);

            //velocityFuture = Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(velocityTimer, delay, MS_DELAY_NUM * MS_DELAY_NUM_VEL, TimeUnit.MILLISECONDS);
            Log.d("TAG", "startStop");
            velocityRiemannSum = 0;
            velocityRiemannSumInteger = 0;
            distanceRiemannSum = 0;
            distanceRiemannSumInteger = 0;//TODO FIX

        } else {
            stopped = true;
            binding.btnStart.setText(R.string.start_);
            binding.btnRestart.setEnabled(true);
            watchTime.addStoredTime(timeInMilli);
            repeatedFuture.cancel(true);
            velocityFuture.cancel(true);
            lockDeviceRotation(false); //enables rotation
        }
    }

    private void reset() {
        if (timeInMilli >= MS_DELAY_NUM_VEL) {
            VediGraph v = new VediGraph(vdata, adata, distanceRiemannSum, MS_DELAY_NUM_VEL, unit);
            homeViewModel.addGraph(v);

            //MainActivity.db.add(v);
            vdata.clear();
            adata.clear();
        }

        count = 0;
        watchTime.resetTime();
        timeInMilli = 0L;

        binding.txtTime.setText((R.string._00_00_00));
        homeViewModel.setTimeInMilli(0L);
        homeViewModel.setTime("00 : 00 : 00");
        homeViewModel.setStoredTime(0L);
        binding.txtSpeed2.setText(R.string.zeroxmph);
        velocityRiemannSum = 0;

        binding.txtMaxVel.setText(R.string.max_vel);
        velocityRiemannSumInteger = 0;
        tempMax = 0;

        binding.txtDistance.setText(R.string.total_distance);
        distanceRiemannSumInteger = 0;
        distanceRiemannSum = 0;


    }

    public Runnable updateTimerRunnable = new Runnable() {
        @Override
        public void run() {

            Acceleration.ShakeListener shakeListener = new Acceleration.ShakeListener();


            timeInMilli = SystemClock.uptimeMillis() - watchTime.getStartTime();
            //homeViewModel.setTimeInMilli(0L);
            //homeViewModel.setTimeInMilli(timeInMilli);
            watchTime.setTimeUpdate(watchTime.getStoreTime() + timeInMilli);
            int time = (int) watchTime.getTimeUpdate() / 1000;
            int minutes = time / 60;
            int seconds = time % 60;
            int hundredths = (int) (watchTime.getTimeUpdate() % 1000) / 10;



            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

//                    String velocityRiemannSumString = velocityRiemannSumInteger + " mph"; //making a string for the speedometer
                    //binding.txtSpeed2.setText(velocityRiemannSumString); //setting the text to that string every 200 milliseconds

                    String tempMaxString = "Max Velocity: " + tempMax + " mph"; //making a string for the max velocity at a time
                    binding.txtMaxVel.setText(tempMaxString); //setting the text to that max velocity

                    String tempDistanceString = "Total Distance: " + distanceRiemannSumInteger + " miles"; //making a string for the total distance
                    binding.txtDistance.setText(tempDistanceString); //setting the text to that total distance
                    homeViewModel.setTime(String.format(Locale.US, "%02d : %02d : %02d", minutes, seconds, hundredths));//binding.txtTime.setText(

                           // String.format(Locale.US, "%02d : %02d : %02d", minutes, seconds, hundredths));//changing the timer
                    homeViewModel.setStoredTime(watchTime.getTimeUpdate());
                    homeViewModel.setTimeInMilli(timeInMilli);
                }
            });
        }
    };

    public Runnable velocityTimer = new Runnable() {
        boolean timerOn = false;

        @Override
        public void run() {//off as of 3/28/23

            Acceleration.ShakeListener shakeListener = new Acceleration.ShakeListener();
            //TODO Everytime you rotate suminteger always equals zero
            Log.d("vel", "measuring velocity");
            //first antiderivative
            averageAccel = Acceleration.ShakeListener.getAverageAcc();
            Log.d("vel", String.valueOf(averageAccel));
            velocityRiemannSum = averageAccel * (MS_DELAY_NUM_VEL * 0.001); // meters per second squared into meters per second
            velocityRiemannSum = velocityRiemannSum * 3600; //into meters/hour
            unit = MainActivity.pref.getBoolean(getString(R.string.convert_key), false);
            if (MainActivity.pref.getBoolean(getString(R.string.convert_key), false)) {
                velocityRiemannSum = velocityRiemannSum * .001; //into km/hour
            } else {
                velocityRiemannSum = velocityRiemannSum * 0.000621372; //into miles/hour //original number 0.00062137
            }
            velocityRiemannSumInteger = (int) velocityRiemannSum; //into an integer

            vdata.add(velocityRiemannSum);

            if (vdata.size()>1) {
                count++;
                temp = (vdata.get(count) - vdata.get(count-1)) / (MS_DELAY_NUM_VEL * 0.001);
                adata.add(temp);
            }

            //second antiderivative
            distanceRiemannSum += (velocityRiemannSum * (((double) MS_DELAY_NUM_VEL) / (2000/*miliseconds to seconds*/ * 3600 /*seconds to hours*/))); //turning sum from: mph to m
            distanceRiemannSumInteger = (int) distanceRiemannSum;

            if (!timerOn) {
                timerOn = true;
                tempMax = velocityRiemannSumInteger;
            } else {
                if (velocityRiemannSumInteger > tempMax) {
                    tempMax = velocityRiemannSumInteger;
                }
            }

            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    homeViewModel.setVelRiemannSumInt(velocityRiemannSumInteger);
                }
            });

        }

    };


    @Override
    public void onPause() {
        super.onPause();
        binding = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume(){
        super.onResume();

       // binding.txtTime.setText(homeViewModel.getTime());

    }

    public void lockDeviceRotation(boolean lock) {
        if (lock) {
            requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        } else {
            requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        }
    }
}

