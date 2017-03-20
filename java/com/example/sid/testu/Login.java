package com.example.sid.testu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


//        get the name of the user
        SharedPreferences prefs = getSharedPreferences("preferencename", 0);
        String loginName = prefs.getString("name","@");

//        if the user name is present go to the home page
        if(!loginName.equals("@")&&loginName.length()>0){

            goTOActivity();

        }

        final TextView loginNameEt = (TextView)findViewById(R.id.login_name_et);
        Button loginSubmitBtn = (Button)findViewById(R.id.login_submit_btn);
        loginSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = loginNameEt.getText().toString();


                if(name!=null&&!name.trim().equals("")){




//                      store the name in shared prefs.
                        SharedPreferences prefs = getSharedPreferences("preferencename", 0);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("name", name);



                        editor.commit();
                    goTOActivity();


                }else{

                    Toast.makeText(Login.this,"Please enter the name", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    private void goTOActivity() {
        Intent intent = new Intent(this,Home.class);
        startActivity(intent);
        finish();

    }
}
