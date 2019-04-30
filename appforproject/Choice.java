package com.example.appforproject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

public class Choice extends AppCompatActivity {
    private static String TAG = "Choice";
    private String TABLENAME;
    Button chartBtn;
    WeedData d = new WeedData();
    View mapView;
    Button mapBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"Choice On Create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);
        chartBtn = findViewById(R.id.chartBtn);
        mapBtn = findViewById(R.id.mapBtn);
        if(getIntent() !=null) {
            Bundle uData = getIntent().getExtras();
            TABLENAME = uData.getString("Name");
        }
        System.out.println(TABLENAME);
        if(TABLENAME.contains("feildrun_notdetected")){
            chartBtn.setVisibility(View.INVISIBLE);
            mapBtn.setVisibility(View.INVISIBLE);
        }
    }

    public void photoView(View view) {
        Intent intent = new Intent(this, photoViewer.class);
        Bundle uData = new Bundle();
        uData.putString("Name", TABLENAME);
        intent.putExtras(uData);
        Log.d("asa", "2");
        startActivity(intent);
        overridePendingTransition(R.anim.slide_to_right,R.anim.slide_out_left);
    }

    public void chartView(View view) {
        Intent intent = new Intent(this, PiChart.class);
        Bundle uData = new Bundle();
        uData.putString("Name", TABLENAME);
        intent.putExtras(uData);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_to_right,R.anim.slide_out_left);
    }

    public void getMap(View view){
        new getGeo().execute();
        mapView = view;
    }
    public void nextActivy(View view){
        if(d.getLat().size()>=1) {
            Intent intent = new Intent(view.getContext(), MapsActivity.class);
            Bundle uData = new Bundle();
            ArrayList<String> lat = new ArrayList<>();
            ArrayList<String> lon = new ArrayList<>();
            for (int x = 0; x < d.getLon().size(); x++) {
                lat.add(String.valueOf(d.getLat().get(x)));
                lon.add(String.valueOf(d.getLon().get(x)));
            }
            uData.putStringArrayList("LON", lon);
            uData.putStringArrayList("LAT", lat);
            uData.putStringArrayList("NAME", d.geoName);
            intent.putExtras(uData);
            startActivity(intent);
        }
        else{
            Context context = getApplicationContext();
            Toast toast = Toast.makeText(context, "No Data Stored to Display", Toast.LENGTH_SHORT);
            toast.show();
        }
    }


    private class getGeo extends AsyncTask<String, String, WeedData> {

        @Override
        protected void onPostExecute(final WeedData s) {
            super.onPostExecute(s);
            nextActivy(mapView);
        }

        @Override
        protected WeedData doInBackground(String... strings) {
            Log.d(TAG,"GetGeo DoInBackground");
            URL link = null;
            HttpURLConnection urlconnection = null;
            ArrayList<String> message = null;
            try {
                link = new URL("http://"+ipAddress+":8080/web_war_exploded/getLonLat");
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
}
