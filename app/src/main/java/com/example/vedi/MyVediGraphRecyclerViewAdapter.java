package com.example.vedi;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.vedi.placeholder.PlaceholderContent.PlaceholderItem;
import com.example.vedi.databinding.FragmentItemBinding;
import com.example.vedi.ui.home.HomeViewModel;
import com.example.vedi.ui.slideshow.SlideshowFragment;
import com.google.gson.Gson;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyVediGraphRecyclerViewAdapter extends RecyclerView.Adapter<MyVediGraphRecyclerViewAdapter.ViewHolder> {

    private static final String PREFS = "shared_prefs";
    private static final String SAVED_GRAPHS = "saved_graphs";
    private List<VediGraph> mValues;

    private HomeViewModel homeVM;
    private SlideshowFragment sFrag;
    /*private final LayoutInflater mInflater;*/
    private Context context;


    public MyVediGraphRecyclerViewAdapter(Context context) {
        this.context = context;
        mValues = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    homeVM = new ViewModelProvider((MainActivity)parent.getContext()).get(HomeViewModel.class);
    return new ViewHolder(FragmentItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        /*//List<VediGraph> dbLast = homeVM.getGraphs().getValue();
        if (dbLast.size() > 0) {
            holder.series = dbLast.get(position).getvSeries();
        }*/
        holder.series = holder.mItem.getvSeries();
        holder.mIdView.addSeries(holder.series);
        String m;
        boolean u = holder.mItem.getConvertableUnit();
        if (MainActivity.pref.getBoolean(context.getString(R.string.convert_key), false)) {
            m = "km";
            if (!u) {
                holder.mItem.setDistance(holder.mItem.getDistance() / 0.000621372 * 0.001);
                u = MainActivity.pref.getBoolean(context.getString(R.string.convert_key), false);
                holder.mItem.setConvertableUnit(u);
            }
        }
        else {
            m = "mi";
            if (u) {
                holder.mItem.setDistance(holder.mItem.getDistance() / 0.001 * 0.000621372);
                u = MainActivity.pref.getBoolean(context.getString(R.string.convert_key), false);
                holder.mItem.setConvertableUnit(u);
            }
        }
        String dis = "Distance:\n" + String.format(Locale.US, "%.2f", mValues.get(position).getDistance()) + m;
        String inc = "Increment:\n" + (holder.mItem.getDelay() * 0.001) + "sec";
        holder.mDistance.setText(dis + "\n" + inc);
        //delete button
        holder.mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle(R.string.diaTitle)
                        .setMessage(R.string.diaMessage)
                        .setNegativeButton(R.string.diaCancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {

                            }
                        })
                        .setPositiveButton(R.string.diaConfirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //mValues.remove(holder.mItem);
                                homeVM.remove(holder.mItem);
                                //setVediGraphs(mValues);
                            }
                        }).create().show();

            }
        });
        //dialog layout
        holder.mIdView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                LayoutInflater inflater = LayoutInflater.from(view.getContext());
                View dia = inflater.inflate(R.layout.dialog_graph, null);
                //graphs
                GraphView graphV = dia.findViewById(R.id.graphV);
                graphV.addSeries(holder.series);
                GraphView graphA = dia.findViewById(R.id.graphA);
                graphA.addSeries(holder.mItem.getaSeries());
                //linear layout data
                LinearLayout linData = dia.findViewById(R.id.linData);
                TextView diaTxtDistance = linData.findViewById(R.id.txtDistance);
                diaTxtDistance.setText(dis);
                TextView diaTxtIncrement = linData.findViewById(R.id.txtIncrement);
                diaTxtIncrement.setText(inc);
                //linear layout buttons
                LinearLayout linButton = dia.findViewById(R.id.linButton);
                ImageButton diaBack = linButton.findViewById(R.id.imgBack);
                diaBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                ImageButton diaDelete = linButton.findViewById(R.id.imgDelete);
                diaDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(context)
                                .setTitle(R.string.diaTitle)
                                .setMessage(R.string.diaMessage)
                                .setNegativeButton(R.string.diaCancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {

                                    }
                                })
                                .setPositiveButton(R.string.diaConfirm, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //mValues.remove(holder.mItem);
                                        homeVM.remove(holder.mItem);
                                        //setVediGraphs(mValues);
                                    }
                                }).create().show();
                    }
                });


                builder.setView(dia);

                builder.create();
                builder.show();
            }
        });
    }

    public void setVediGraphs(List<VediGraph> graphs) {
        this.mValues = graphs;
        notifyDataSetChanged();
        saveGraphs();
    }

    private void saveGraphs() {
        SharedPreferences pref = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        /*Log.d("Values", Arrays.toString(mValues.get(0).getaData().toArray()));*/
        String eventJSON = gson.toJson(mValues);
        pref.edit().putString(SAVED_GRAPHS, eventJSON).commit();
    }


    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = ((MainActivity) context).getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final GraphView mIdView;
        public final TextView mDistance;
        public VediGraph mItem;

        public ImageView mDelete;
        public LineGraphSeries<DataPoint> series;

        public ViewHolder(FragmentItemBinding binding) {
          super(binding.getRoot());
          mIdView = binding.graphV;
          mDistance = binding.content;
          mDelete = binding.delete;
          series = new LineGraphSeries<>();
        }



        @Override
        public String toString() {
            return super.toString() + " '" + mDistance.getText() + "'";
        }
    }

}