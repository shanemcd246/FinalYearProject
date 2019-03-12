package com.example.apptotomcat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    Button getBtn;
    TextView uName,uPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void getData(View view) throws IOException, ClassNotFoundException {
        new Login().execute();
    }
    private class Login extends AsyncTask<String,String,String>{
        @Override
        protected String doInBackground(String... strings) {
            URL link = null;
            try {
                link = new URL("http://192.168.0.185:8080/web_war_exploded/pleaseWork");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection urlconnection = null;
            try {
                urlconnection = (HttpURLConnection) link.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }

            urlconnection.setDoOutput(true);
            urlconnection.setDoInput(true);
            urlconnection.setUseCaches(false);
            urlconnection.setDefaultUseCaches(false);
            //System.out.println(c.getPersonID());

            // Specify the content type that we will send binary data
            urlconnection.setRequestProperty("Content-Type", "application/octet-stream");

            ObjectOutputStream oos = null;
            try {
                oos = new ObjectOutputStream(urlconnection.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            Integer val =2;
            try {
                oos.writeObject((Integer)val);  // send the id
            } catch (IOException e) {
                e.printStackTrace();
            }

            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(urlconnection.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Log.d("Reading Back Value","ASFDGZFQ");
                WeedData d = (WeedData) ois.readObject();
                byte[] byteArray =d.getPhoto();
                Bitmap bm = BitmapFactory.decodeByteArray(byteArray, 0 ,byteArray.length);
                //Integer count = (Integer)ois.readObject();  // read back the number of row deleted
                Log.d("backValue",d.getName());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            try {
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "hello";
        }

    }
    public void getCharts(View view) throws IOException, ClassNotFoundException {
        Intent intent = new Intent(this, ChoicePage.class);
        startActivity(intent);
    }
}
