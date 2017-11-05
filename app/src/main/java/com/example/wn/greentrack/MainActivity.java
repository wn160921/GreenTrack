package com.example.wn.greentrack;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(checkLogin()){
            Intent intent = new Intent(this,WorkActivity.class);
            startActivity(intent);
            finish();
        }else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
    private boolean checkLogin(){
        SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
        boolean logined = pref.getBoolean("logined",false);
        return logined;
    }
}
