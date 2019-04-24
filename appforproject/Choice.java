package com.example.appforproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class Choice extends AppCompatActivity {
    private String TABLENAME;
    Button chartBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);
        chartBtn = findViewById(R.id.chartBtn);
        if(getIntent() !=null) {
            Bundle uData = getIntent().getExtras();
            TABLENAME = uData.getString("Name");
        }
        System.out.println(TABLENAME);
        if(TABLENAME.contains("feildrun_notdetected")){
            chartBtn.setVisibility(View.INVISIBLE);
        }
    }

    public void photoView(View view) {
        Intent intent = new Intent(this, photoViewer.class);
        Bundle uData = new Bundle();
        uData.putString("Name", TABLENAME);
        intent.putExtras(uData);
        Log.d("asa", "2");
        startActivity(intent);
    }

    public void chartView(View view) {
        Intent intent = new Intent(this, PiChart.class);
        Bundle uData = new Bundle();
        uData.putString("Name", TABLENAME);
        intent.putExtras(uData);
        Log.d("asa", "2");
        startActivity(intent);
    }
}
