package com.example.sid.testu;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

//    def variables
    CustomAdapter adapter;
    List<String> feelingList = new ArrayList();
    List<String> preferredList = new ArrayList();
    List<String> timeList = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        creating a custom adapter as default adapter wouldn't work here
        adapter = new CustomAdapter(MainActivity.this, feelingList , preferredList , timeList);
        ListView lv = (ListView) findViewById(R.id.list_history);
        lv.setAdapter(adapter);
//        get the history of user
        new getUserHistory().execute();


    }




    public static JSONArray getJSONObjectFromURL(String urlString) throws IOException, JSONException {

        HttpURLConnection urlConnection = null;

        URL url = new URL(urlString);

        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setReadTimeout(10000 /* milliseconds */);
        urlConnection.setConnectTimeout(15000 /* milliseconds */);

        urlConnection.setDoOutput(true);

        urlConnection.connect();

        BufferedReader br=new BufferedReader(new InputStreamReader(url.openStream()));

        char[] buffer = new char[1024];

        String jsonString = new String();

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line+"\n");
        }
        br.close();

        jsonString = sb.toString();

        System.out.println("JSON: " + jsonString);

        return new JSONArray(jsonString);
    }


    public class getUserHistory extends AsyncTask<String,String,String> {


        ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //            show the progress dialog
            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Sending Data to server");
            pd.show();
            //Toast.makeText(getActivity(),"exec" ,Toast.LENGTH_SHORT).show();
        }




        @Override
        protected String doInBackground(String... params) {

            SharedPreferences prefs = getSharedPreferences("preferencename", 0);

            String name = prefs.getString("name", "error");

            String ip = "http://10.129.23.30:8000/historyData/"+name;
            try{
                JSONArray jsonArray = getJSONObjectFromURL(ip);
                return  jsonArray.toString();


                // Parse your json here
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return "Download failed";

        }

        @Override
        protected void onPostExecute(String response) {

            if(pd.isShowing())
                pd.dismiss();
            try {

                JSONArray root = new JSONArray(response);

                for(int i=0; i<root.length();i++){
//                    the custom adapter needs 3 list to show the history, 1: feeling, 2: preferred , 3:TimeStamp
                    feelingList.add(root.getJSONObject(i).getString("feeling"));
                    preferredList.add(root.getJSONObject(i).getString("preferred"));
                    timeList.add(root.getJSONObject(i).getString("TS"));



                }

//                notify the adapter that dataset has changed
                adapter.notifyDataSetChanged();


                //Toast.makeText(getActivity(),root.toString() , Toast.LENGTH_SHORT).show();
            }
            catch (JSONException e){




            }

            if(response.equals("Download failed")){

            }else{


            }


        }
    }





}
