package com.example.apptotomcat;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;

public class PiChart extends AppCompatActivity {
    private static String TAG = "PiActivity";
    private float[] valueSet = {25.3f, 10.6f, 66.76f, 44.32f, 46.01f};
    private String[] nameSet = {"DockLeaf", "Nettle" , "SunSpurge" , "ButterCup", "Chickweed"};
    PieChart pieChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pi_chart);
        Log.d(TAG,"on create pi chart");

        pieChart = (PieChart)findViewById(R.id.pieChart);
        pieChart.setRotationEnabled(true);
        pieChart.setHoleRadius(15f);
        pieChart.setCenterText("Weeds Detected");
        pieChart.setCenterTextSize(5);

        addDataSet(pieChart);

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Log.d(TAG, "Value selected");

            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    private void addDataSet(PieChart piChart) {
        Log.d(TAG, "adding data set");
        ArrayList<PieEntry> values= new ArrayList<>();
        ArrayList<String> names= new ArrayList<>();

        for(int i = 0; i < valueSet.length; i++){
            values.add(new PieEntry(valueSet[i] , i));
        }

        for(int i = 1; i < nameSet.length; i++){
            names.add(nameSet[i]);
        }

        PieDataSet pieDataSet = new PieDataSet(values, "Detected Weeds");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);

        //add colors to dataset
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.GRAY);
        colors.add(Color.BLUE);
        colors.add(Color.RED);
        colors.add(Color.GREEN);
        colors.add(Color.CYAN);
        colors.add(Color.YELLOW);
        colors.add(Color.MAGENTA);

        pieDataSet.setColors(colors);

        //add legend to chart
        Legend legend = piChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);

        //create pie data object
        PieData pieData = new PieData(pieDataSet);
        piChart.setData(pieData);
        piChart.invalidate();

    }
}
