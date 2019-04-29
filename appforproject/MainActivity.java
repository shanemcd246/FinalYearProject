package com.example.appforproject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> dbList = new ArrayList<String>();
    private static String TAG = "ChoicePageAct";
    static final String ipAddress = "10.0.2.2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        System.out.println("BBBBBBBBBBBBBBBBBBBBBB");
        new Login().execute();
    }

    private class Login extends AsyncTask<String,String, String> {
        @Override
        protected String doInBackground(String... strings) {
            System.out.println("GGGGGGGGGGGGGGGGGGGGGgg");
            URL link = null;
            HttpURLConnection urlconnection = null;
            try {
                link = new URL("http://"+ipAddress+":8080/web_war_exploded/listDatabase");
                urlconnection = (HttpURLConnection) link.openConnection();
                urlconnection.setDoOutput(true);
                urlconnection.setDoInput(true);
                urlconnection.setUseCaches(false);
                urlconnection.setDefaultUseCaches(false);
                urlconnection.setRequestProperty("Content-Type", "application/octet-stream");
                ObjectOutputStream oos = null;
                oos = new ObjectOutputStream(urlconnection.getOutputStream());
                Integer val =2;
                oos.writeObject((Integer)val);
                ObjectInputStream ois = null;
                Log.d(TAG, String.valueOf(dbList));
                ois = new ObjectInputStream(urlconnection.getInputStream());
                Log.d(TAG, "Getting List");
                dbList = (ArrayList<String>) ois.readObject();
                Log.d(TAG, String.valueOf(dbList));
                oos.close();
                ois.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void viewDatabases(View view) {
        if (dbList.size() < 1) {
            Context context = getApplicationContext();
            Toast toast = Toast.makeText(context, "Gathering Data Base Information", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            Intent intent = new Intent(this, viewDatabases.class);
            Bundle uData = new Bundle();
            uData.putStringArrayList("dbList", dbList);
            intent.putExtras(uData);
            Log.d("asa", "2");
            startActivity(intent);
            overridePendingTransition(R.anim.slide_to_right,R.anim.slide_out_left);
        }
    }

}
