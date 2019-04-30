package com.example.appforproject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.example.appforproject.MainActivity.ipAddress;

public class Retrain extends AppCompatActivity {
    private static String TAG = "Retrain";
    Button yesBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"Retrain on create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrain);
        yesBtn =findViewById(R.id.yesBtn);
    }

    private class RetrainStart extends AsyncTask<String,String, String> {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            yesBtn.setEnabled(false);
        }

        @Override
        protected String doInBackground(String... strings) {
            URL link = null;
            HttpURLConnection urlconnection = null;
            try {
                link = new URL("http://"+ipAddress+":8080/web_war_exploded/retrain");
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
                ois = new ObjectInputStream(urlconnection.getInputStream());
                Integer Result = (Integer) ois.readObject();
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
    public void YesFun(View view){
        new RetrainStart().execute();
    }
    public void NoFun(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
