package com.example.appforproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class photoViewer extends AppCompatActivity {
    private int currentLoc =1;
    private int maxPicture =0;
    private static String TAG = "PhotoViewer";
    ImageView imageView;
    TextView nameText;
    Button nextBtn,preBtn,submitBtn;
    private String TABLENAME = null;
    private ConstraintSet set = new ConstraintSet();
    WeedData d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_photo_viewer);
        imageView = findViewById(R.id.myimage);
        nextBtn = findViewById(R.id.nxtBtn);
        preBtn = findViewById(R.id.preBtn);
        submitBtn = findViewById(R.id.submitBtn);
        nameText = findViewById(R.id.nameText);
        nameText.setBackgroundColor(Color.CYAN);
        if(getIntent() !=null) {
            Bundle uData = getIntent().getExtras();
            TABLENAME = uData.getString("Name");
            new getPhoto().execute();
        }
        if(TABLENAME.contains("feildrun_notdetected")){
            submitBtn.setVisibility(View.VISIBLE);
        }
    }
    private class getPhoto extends AsyncTask<String, String, WeedData> {
        @Override
        protected void onPostExecute(WeedData weedData) {
            super.onPostExecute(weedData);
            try {
                Bitmap bitmap = BitmapFactory.decodeByteArray(weedData.getPhoto(), 0, weedData.getPhoto().length);
                imageView.setImageBitmap(bitmap);
                maxPicture = weedData.getMaxEntery();
                if(TABLENAME.contains("feildrun_notdetected")){
                    nameText.setHint("Enter Your Guess");
                }
                else{
                    nameText.setText(weedData.getName());
                }
            }
            catch (Exception e){
                nameText.setText("NOT GOOD");
            }
        }
        @Override
        protected WeedData doInBackground(String... strings) {
            Log.d(TAG, "running do in background");
            URL link = null;
            HttpURLConnection urlconnection = null;
            try {
                link = new URL("http://192.168.0.100:8080/web_war_exploded/pleaseWork");
                urlconnection = (HttpURLConnection) link.openConnection();
                urlconnection.setDoOutput(true);
                urlconnection.setDoInput(true);
                urlconnection.setUseCaches(false);
                urlconnection.setDefaultUseCaches(false);
                urlconnection.setRequestProperty("Content-Type", "application/octet-stream");
                ObjectOutputStream oos = null;
                oos = new ObjectOutputStream(urlconnection.getOutputStream());
                Integer val = currentLoc;
                List<? extends Serializable> messages = Arrays.asList(TABLENAME,currentLoc);
                oos.writeObject(messages);
                ObjectInputStream ois = null;
                ois = new ObjectInputStream(urlconnection.getInputStream());
                Log.d(TAG, "Getting List");
                d = (WeedData) ois.readObject();
                System.out.println(d.getPhoto());
                //Log.d(TAG, String.valueOf(dbList));
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
    public void nextPic(View view){
        if(currentLoc < maxPicture){
            preBtn.setEnabled(true);
            currentLoc +=1;
            new getPhoto().execute();
        }
        else{
            nextBtn.setEnabled(false);
        }

    }
    public void prePic(View view){
        if(currentLoc > 1){
            nextBtn.setEnabled(true);
            currentLoc -=1;
            new getPhoto().execute();
        }
        else{
            preBtn.setEnabled(false);
        }
    }
    public void submitAnswer(View view){
        if(nameText.getText().length()> 0){
            new submitAnswer().execute(String.valueOf(nameText.getText()));
        }
    }

    private class submitAnswer extends AsyncTask<String, String, Integer> {
        @Override
        protected void onPostExecute(Integer resultCode) {
            super.onPostExecute(resultCode);
            Context context = getApplicationContext();
            if (resultCode == 1) {
                Toast toast = Toast.makeText(context, "Added " + nameText.getText() + " As Plant Name.", Toast.LENGTH_SHORT);
                toast.show();
            }
            else {
                Toast toast = Toast.makeText(context, "Value Not Added", Toast.LENGTH_SHORT);
                toast.show();
            }
        }

        @Override
        protected Integer doInBackground(String... strings) {
            URL link = null;
            HttpURLConnection urlconnection = null;
            Integer resultCode = 0;
            try {
                link = new URL("http://192.168.0.100:8080/web_war_exploded/submitAnswer");
                urlconnection = (HttpURLConnection) link.openConnection();
                urlconnection.setDoOutput(true);
                urlconnection.setDoInput(true);
                urlconnection.setUseCaches(false);
                urlconnection.setDefaultUseCaches(false);
                urlconnection.setRequestProperty("Content-Type", "application/octet-stream");
                ObjectOutputStream oos = null;
                oos = new ObjectOutputStream(urlconnection.getOutputStream());
                d.setName(strings[0]);
                oos.writeObject(d);
                ObjectInputStream ois = null;
                ois = new ObjectInputStream(urlconnection.getInputStream());
                Log.d(TAG, "Getting List");
                resultCode = (Integer) ois.readObject();
                System.out.println(resultCode);
                oos.close();
                ois.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return resultCode;
        }
    }
}
