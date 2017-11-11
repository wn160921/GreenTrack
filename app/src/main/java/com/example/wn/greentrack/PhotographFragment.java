package com.example.wn.greentrack;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wn.greentrack.net.OkHttpManager;
import com.example.wn.greentrack.util.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import okhttp3.Request;

import static android.app.Activity.RESULT_OK;

public class PhotographFragment extends Fragment {
    ByteArrayOutputStream baos;
    ImageView imageView;
    Button button;
    TextView progress;
    public static final int TAKE_PHOTO=1;
    Uri imageUri;
    File outputImage1;
    public static int sleeptime = 1000;
    WaitThread waitThread;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photograph, container, false);
        waitThread = new WaitThread();
        imageView = view.findViewById(R.id.imageview);
        button = view.findViewById(R.id.take_photo);
        progress = view.findViewById(R.id.progress);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paishe();
            }
        });
        return view;
    }

    public void paishe(){
        outputImage1 =new File(getActivity().getExternalCacheDir(),Constant.username+".jpg");
        try{
            if(outputImage1.exists()){
                outputImage1.delete();
            }
            outputImage1.createNewFile();
        }catch (IOException e){
            e.printStackTrace();
        }
        if(Build.VERSION.SDK_INT>=24){
            imageUri= FileProvider.getUriForFile(getContext().getApplicationContext(),"com.example.wn.greentrack.fileprovider",outputImage1);
        }else {
            imageUri= Uri.fromFile(outputImage1);
        }
        Intent intent1=new Intent("android.media.action.IMAGE_CAPTURE");
        intent1.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(intent1,TAKE_PHOTO);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case TAKE_PHOTO:
                if(resultCode==RESULT_OK){
                    try {
                        BitmapFactory.Options options=new BitmapFactory.Options();
                        options.inJustDecodeBounds=true;
                        Rect rect=new Rect();
                        BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imageUri),rect,options);
                        options.inSampleSize=calculateInSampleSize(options,300,300);
                        options.inJustDecodeBounds=false;
                        Bitmap bitmap= BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imageUri),rect,options);
                        //baos=new ByteArrayOutputStream();
                        //bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
                        //b=null;
                        //b=baos.toByteArray();
                        imageView.setImageBitmap(bitmap);
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }
                    ToCheck();
                }
                break;
            default:
                break;
        }
    }
    private void ToCheck(){
        progress.setVisibility(View.VISIBLE);
        sleeptime = 1000;
        waitThread.start();
        ObjectAnimator alpha = ObjectAnimator.ofFloat(imageView,"alpha",1f,0.4f,1f);
        ObjectAnimator rotate = ObjectAnimator.ofFloat(imageView,"rotation",0f,360f,0f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(imageView,"scaleX",1f,0.3f,1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(imageView,"scaleY",1f,0.3f,1f);
        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(alpha).with(rotate).with(scaleX).with(scaleY);
        animatorSet.setDuration(3000);
        animatorSet.start();
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(sleeptime==1000) {
                    animation.start();
                }

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        OkHttpManager okHttpManager = OkHttpManager.getInstance();
        okHttpManager.postFile(Constant.url + "/upload",outputImage1 , new OkHttpManager.ResultCallback() {
            @Override
            public void onFailed(Request request, IOException e) {
                Log.d("upload","failed");
                Toast.makeText(getContext(),"网络连接失败",Toast.LENGTH_LONG);
                progress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onSuccess(String s) {
                sleeptime = 200;
                if(s.length()==1){
                    Utils.addIntegral(1);
                    Log.d("upload",s);
                    progress.setVisibility(View.INVISIBLE);
                    Toast.makeText(getActivity(),"识别成功，积分+"+s,Toast.LENGTH_LONG).show();
                    imageView.setImageResource(R.mipmap.logo);
                }else {
                    Log.d("upload",s);
                    Toast.makeText(getActivity(),"哎呀呀，服务器炸了",Toast.LENGTH_LONG).show();
                    progress.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
    /*
    正在清理工作台
    正在寻找放大镜
    正在擦拭相片
    正在仔细辨别
    正在最后确认
    正在发送确认信息
     */
    class WaitThread extends Thread {
        @Override
        public void run() {
            try {
                setTextOrign("正在清理工作台");
                sleep(sleeptime);
                setTextOrign("正在寻找放大镜");
                sleep(sleeptime);
                setTextOrign("正在擦拭图片");
                sleep(sleeptime);
                setTextOrign("正在仔细辨别");
                sleep(sleeptime);
                setTextOrign("正在最后确认");
                sleep(sleeptime);
                setTextOrign("正在发送确认信息");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void setTextOrign(final String s){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progress.setText(s);
            }
        });
    }
}
