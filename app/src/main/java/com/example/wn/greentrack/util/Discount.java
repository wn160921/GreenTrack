package com.example.wn.greentrack.util;

/**
 * Created by wn on 2017/11/5.
 */

public class Discount {
    public String text;
    public String url;
    public String active_code;
    public int cost;
    public Discount(String arg1,String arg2,String arg3,int cost){
        this.text=arg1;
        url=arg2;
        this.active_code = arg3;
        this.cost = cost;
    }
}
