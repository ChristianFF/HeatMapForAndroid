package com.ff.heatmap;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import com.ff.heatmap.heatmap.HeatMap;
import com.ff.heatmap.heatmap.HeatMapOverlay;
import com.ff.heatmap.heatmap.WeightedLatLng;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private int screenWidth;
    private int screenHeight;
    private ImageView imageView;
    private HeatMap heatMap;
    private HeatMapOverlay heatMapOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        imageView = (ImageView) findViewById(R.id.image);
        FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("HeatMap");

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "regenerate heatmap data", Snackbar.LENGTH_LONG)
                        .setAction("ok", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                List<WeightedLatLng> data = generateHeatMapData();
                                heatMap.setWeightedData(data);
                                imageView.setImageBitmap(heatMap.generateMap());
                            }
                        }).show();
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Regenerate HeatMapOverlay Data", Snackbar.LENGTH_LONG)
                        .setAction("ok", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                List<WeightedLatLng> data = generateHeatMapData();
                                heatMapOverlay.setWeightedData(data);
                                imageView.setImageBitmap(heatMapOverlay.generateMap());
                            }
                        }).show();
            }
        });


        measureScreen();
        List<WeightedLatLng> data = generateHeatMapData();
        heatMap = new HeatMap.Builder().weightedData(data).radius(35).width(screenWidth).height(screenHeight).build();
        heatMapOverlay = new HeatMapOverlay.Builder().weightedData(data).radius(35).width(screenWidth).height(screenHeight).build();
        imageView.setImageBitmap(heatMap.generateMap());
    }

    private void measureScreen() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
    }

    @NonNull
    private List<WeightedLatLng> generateHeatMapData() {
        List<WeightedLatLng> data = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            data.add(new WeightedLatLng((int) (Math.random() * screenWidth),
                    (int) (Math.random() * screenHeight),
                    Math.random() * 100));
        }
        return data;
    }
}
