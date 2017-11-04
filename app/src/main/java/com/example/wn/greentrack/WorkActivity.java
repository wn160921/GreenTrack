package com.example.wn.greentrack;


import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.ashokvarma.bottomnavigation.behaviour.BottomNavBarFabBehaviour;

import java.util.ArrayList;
import java.util.List;

public class WorkActivity extends AppCompatActivity {
    BottomNavigationBar bottomNavigationBar;
    DrawerLayout drawerLayout;
    ViewPager viewPager;
    MyFragmentAdapter myFragmentAdapter;
    PhotographFragment photographFragment;
    BusinessFragment businessFragment;
    IntrodutionFragment introdutionFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);
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
    }
}
