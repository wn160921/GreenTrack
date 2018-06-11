package com.example.wn.greentrack;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.example.wn.greentrack.domain.User;
import com.example.wn.greentrack.net.OkHttpManager;
import com.example.wn.greentrack.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Request;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class PhotographFragment extends Fragment {
    String API_KEY = "Wz0qmeT0SkzNeakFG9n5tLxY";
    String SECRET_KEY = "ip1AP0GUe5qNG20f3zFFixHnaOg2uhjR";
    String auth;
    byte[] imageBuffer;
    List<String> showString = new LinkedList<>();
    ImageView imageView;
    Button button;
    TextView progress;
    public static final int TAKE_PHOTO = 1;
    private Uri imageUri;
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
        getAuth();
        waitThread = new WaitThread();
        waitThread.start();
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

    private boolean checkLogin(){
        SharedPreferences pref = getActivity().getSharedPreferences("data",MODE_PRIVATE);
        boolean logined = pref.getBoolean("logined",false);
        if(logined){
            String userJSON = pref.getString("user","");
            if(!userJSON.equals("")){
                Constant.user = (User) JSON.parseObject(userJSON,User.class);
            }else {
                return false;
            }
        }
        return logined;
    }

    public void paishe() {
        checkLogin();
        try {
            outputImage1 = new File(getActivity().getExternalCacheDir(), Constant.user.getUsername() + ".jpg");
            if (outputImage1.exists()) {
                outputImage1.delete();
            }
            outputImage1.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(getContext().getApplicationContext(), "com.example.wn.greentrack.fileprovider", outputImage1);
        } else {
            imageUri = Uri.fromFile(outputImage1);
        }
        Intent intent1 = new Intent("android.media.action.IMAGE_CAPTURE");
        intent1.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent1, TAKE_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        Rect rect = new Rect();
                        BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imageUri), rect, options);
                        options.inSampleSize = calculateInSampleSize(options, 300, 300);
                        options.inJustDecodeBounds = false;
                        Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imageUri), rect, options);
                        imageView.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    //确认是否加积分
                    toCheck();
                }
                break;
            default:
                break;
        }
    }

    private void toCheck() {
        progress.setVisibility(View.VISIBLE);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(imageView, "alpha", 1f, 0.4f, 1f);
        ObjectAnimator rotate = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 360f, 0f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(imageView, "scaleX", 1f, 0.3f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(imageView, "scaleY", 1f, 0.3f, 1f);
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
                if (sleeptime == 1000) {
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
        try{
            InputStream in = getActivity().getContentResolver().openInputStream(imageUri);
            byte[] b = new byte[1000];
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            int n;
            while ((n=in.read(b))!=-1){
                bao.write(b,0,n);
            }
            in.close();
            imageBuffer = null;
            imageBuffer = bao.toByteArray();
            bao.close();
            System.out.println("length:"+imageBuffer.length);
            imageBuffer = compressBitmap(imageBuffer,400);
            System.out.println("length:"+imageBuffer.length);
            String base64 = Base64.encodeToString(imageBuffer, Base64.DEFAULT);
            System.out.println("准备识别");
            classify(base64);
        }catch (Exception e){
            e.printStackTrace();
        }
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
            while (true){
                if(waitFlag){
                    try {
                        for(String s:showString){
                            setTextOrign(s);
                            sleep(1000);
                        }
                        waitFlag = false;
                        showString.clear();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void setTextOrign(final String s) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(progress!=null) {
                    progress.setText(s);
                }
            }
        });
    }
    boolean waitFlag = false;
    //识别
    public void classify(String image){
        System.out.println("auth-----------"+auth);
        OkHttpManager.getInstance().postNet("https://aip.baidubce.com/rest/2.0/image-classify/v2/advanced_general", new OkHttpManager.ResultCallback() {
            @Override
            public void onFailed(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onSuccess(String s) {
                try {
                    JSONObject js = new JSONObject(s);
                    //txt.setText(js.getJSONArray("result").toString());
                    JSONArray ja = js.getJSONArray("result");
                    for(int i=0;i<ja.length();i++){
                        showString.add(ja.get(i).toString());
                    }
                    showString.add("积分+2！！！");
                    sleeptime = 10000;
                    waitFlag = true;
                    Log.d("图片识别结果：",js.getJSONArray("result").toString());
                    Utils.addIntegral(2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },new OkHttpManager.Param("access_token",auth),new OkHttpManager.Param("image",image));
    }

    //获取授权吗
    private void getAuth() {
        OkHttpManager.getInstance().postNet("https://aip.baidubce.com/oauth/2.0/token", new OkHttpManager.ResultCallback() {
            @Override
            public void onFailed(Request request, IOException e) {
                System.out.println("auth failed");
            }

            @Override
            public void onSuccess(String s) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(s);
                    String access_token = jsonObject.getString("access_token");
                    auth = access_token;
                    System.out.println("auth success");
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        }, new OkHttpManager.Param("grant_type", "client_credentials"), new OkHttpManager.Param("client_id", API_KEY), new OkHttpManager.Param("client_secret", SECRET_KEY));

    }
    //图片压缩
    public byte[] compressBitmap(byte[] buffer,int WIDTH) {
        if (buffer == null || buffer.length == 0) {
            return null;
        }
        ByteArrayOutputStream baos = null;
        try {
            // 只获取图片的大小信息，而不是将整张图片载入在内存中，避免内存溢出
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(buffer, 0, buffer.length, options);
            int outWidth = options.outWidth;
            int scale = outWidth / WIDTH;
            if (scale > 1) {
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                Bitmap bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length, options);
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                return baos.toByteArray();
            } else {
                return buffer;
            }
        } catch (Exception e) {
            return null;
        } catch (OutOfMemoryError e) {
            return null;
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
            } catch (IOException e) {
                return null;
            }
        }
    }
}