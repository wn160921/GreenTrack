package com.example.wn.greentrack.util;

import com.alibaba.fastjson.JSON;
import com.example.wn.greentrack.Constant;
import com.example.wn.greentrack.domain.User;
import com.example.wn.greentrack.net.OkHttpManager;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Request;

/**
 * Created by wn on 2017/11/5.
 */

public class Utils {
    //获取积分
    public static void updateUser(){
        OkHttpManager okHttpManager = OkHttpManager.getInstance();
        okHttpManager.postNet(Constant.url+"UserServlet",
                new OkHttpManager.ResultCallback(){

                    @Override
                    public void onFailed(Request request, IOException e) {

                    }

                    @Override
                    public void onSuccess(String s) {
                        if(!s.equals("无信息")){
                            Constant.user = JSON.parseObject(s, User.class);
                            if(Constant.rewardPoints!=null){
                                Constant.rewardPoints.setText("当前积分："+Constant.user.getRewardPoints());
                            }
                        }
                    }
                },new OkHttpManager.Param("uuid",Constant.user.getUuid()),new OkHttpManager.Param("method","getUserByUuid"));
    }

    public static void addIntegral(int add){
        OkHttpManager okHttpManager = OkHttpManager.getInstance();
        okHttpManager.postNet(Constant.url + "UserServlet", new OkHttpManager.ResultCallback() {
            @Override
            public void onFailed(Request request, IOException e) {

            }

            @Override
            public void onSuccess(String s) {
                if(s.equals("积分改变成功"));
                updateUser();
            }
        },new OkHttpManager.Param("uuid",Constant.user.getUuid()),new OkHttpManager.Param("updateNum",String.valueOf(add)),new OkHttpManager.Param("method","addRewardPoints"));
    }
}
