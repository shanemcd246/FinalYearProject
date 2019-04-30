package com.example.appforproject;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static String TAG = "MapsActivity";
    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    boolean isDone = false;
    ArrayList<String> latList = new ArrayList<>();
    ArrayList<String> lonList = new ArrayList<>();
    ArrayList<String> geoname = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"MapsActivity on Create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        if(getIntent() !=null) {
            Bundle uData = getIntent().getExtras();
            latList= uData.getStringArrayList("LON");
            lonList= uData.getStringArrayList("LAT");
            geoname= uData.getStringArrayList("NAME");
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        for(int x =0;x<latList.size();x++){
            System.out.println("in loop");
            double lat = Double.parseDouble(latList.get(x));
            double lon = Double.parseDouble(lonList.get(x));
            mMap.addMarker(new MarkerOptions().position(new LatLng(lat,lon)).title(geoname.get(x)));
        }
        double lat = Double.parseDouble(latList.get(0));
        double lon = Double.parseDouble(lonList.get(0));
        LatLng start = new LatLng(lat, lon);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(start));
    }
}
