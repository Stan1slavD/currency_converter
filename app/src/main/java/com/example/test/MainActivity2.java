package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity2 extends AppCompatActivity {
ListView listView;
    public static ArrayList<Double> oldСurrency=new ArrayList<Double>();

    public static ArrayList<Double>currentСurrency=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        listView=findViewById(R.id.listView);
        loadData();
        listView.setOnTouchListener(new OnSwipeTouchListener(MainActivity2.this) {
            public void onSwipeTop() {
            }
            public void onSwipeRight() {
                Toast.makeText(MainActivity2.this, "Wait...", Toast.LENGTH_SHORT).show();
                changeActivity();

            }
            public void onSwipeLeft() {

            }
            public void onSwipeBottom() {

            }

        });
    }

    private void changeActivity() {
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    public void loadData(){
        Date date=new Date(System.currentTimeMillis()-24*60*60*1000);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        new MainActivity2.GetURLData().execute("http://api.currencylayer.com/historical?access_key=5311b83c01c72b334c620bfce7f18a06&date="+dateFormat.format(date));
        try {
            java.util.concurrent.TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new MainActivity2.GetURLData().execute("http://api.currencylayer.com/live?access_key=5311b83c01c72b334c620bfce7f18a06");

    }





    private class GetURLData extends AsyncTask<String,String,String>{

        protected void onPreExecute(){
            super.onPreExecute();

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
if(jsonObject.toString().contains("date")){
                        FileOutputStream fileOutputStream=openFileOutput("y.txt",MODE_PRIVATE);
    String[] arr = jsonObject.getJSONObject("quotes").toString().split(",");



                        for (int y = 0; y<arr.length;y++) {
                        String s = arr[y].replaceAll("[USD,{}\"]", "");
                        s=s.replaceAll("[:]"," ");
                        double dd=Double.valueOf(s.substring(s.indexOf(" ")));
                            oldСurrency.add(dd);
                            String str1 =String.valueOf(dd)+" ";
                        fileOutputStream.write(str1.getBytes());
                        }
                        fileOutputStream.close();
                        FileInputStream fileInputStream=openFileInput("y.txt");
                        InputStreamReader inputStreamReader =new InputStreamReader(fileInputStream);
                        BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
                        StringBuffer stringBuffer=new StringBuffer();
                        String line="";
                        while((line=bufferedReader.readLine())!=null ){
                            stringBuffer.append(line);
                        }
                    }
                    else{
                            FileInputStream fileInputStream =openFileInput("y.txt");
                            InputStreamReader inputStreamReader =new InputStreamReader(fileInputStream);
                            BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
                            StringBuffer stringBuffer=new StringBuffer();
                            String line="";
                            while((line=bufferedReader.readLine())!=null ){
                        stringBuffer.append(line);
                        }

                            String endstr=stringBuffer.toString();
                            String[]  finalArray=endstr.split(" ");
                            for (int i=0;i< finalArray.length;i++){
                                oldСurrency.add(Double.valueOf( finalArray[i]));
                            }
                            String[] arr =  jsonObject.getString("quotes").split(",");
                            for (int y = 0; y < arr.length; y++) {
                                  String s = arr[y].replaceAll("[USD,{}\"]", "");
                                  s = s.replaceAll("[:]", " ");
                                  double dd = Double.valueOf(s.substring(s.indexOf(" ")));
                                  currentСurrency.add(dd);
                            }

                            for (int y = 0; y<arr.length;y++) {
                             arr[y] = arr[y].replaceAll("[,{}\"]", "");
                             arr[y]=arr[y].replaceAll("[:]"," ");
                             arr[y]=arr[y].replaceAll("USD"," ");

                            }

                            for (int i = 0; i < arr.length; i++) {
                                if (currentСurrency.get(i) >= oldСurrency.get(i)) {
                                    double value=(currentСurrency.get(i)-oldСurrency.get(i))/oldСurrency.get(i)*100;
                                     finalArray[i] = arr[i]+"        "+"\n+"+String.format("%.3f",value)+"%";
                                 } else {
                                        double value =(oldСurrency.get(i)-currentСurrency.get(i))/oldСurrency.get(i)*100;
                                         finalArray[i] = arr[i]+"        "+"\n-"+String.format("%.3f",value)+"%";
                                         }
                            }



                            ArrayAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,  finalArray);
                            listView.setAdapter(adapter);

                    }

                } catch (JSONException | FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(MainActivity2.this, "Ошибка", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
