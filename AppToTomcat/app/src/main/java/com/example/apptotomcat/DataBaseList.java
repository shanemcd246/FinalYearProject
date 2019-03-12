package com.example.apptotomcat;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import java.util.ArrayList;

public class DataBaseList extends AppCompatActivity {
    private static String TAG = "DataBaseListAct";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "On create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_base_list);
        LinearLayout layout = findViewById(R.id.layout);
        ArrayList<String> dbList = new ArrayList<String>();
        Bundle uData = getIntent().getExtras();
        dbList =uData.getStringArrayList("dbList");
        Log.d(TAG, "Size of array :"+String.valueOf(dbList.size()));
        for(int x = 0; x<dbList.size();x++) {
            Log.d(TAG, "In For Loop");
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            Button btn = new Button(this);
            btn.setId(x);
            final int id_ = btn.getId();
            btn.setText(dbList.get(x).toString());
            btn.setBackgroundColor(Color.rgb(7, 80, 90));
            layout.addView(btn,params);
//            Button btn1 = ((Button) findViewById(id_));
//            btn1.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View view) {
//                    Toast.makeText(view.getContext(),
//                            "Button clicked index = " + id_, Toast.LENGTH_SHORT)
//                            .show();
//                }
//            });
        }
    }
}
