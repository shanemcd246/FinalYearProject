package com.example.appforproject;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.appforproject.MainActivity.ipAddress;


public class PiChart extends AppCompatActivity {
    private static String TAG = "PiChart";
    private static String TABLENAME;
    PieChart pieChart;
    TextView totalWeedstxt;
    WeedData d = new WeedData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pi_chart);
        if(getIntent() !=null) {
            Bundle uData = getIntent().getExtras();
            TABLENAME = uData.getString("Name");
        }
        pieChart = (PieChart)findViewById(R.id.pieChart);
        pieChart.setRotationEnabled(true);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setHoleRadius(5);
        totalWeedstxt = findViewById(R.id.totalWeedstxt);
        new getInfo().execute();

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int pos1 = e.toString().indexOf("y: ");
                String amount = e.toString().substring(pos1 + 3);

                for(int i = 0; i < d.getCount().size(); i++){
                    if(d.getCount().get(i) == Float.parseFloat(amount)){
                        pos1 = i;
                        break;
                    }
                }
                String weed = d.getNames().get(pos1);
                Toast.makeText(PiChart.this,  weed + "\n" + "Amount: " + amount , Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    private class getInfo extends AsyncTask<String, String, WeedData> {

        @Override
        protected void onPostExecute(final WeedData s) {
            super.onPostExecute(s);
            addDataSet(pieChart,s.getNames(),s.getCount());
            totalWeedstxt.setText("Total Number of Weeds: "+s.getMax());
        }

        @Override
        protected WeedData doInBackground(String... strings) {
            URL link = null;
            HttpURLConnection urlconnection = null;
            ArrayList<String> message = null;
            try {
                link = new URL("http://"+ipAddress+":8080/web_war_exploded/feildStats");
                urlconnection = (HttpURLConnection) link.openConnection();
                urlconnection.setDoOutput(true);
                urlconnection.setDoInput(true);
                urlconnection.setUseCaches(false);
                urlconnection.setDefaultUseCaches(false);
                urlconnection.setRequestProperty("Content-Type", "application/octet-stream");
                ObjectOutputStream oos = null;
                oos = new ObjectOutputStream(urlconnection.getOutputStream());
                List<? extends Serializable> messages = Arrays.asList(TABLENAME);
                oos.writeObject(messages);
                ObjectInputStream ois = null;
                ois = new ObjectInputStream(urlconnection.getInputStream());
                Log.d(TAG, "Getting List");
                d = new WeedData();
                d = (WeedData) ois.readObject();
                oos.close();
                ois.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return d;
        }
    }
    private void addDataSet(PieChart piChart,ArrayList<String> name,ArrayList<Float> counter) {
        Log.d(TAG, "adding data set");
        ArrayList<PieEntry> values= new ArrayList<>();
        ArrayList<String> names= new ArrayList<>();

        for(int i = 0; i < counter.size(); i++){
            values.add(new PieEntry(counter.get(i), i));
        }

        for(int i = 1; i < name.size(); i++){
            names.add(name.get(i));
        }

        PieDataSet pieDataSet = new PieDataSet(values, "Detected Weeds");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.RED);
        colors.add(Color.BLUE);
        colors.add(Color.GREEN);
        colors.add(Color.CYAN);
        colors.add(Color.YELLOW);
        colors.add(Color.MAGENTA);
        colors.add(Color.GRAY);
        colors.add(Color.BLACK);
        pieDataSet.setColors(colors);
        PieData pieData = new PieData(pieDataSet);
        piChart.setData(pieData);
        piChart.invalidate();

    }

}
