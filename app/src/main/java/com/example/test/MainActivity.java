package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private EditText fromNumber;
    private Spinner fromSpinner;
    private Spinner toSpinner;
    private Button convertBtn;
    private TextView result;
    private RelativeLayout background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fromNumber = findViewById(R.id.fromNumber);
        fromSpinner = findViewById(R.id.fromSpinner);
        toSpinner = findViewById(R.id.toSpinner);
        convertBtn = findViewById(R.id.button);
        result = findViewById(R.id.result);
        background=findViewById(R.id.background);
        convertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fromNumber.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, R.string.toast_msg, Toast.LENGTH_SHORT).show();
                } else {

                        convert(Integer.valueOf(fromNumber.getText().toString()),fromSpinner.getSelectedItem().toString(),toSpinner.getSelectedItem().toString());
                }

            }
        });

        background.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            public void onSwipeTop() {
            }
            public void onSwipeRight() {
            }
            public void onSwipeLeft() {
                Toast.makeText(MainActivity.this, "Wait...", Toast.LENGTH_SHORT).show();
                changeActivity();
            }
            public void onSwipeBottom() {
            }

        });

    }

    public void changeActivity(){
        Intent intent=new Intent(this,MainActivity2.class);
        startActivity(intent);
    }
public void convert(int number, String from,String to){
    new GetURLData(number,from,to).execute("http://api.currencylayer.com/live?access_key=6a4ffa146e1739ebcdf678a455fc1588");
}

    private class GetURLData extends AsyncTask<String,String,String>{
        int number;
        String from;
        String to;
private GetURLData(int number,String from,String to){
    this.number=number;
    this.from=from;
    this.to=to;
}
        protected void onPreExecute(){
        super.onPreExecute();
        result.setText("Wait...");
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection httpURLConnection=null;
            BufferedReader bufferedReader=null;
            try {
                URL url=new URL(strings[0]);
                httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.connect();

                InputStream inputStream=httpURLConnection.getInputStream();
                bufferedReader=new BufferedReader(new InputStreamReader(inputStream));

                StringBuffer stringBuffer=new StringBuffer();
                String line="";
                while ((line=bufferedReader.readLine())!=null)
                    stringBuffer.append(line).append("\n");

                return  stringBuffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if(httpURLConnection!=null)
                    httpURLConnection.disconnect();
                try {
                if(bufferedReader!=null)
                    bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            return null;
            }


        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String str){
            super.onPostExecute(str);

            if(str!=null){
                try {
                JSONObject jsonObject=new JSONObject(str);
                System.out.println(str);
                if (from==to){
                    result.setText(Integer.toString(number));
                }
                else {
                    result.setText(number+" "+from+" ="+String.format("%.3f",Double.valueOf(number)*jsonObject.getJSONObject("quotes").getDouble("USD" + to)/jsonObject.getJSONObject("quotes").getDouble("USD" + from))+" "+to);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            }else{
                Toast.makeText(MainActivity.this, "Ошибка", Toast.LENGTH_SHORT).show();
            }
        }
    }
}