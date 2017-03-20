package com.example.sid.testu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Home extends AppCompatActivity {


    public boolean isWifiConncted(){

        ConnectivityManager connManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            return true;
        }

        return false;

    }

    public boolean isWifiIITNetwork(){

        WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(this.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();

        if (wifiInfo.getSSID().equals("SEIL")
                ||wifiInfo.getSSID().equals("IITB-Guest")
                ||wifiInfo.getSSID().equals("SEIL-guest")
                ||wifiInfo.getSSID().equals("IITB-Wireless")
                ||wifiInfo.getSSID().equals("eduroam")) {

            return true;
        }

        return false;

    }

//    defining variables
    RadioButton feelingYesRB,feelingNoRB;
    Spinner preferenceSpinner;
    String name,feelingString,preferredString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


//        init variables
        preferenceSpinner = (Spinner)findViewById(R.id.preferred_spinner);
        Button formSubmitBtn = (Button)findViewById(R.id.form_submit_btn);
        Button historyBtn = (Button)findViewById(R.id.previous_responses_btn);

        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this , MainActivity.class);
                startActivity(intent);
            }
        });



        feelingYesRB = (RadioButton)findViewById(R.id.feeling_yes_rb);
        feelingNoRB = (RadioButton)findViewById(R.id.feeling_no_rb);


        feelingYesRB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if feeling comfortable is checked then disable preferred.
                if(isChecked){
                    preferenceSpinner.setEnabled(false);
                    preferredString = "justfine";
                }else{
                    preferenceSpinner.setEnabled(true);
                }

            }
        });


        final ArrayList<String> lst2 = new ArrayList<String>();
        lst2.add("Warmer");
        lst2.add("Warm");
        lst2.add("Just fine");
        lst2.add("Cool");
        lst2.add("Cooler");
        ArrayAdapter preferred = new ArrayAdapter<String>(this,
                R.layout.spinner_item, lst2);
        preferenceSpinner.setAdapter(preferred);



        formSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                SharedPreferences prefs = getSharedPreferences("preferencename", 0);

                name = prefs.getString("name", "error");

                if(feelingYesRB.isChecked()){
                    feelingString = "yes";
                 
                }else{

                    feelingString = "no";
                    preferredString = lst2.get(preferenceSpinner.getSelectedItemPosition());;
                }




                    new storeFeedbackAsyncTask().execute();

//                check if phone is connected to wifi, as the app won't work on mobile data
                if(isWifiConncted()) {
//                    check if the connected network is in IITB network
                    if(!isWifiIITNetwork()){

                        Toast.makeText(Home.this,"connected wifi May not be in IITB network",Toast.LENGTH_SHORT).show();



                    }
//                    execute async tack to store feedback
//                    new storeFeedbackAsyncTask().execute();
                }else{

                    Toast.makeText(Home.this,"Please connect to wifi (IITB Network)",Toast.LENGTH_SHORT).show();

                }



            }
        });








    }

// function to get json object from url
    public static JSONObject getJSONObjectFromURL(String urlString) throws IOException, JSONException {


//        creating a url connection to send data
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

        return new JSONObject(jsonString);
    }


    public class storeFeedbackAsyncTask extends AsyncTask<String,String,String> {


        ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            show the progress dialog
            pd = new ProgressDialog(Home.this);
            pd.setMessage("Sending Data to server");
            pd.show();
            //Toast.makeText(getActivity(),"exec" ,Toast.LENGTH_SHORT).show();
        }




        @Override
        protected String doInBackground(String... params) {

            String ip = "http://10.129.23.30:8000/apistoreformdata/"+name+"/"+feelingString.trim().replace(" ","")+"/"+preferredString.trim().replace(" ","");
            try{
                JSONObject jsonObj = getJSONObjectFromURL(ip);
                return  jsonObj.toString();


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
            System.out.println("sensors response"+response);
            try {
//                remove the progress dialog if it is showing
                if(pd.isShowing())
                    pd.dismiss();

                JSONObject root = new JSONObject(response);
                if(root.getString("response").equals("success")){

                    Toast.makeText(Home.this,"Your response has been successfully recorded.",Toast.LENGTH_SHORT).show();



                }


            }
            catch (JSONException e){



            }

            if(response.equals("Download failed")){

            }else{


            }


        }
    }







}
