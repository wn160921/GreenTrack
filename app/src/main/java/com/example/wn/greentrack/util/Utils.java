package com.example.wn.greentrack.util;

import com.example.wn.greentrack.Constant;
import com.example.wn.greentrack.net.OkHttpManager;
import java.io.IOException;

import okhttp3.Request;

/**
 * Created by wn on 2017/11/5.
 */

public class Utils {
    public static void getIntegral(){
        OkHttpManager okHttpManager = OkHttpManager.getInstance();
        okHttpManager.postNet(Constant.url+"gtf",
                new OkHttpManager.ResultCallback(){

                    @Override
                    public void onFailed(Request request, IOException e) {

                    }

                    @Override
                    public void onSuccess(String s) {
                        if(true){
                            Constant.user.setRewardPoints(Integer.valueOf(s));
                            //Constant.textView.setText("当前积分："+Constant.user.getRewardPoints());
                        }
                    }
                },new OkHttpManager.Param("user",Constant.user.getUsername()));
    }

    public static void addIntegral(int add){
        OkHttpManager okHttpManager = OkHttpManager.getInstance();
        okHttpManager.postNet(Constant.url + "/setjifen", new OkHttpManager.ResultCallback() {
            @Override
            public void onFailed(Request request, IOException e) {

            }

            @Override
            public void onSuccess(String s) {
                getIntegral();
            }
        },new OkHttpManager.Param("user",Constant.user.getUsername()),new OkHttpManager.Param("add",String.valueOf(add)));
    }
}
