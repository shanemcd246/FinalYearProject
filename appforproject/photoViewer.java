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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
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

public class photoViewer extends AppCompatActivity {
    private static String TAG = "PhotoViewer";
    private int currentLoc =0;
    private int maxPicture =0;
    ImageView imageView,imageViewDetected,imageViewDelete;
    TextView nameText,hiddenText;
    Button nextBtn,preBtn,submitBtn,delBtn;
    Spinner dropdown;
    private String TABLENAME = null;
    private ConstraintSet set = new ConstraintSet();
    ArrayList<Integer> currentIDs = new ArrayList<>();
    WeedData d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_photo_viewer);
        imageView = findViewById(R.id.myimage);
        imageViewDetected = findViewById(R.id.imageView2);
        imageViewDelete= findViewById(R.id.imageView3);
        nextBtn = findViewById(R.id.nxtBtn);
        preBtn = findViewById(R.id.preBtn);
        submitBtn = findViewById(R.id.submitBtn);
        delBtn = findViewById(R.id.delBtn);
        nameText = findViewById(R.id.nameText);
        nameText.setBackgroundColor(Color.CYAN);
        dropdown = findViewById(R.id.dropDownMenu);
        hiddenText = findViewById(R.id.hiddenName);
        hiddenText.setPaintFlags(0);
        if(getIntent() !=null) {
            Bundle uData = getIntent().getExtras();
            TABLENAME = uData.getString("Name");
            if(TABLENAME.contains("feildrun_notdetected")){
                String[] items = new String[]{"DockLeaf", "Buttercup", "ForgetMeNot","Fumitory",
                        "Nettle", "Sowthistle", "Spear Thistle", "SunSpurge", "Thistle"};
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
                dropdown.setAdapter(adapter);
                submitBtn.setVisibility(View.VISIBLE);
                dropdown.setVisibility(View.VISIBLE);
                nameText.setVisibility(View.INVISIBLE);
                delBtn.setVisibility(View.VISIBLE);
            }
            new getIds().execute();

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
                if(!TABLENAME.contains("feildrun_notdetected")){
                    nameText.setText(weedData.getName());
                }
                else{
                    if(weedData.getName().length()>1)
                    {
                        hiddenText.setVisibility(View.VISIBLE);
                        hiddenText.setText(d.getName());
                        imageViewDetected.setVisibility(View.VISIBLE);
                    }
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
                link = new URL("http://"+ipAddress+":8080/web_war_exploded/pleaseWork");
                urlconnection = (HttpURLConnection) link.openConnection();
                urlconnection.setDoOutput(true);
                urlconnection.setDoInput(true);
                urlconnection.setUseCaches(false);
                urlconnection.setDefaultUseCaches(false);
                urlconnection.setRequestProperty("Content-Type", "application/octet-stream");
                ObjectOutputStream oos = null;
                oos = new ObjectOutputStream(urlconnection.getOutputStream());
                Integer val;
                val = currentIDs.get(currentLoc);
                List<? extends Serializable> messages = Arrays.asList(TABLENAME,val);
                oos.writeObject(messages);
                ObjectInputStream ois = null;
                ois = new ObjectInputStream(urlconnection.getInputStream());
                Log.d(TAG, "Getting List");
                d = (WeedData) ois.readObject();
                oos.flush();
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
        if(currentLoc < maxPicture-1){
            preBtn.setEnabled(true);
            currentLoc +=1;
            imageViewDetected.setVisibility(View.INVISIBLE);
            hiddenText.setVisibility(View.INVISIBLE);
            new getPhoto().execute();
        }
        else{
            nextBtn.setEnabled(false);
        }

    }
    public void prePic(View view){
        if(currentLoc >= 1){
            nextBtn.setEnabled(true);
            currentLoc -=1;
            imageViewDetected.setVisibility(View.INVISIBLE);
            hiddenText.setVisibility(View.INVISIBLE);
            new getPhoto().execute();
        }
        else{
            preBtn.setEnabled(false);
        }
    }
    public void submitAnswer(View view){
        String text = dropdown.getSelectedItem().toString();
        new submitAnswer().execute(text);
    }

    public void deleteItem(View view){
        imageViewDetected.setVisibility(View.INVISIBLE);
        imageViewDelete.setVisibility(View.VISIBLE);
        hiddenText.setText("DELETED");
        new deleteAnswer().execute();
    }


    private class deleteAnswer extends AsyncTask<String, String, Integer> {
        @Override
        protected void onPostExecute(Integer resultCode) {
            super.onPostExecute(resultCode);
            Context context = getApplicationContext();
            if (resultCode == 1) {
                Toast toast = Toast.makeText(context, "Deleted From Database", Toast.LENGTH_SHORT);
                toast.show();
                imageViewDelete.setVisibility(View.INVISIBLE);
                hiddenText.setVisibility(View.INVISIBLE);
                currentIDs.remove(currentIDs.indexOf(d.getId()));
                maxPicture-=1;
                new getPhoto().execute();

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
                link = new URL("http://"+ipAddress+":8080/web_war_exploded/deleteItem");
                urlconnection = (HttpURLConnection) link.openConnection();
                urlconnection.setDoOutput(true);
                urlconnection.setDoInput(true);
                urlconnection.setUseCaches(false);
                urlconnection.setDefaultUseCaches(false);
                urlconnection.setRequestProperty("Content-Type", "application/octet-stream");
                ObjectOutputStream oos = null;
                oos = new ObjectOutputStream(urlconnection.getOutputStream());
                List<? extends Serializable> dts = Arrays.asList(TABLENAME,d.getId());
                oos.writeObject(dts);
                ObjectInputStream ois = null;
                ois = new ObjectInputStream(urlconnection.getInputStream());
                Log.d(TAG, "Getting List");
                resultCode = (Integer) ois.readObject();
                ois.close();
                oos.flush();
                oos.close();
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

    private class submitAnswer extends AsyncTask<String, String, Integer> {
        @Override
        protected void onPostExecute(Integer resultCode) {
            super.onPostExecute(resultCode);
            Context context = getApplicationContext();
            if (resultCode == 1) {
                Toast toast = Toast.makeText(context, "Added to retain list As "+dropdown.getSelectedItem().toString(), Toast.LENGTH_SHORT);
                imageViewDetected.setVisibility(View.VISIBLE);
                hiddenText.setVisibility(View.VISIBLE);
                hiddenText.setText(dropdown.getSelectedItem().toString());
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
                link = new URL("http://"+ipAddress+":8080/web_war_exploded/submitAnswer");
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
                ois.close();
                oos.flush();
                oos.close();
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

    private class getIds extends AsyncTask<String, String, Integer> {
        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            new getPhoto().execute();
        }

        @Override
        protected Integer doInBackground(String... strings) {
            URL link = null;
            HttpURLConnection urlconnection = null;
            try {
                link = new URL("http://"+ipAddress+":8080/web_war_exploded/getIDs");
                urlconnection = (HttpURLConnection) link.openConnection();
                urlconnection.setDoOutput(true);
                urlconnection.setDoInput(true);
                urlconnection.setUseCaches(false);
                urlconnection.setDefaultUseCaches(false);
                urlconnection.setRequestProperty("Content-Type", "application/octet-stream");
                ObjectOutputStream oos = null;
                oos = new ObjectOutputStream(urlconnection.getOutputStream());
                List<? extends Serializable> dts = Arrays.asList(TABLENAME);
                oos.writeObject(dts);
                ObjectInputStream ois = null;
                ois = new ObjectInputStream(urlconnection.getInputStream());
                WeedData recived = (WeedData) ois.readObject();
                currentIDs =recived.getIdNumbers();
                maxPicture = currentIDs.size();
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
}
