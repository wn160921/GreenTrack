package com.example.wn.greentrack;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.example.wn.greentrack.net.OkHttpManager;
import com.example.wn.greentrack.side.AboutUsActivity;
import com.example.wn.greentrack.side.HelpActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;

public class WorkActivity extends AppCompatActivity {
    BottomNavigationBar bottomNavigationBar;
    DrawerLayout drawerLayout;
    ViewPager viewPager;
    MyFragmentAdapter myFragmentAdapter;
    PhotographFragment photographFragment;
    BusinessFragment businessFragment;
    IntrodutionFragment introdutionFragment;

    Button about_us;
    Button help;
    Button exit;
    TextView jifen;
    private int jifenshu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);
        init_side_button();
        drawerLayout = findViewById(R.id.drawlayout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if( actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.settings);
        }
        bottomNavigationBar = findViewById(R.id.bottom_navigation_bar);
        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.mipmap.bg1,"商家"))
                .addItem(new BottomNavigationItem(R.mipmap.bg2,"拍照"))
                .addItem(new BottomNavigationItem(R.mipmap.bg3,"介绍"))
                .initialise();
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        initViewPager();
        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                viewPager.setCurrentItem(position);
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {

            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return true;
    }
    public void initViewPager(){
        viewPager = findViewById(R.id.viewpager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                bottomNavigationBar.selectTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        businessFragment = new BusinessFragment();
        introdutionFragment = new IntrodutionFragment();
        photographFragment = new PhotographFragment();
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(businessFragment);
        fragmentList.add(photographFragment);
        fragmentList.add(introdutionFragment);
        myFragmentAdapter = new MyFragmentAdapter(getSupportFragmentManager(),fragmentList);
        viewPager.setAdapter(myFragmentAdapter);
        viewPager.setOffscreenPageLimit(3);  //防止多次加载
    }
    private void init_side_button(){
        about_us = findViewById(R.id.about_us);
        help = findViewById(R.id.help);
        exit = findViewById(R.id.exit);
        jifen = findViewById(R.id.jifen);
        SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
        String logined = pref.getString("username","");
        getIntegral(logined);
        about_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WorkActivity.this, AboutUsActivity.class);
                startActivity(intent);
            }
        });
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WorkActivity.this, HelpActivity.class);
                startActivity(intent);
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                editor.putBoolean("logined",false);
                editor.apply();
                Intent intent = new Intent(WorkActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    public void getIntegral(String user){
        OkHttpManager okHttpManager = OkHttpManager.getInstance();
        okHttpManager.postNet(Constant.url+"gtf",
                new OkHttpManager.ResultCallback(){

                    @Override
                    public void onFailed(Request request, IOException e) {

                    }

                    @Override
                    public void onSuccess(String s) {
                        jifenshu = Integer.valueOf(s);
                        jifen.setText("当前积分为"+s);
                    }
                },new OkHttpManager.Param("user",user));
    }
    public void addIntegral(int add){
        OkHttpManager okHttpManager = OkHttpManager.getInstance();
        okHttpManager.postNet(Constant.url + "/setjifen", new OkHttpManager.ResultCallback() {
            @Override
            public void onFailed(Request request, IOException e) {

            }

            @Override
            public void onSuccess(String s) {
                SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
                String logined = pref.getString("username","");
                getIntegral(logined);
            }
        },new OkHttpManager.Param("user",getUsernaem()),new OkHttpManager.Param("add",String.valueOf(jifenshu+add)));
    }
    public String getUsernaem(){
        SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
        String logined = pref.getString("username","");
        return logined;
    }
}
