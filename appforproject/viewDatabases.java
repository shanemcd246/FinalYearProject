package com.example.appforproject;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class viewDatabases extends AppCompatActivity {
    private static String TAG = "DataBaseListAct";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "On create");
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_view_databases);
        LinearLayout layout = findViewById(R.id.layout);
        ArrayList<String> dbList;
        Bundle uData = getIntent().getExtras();
        dbList = uData.getStringArrayList("dbList");
        Log.d(TAG, "Size of array :" + String.valueOf(dbList.size()));
        for (int x = 0; x < dbList.size(); x++) {
            Log.d(TAG, "In For Loop");
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            Button btn = new Button(this);
            btn.setId(x);
            final int id_ = btn.getId();
            btn.setText(dbList.get(x));
            final String name = dbList.get(x);
            btn.setBackgroundColor(Color.LTGRAY);
            btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(),Choice.class);
                    Bundle uData = new Bundle();
                    uData.putString("Name", name);
                    intent.putExtras(uData);
                    Log.d("asa","2");
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_to_right,R.anim.slide_out_left);
                }
            });
            layout.addView(btn, params);
        }
    }
}
