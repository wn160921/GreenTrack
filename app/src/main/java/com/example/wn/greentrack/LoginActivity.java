package com.example.wn.greentrack;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.wn.greentrack.net.OkHttpManager;

import java.io.IOException;
import okhttp3.Request;

public class LoginActivity extends AppCompatActivity {
    Button login;
    Button register;
    TextInputEditText user;
    TextInputEditText pwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        user = findViewById(R.id.username_edit);
        pwd = findViewById(R.id.mima_edit);
        login = findViewById(R.id.loginBtn);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("test","注册");
                check(Constant.url+"abc");
            }
        });
        register = findViewById(R.id.registerBtn);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check(Constant.url+"register");
            }
        });

    }
    public void check(String url){
        OkHttpManager okHttpManager = OkHttpManager.getInstance();
        okHttpManager.postNet(url,new OkHttpManager.ResultCallback(){
            @Override
            public void onFailed(Request request, IOException e) {
                Log.d("test","失败");
            }

            @Override
            public void onSuccess(String s) {
                Log.d("test",s);
                weiteShared();
                Intent intent = new Intent(LoginActivity.this,WorkActivity.class);
                startActivity(intent);
                finish();
            }
        },new OkHttpManager.Param("username",user.getText().toString()),new OkHttpManager.Param("password",pwd.getText().toString()));
    }
    private void weiteShared(){
        SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
        editor.putString("username",user.getText().toString());
        Constant.username=user.getText().toString();
        editor.putBoolean("logined",true);
        editor.apply();
    }
}
