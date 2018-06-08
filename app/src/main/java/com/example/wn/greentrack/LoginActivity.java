package com.example.wn.greentrack;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.wn.greentrack.domain.User;
import com.example.wn.greentrack.net.OkHttpManager;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import okhttp3.Request;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    Tencent mTencent;
    String openidString;
    //登录
    Button login;
    //注册
    TextView register;
    TextView forgetPassword;
    ImageButton qqLoginBtn;
    ImageButton weiboLoginBtn;
    EditText userET;
    EditText pwdET;

    RelativeLayout loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
    }

    public void initViews(){
        login = findViewById(R.id.loginBtn);
        register = findViewById(R.id.register);
        forgetPassword = findViewById(R.id.forgetPassword);
        qqLoginBtn = findViewById(R.id.qqLogin);
        weiboLoginBtn = findViewById(R.id.weiboLogin);
        userET = findViewById(R.id.username_edit);
        pwdET = findViewById(R.id.pwd_edit);
        loading = findViewById(R.id.loaging);
        login.setOnClickListener(this);
        register.setOnClickListener(this);
        forgetPassword.setOnClickListener(this);
        qqLoginBtn.setOnClickListener(this);
        weiboLoginBtn.setOnClickListener(this);
    }

    //登录联网检查
    public void check(String url){
        final String tag = "登录：";
        OkHttpManager okHttpManager = OkHttpManager.getInstance();
        okHttpManager.postNet(url,new OkHttpManager.ResultCallback(){
            @Override
            public void onFailed(Request request, IOException e) {
                loading.setVisibility(View.GONE);
                Toast.makeText(getBaseContext(),"无法联网",Toast.LENGTH_SHORT).show();
                Log.d(tag,"联网问题");
            }
            @Override
            public void onSuccess(String s) {
                loading.setVisibility(View.GONE);
                Log.d(tag,s);
                if(s.equals("参数错误")){
                    Toast.makeText(getBaseContext(),"账号错误",Toast.LENGTH_SHORT).show();
                }else if(s.equals("密码错误")){
                    Toast.makeText(getBaseContext(),"密码错误",Toast.LENGTH_SHORT).show();
                } else {
                    writeShared(s);
                    Intent intent = new Intent(LoginActivity.this,WorkActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        },new OkHttpManager.Param("username",userET.getText().toString()),new OkHttpManager.Param("password",pwdET.getText().toString()),new OkHttpManager.Param("method","findUserToLogin"));
    }
    private void writeShared(String user){
        SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
        editor.putString("user",user);
        Log.d("写入的用户参数",user);
        Constant.user = (User) JSON.parseObject(user,User.class);
        editor.putBoolean("logined",true);
        editor.apply();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.loginBtn:
                loading.setVisibility(View.VISIBLE);
                check(Constant.url+"UserServlet");
                break;
            case R.id.qqLogin:
                mTencent = Tencent.createInstance("1106699587", this.getApplicationContext());
                mTencent.login(LoginActivity.this,"all",new BaseUiListener());

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Tencent.onActivityResultData(requestCode, resultCode, data, new BaseUiListener());

        if(requestCode == Constants.REQUEST_API) {
            if(resultCode == Constants.REQUEST_LOGIN) {
                Tencent.handleResultData(data, new BaseUiListener());
            }
        }
    }
    private class BaseUiListener implements IUiListener {

        public void onComplete(Object response) {
            // TODO Auto-generated method stub
            Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
            /*
             * 下面隐藏的是用户登录成功后 登录用户数据的获取的方法
             * 共分为两种  一种是简单的信息的获取,另一种是通过UserInfo类获取用户较为详细的信息
             *有需要看看
             * */
            try {
                //获得的数据是JSON格式的，获得你想获得的内容
                //如果你不知道你能获得什么，看一下下面的LOG
                Log.v("----TAG--", "-------------"+response.toString());
                openidString = ((JSONObject) response).getString("openid");
                mTencent.setOpenId(openidString);

                mTencent.setAccessToken(((JSONObject) response).getString("access_token"),((JSONObject) response).getString("expires_in"));


                Log.v("TAG", "-------------"+openidString);
                //access_token= ((JSONObject) response).getString("access_token");              //expires_in = ((JSONObject) response).getString("expires_in");
                writeShared(userET.getText().toString());
                Intent intent = new Intent(LoginActivity.this,WorkActivity.class);
                startActivity(intent);
                finish();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            /**到此已经获得OpneID以及其他你想获得的内容了
             QQ登录成功了，我们还想获取一些QQ的基本信息，比如昵称，头像什么的，这个时候怎么办？
             sdk给我们提供了一个类UserInfo，这个类中封装了QQ用户的一些信息，我么可以通过这个类拿到这些信息
             如何得到这个UserInfo类呢？  *//*

            QQToken qqToken = mTencent.getQQToken();
            UserInfo info = new UserInfo(getApplicationContext(), qqToken);

            //    info.getUserInfo(new BaseUIListener(this,"get_simple_userinfo"));
            info.getUserInfo(new IUiListener() {
                @Override
                public void onComplete(Object o) {
                    //用户信息获取到了

                    try {

                        Toast.makeText(getApplicationContext(), ((JSONObject) o).getString("nickname")+((JSONObject) o).getString("gender"), Toast.LENGTH_SHORT).show();
                        Log.v("UserInfo",o.toString());
                        Intent intent1 = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent1);
                        finish();
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(UiError uiError) {
                    Log.v("UserInfo","onError");
                }

                @Override
                public void onCancel() {
                    Log.v("UserInfo","onCancel");
                }
            });*/

        }

        @Override
        public void onError(UiError uiError) {
            Toast.makeText(getApplicationContext(), "onError", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(getApplicationContext(), "onCancel", Toast.LENGTH_SHORT).show();
        }


    }
}
