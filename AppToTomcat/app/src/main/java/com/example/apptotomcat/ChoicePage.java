package com.example.apptotomcat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ChoicePage extends AppCompatActivity {
    ArrayList<String> dbList = new ArrayList<String>();
    private static String TAG = "ChoicePageAct";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_page);
        Log.d(TAG, "Creating");
        Log.d(TAG, "Exacuting login");
        new Login().execute();
    }
    private class Login extends AsyncTask<String,String, String>{

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "running do in background");
            URL link = null;
            HttpURLConnection urlconnection = null;
            try {
                link = new URL("http://192.168.0.185:8080/web_war_exploded/listDatabase");
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
    public void getDatabases(View view) throws IOException, ClassNotFoundException {
        if(dbList.size()<=1){
            Context context = getApplicationContext();
            Toast toast = Toast.makeText(context, "Gathering Data Base Information", Toast.LENGTH_SHORT);
            toast.show();
        }
        else {
            Intent intent = new Intent(this, DataBaseList.class);
            Bundle uData = new Bundle();
            uData.putStringArrayList("dbList",dbList);
            intent.putExtras(uData);
            Log.d("asa","2");
            startActivity(intent);
        }
    }
}
