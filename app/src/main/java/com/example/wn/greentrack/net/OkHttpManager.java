package com.example.wn.greentrack.net;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by wn on 2017/11/4.
 */

public class OkHttpManager {
    public static final MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("image/png");
    private static OkHttpManager instance;
    private OkHttpClient okHttpClient;
    private Handler okhandler;

    public OkHttpManager(){     //单例模式，不让new
        okhandler=new Handler(Looper.getMainLooper());
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .writeTimeout(3,TimeUnit.SECONDS)
                .readTimeout(3,TimeUnit.SECONDS);
        okHttpClient=builder.build();
    }
    public static OkHttpManager getInstance(){
        if(instance == null){
            synchronized (OkHttpManager.class){
                instance=new OkHttpManager();
            }
        }
        return instance;
    }

    public void postNet(String url,ResultCallback resultCallback,Param... params){
        if(params==null){
            params=new Param[0];    //校验
        }
        FormBody.Builder formbody= new FormBody.Builder();//模拟表单请求
        for(Param p:params){
            formbody.add(p.key,p.value);
        }
        RequestBody requestBody=formbody.build();
        Request request=new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        dealNet(request,resultCallback);
    }
    public void postFile(String url,File file, ResultCallback resultCallback) {
        //Log.d("upload",path);
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("file",file.getName(), RequestBody.create(MediaType.parse("image/png"),file));
        Request request  = new Request.Builder().url(url).post(builder.build()).build();
        dealNet(request,resultCallback);
    }



    private void dealNet(final Request request, final ResultCallback resultCallback){
        Log.d("http","dealnet");
        okHttpClient.newCall(request).enqueue(new Callback() {//异步执行
            @Override
            public void onFailure(Call call, final IOException e) {
                okhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        resultCallback.onFailed(request,e);
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                String str="";
                try{
                    str=response.body().string();
                }catch (IOException e){
                    e.printStackTrace();
                }
                final String finalstr=str;
                okhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        resultCallback.onSuccess(finalstr);
                    }
                });
            }
        });
    }

    public static class Param{
        String key;
        String value;
        public Param(String key,String value){
            this.key=key;
            this.value=value;
        }
    }

    public static abstract class ResultCallback{
        public abstract void onFailed(Request request,IOException e);
        public abstract void onSuccess(String s);
    }

    public void getNet(String url, ResultCallback resultCallback) {
        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)//此设置默认为get,可以不设置
                .build();
        dealNet(request, resultCallback);
    }

}
