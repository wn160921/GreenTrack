package com.example.wn.greentrack.util;

import android.app.Activity;
import android.content.Context;
import android.os.Message;
import android.widget.TextView;

import com.example.wn.greentrack.net.OkHttpManager;

import java.io.IOException;
import java.util.logging.Handler;

import okhttp3.Request;

/**
 * Created by wn on 2017/11/5.
 */

public class Utils {
    public static void getIntegral(String user, final Activity activity){
        OkHttpManager okHttpManager = OkHttpManager.getInstance();
        okHttpManager.postNet("http://192.168.1.101:8080/gtf",
                new OkHttpManager.ResultCallback(){

                    @Override
                    public void onFailed(Request request, IOException e) {

                    }

                    @Override
                    public void onSuccess(String s) {

                    }
                },new OkHttpManager.Param("user",user));
    };
}
