package com.example.wn.greentrack;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.bumptech.glide.Glide;
import com.example.wn.greentrack.domain.Coupon;
import com.example.wn.greentrack.net.OkHttpManager;
import com.example.wn.greentrack.util.Utils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;

public class QuanActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<Coupon> quanList;
    QuanAdapter quanAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quan);
        recyclerView = findViewById(R.id.quan_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        quanList = new ArrayList<>();
        quanAdapter = new QuanAdapter(quanList,this);
        recyclerView.setAdapter(quanAdapter);
        getdata();
    }

    public void getdata(){
        OkHttpManager okHttpManager = OkHttpManager.getInstance();
        okHttpManager.postNet(Constant.url + "CouponServlet/", new OkHttpManager.ResultCallback() {
            @Override
            public void onFailed(Request request, IOException e) {
                Toast.makeText(getBaseContext(),"哎呀呀，服务器炸了",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(String s) {
                Log.d("优惠券信息",s);
                Toast.makeText(getBaseContext(),s,Toast.LENGTH_LONG).show();
                quanList = JSONArray.parseArray(s,Coupon.class);
                quanAdapter.notifyDataSetChanged();
            }
        },new OkHttpManager.Param("method","findAllProvider"));
    }

    class QuanAdapter extends RecyclerView.Adapter<QuanAdapter.QuanVH>{
        List<Coupon> quanList;
        Context context;

        public QuanAdapter(List<Coupon> quans, Context context){
            this.quanList = quans;
            this.context = context;
        }

        @Override
        public QuanVH onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quan_layout,parent,false);
            final QuanVH holder = new QuanVH(view);
            holder.tosure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Constant.user.getRewardPoints()>quanList.get(holder.getLayoutPosition()).getLimit()){
                        Coupon add = quanList.get(holder.getLayoutPosition());
                        tocheck(quanList.get(holder.getLayoutPosition()).getShow(),add);
                    }
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(QuanVH holder, int position) {
            Coupon quan=quanList.get(position);
            holder.text.setText(quan.getShow());
            holder.costs.setText("积分："+String.valueOf(quan.getLimit()));
            Glide.with(context).load(R.mipmap.ic_launcher).into(holder.imageView);
        }


        @Override
        public int getItemCount() {
            return quanList.size();
        }

        class QuanVH extends RecyclerView.ViewHolder{
            ImageView imageView;
            TextView text;
            TextView costs;
            Button tosure;
            public QuanVH(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.image);
                text = itemView.findViewById(R.id.text);
                costs = itemView.findViewById(R.id.cost_text);
                tosure = itemView.findViewById(R.id.tosure);
            }
        }
    }
    public void tocheck(final String check, final Coupon add){
        OkHttpManager okHttpManager = OkHttpManager.getInstance();
        okHttpManager.postNet(Constant.url + "own", new OkHttpManager.ResultCallback() {
            @Override
            public void onFailed(Request request, IOException e) {
                Toast.makeText(getBaseContext(),"",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSuccess(String s) {
                if(s.contains(check)){
                    Toast.makeText(getBaseContext(),"一口吃不成胖子，不急多买",Toast.LENGTH_SHORT).show();
                }else {
                    setown(add);
                }
            }
        },new OkHttpManager.Param("method","findAllProvider"));
    }
    public void setown(final Coupon add){
        OkHttpManager okHttpManager = OkHttpManager.getInstance();
        okHttpManager.postNet(Constant.url + "setown", new OkHttpManager.ResultCallback() {
            @Override
            public void onFailed(Request request, IOException e) {
                Toast.makeText(getBaseContext(),"哎呀呀，服务器炸了",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(String s) {
                if (Integer.valueOf(s)==1){
                    Utils.addIntegral(-add.getLimit());
                    Toast.makeText(getApplicationContext(),"兑换成功！请前往我的优惠券查看",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getBaseContext(),"哎呀呀，服务器炸了",Toast.LENGTH_SHORT).show();
                }
            }
        },new OkHttpManager.Param("user",Constant.user.getUsername()),new OkHttpManager.Param("intro",add.getShow()),new OkHttpManager.Param("text",Constant.shangjia));
    }
}
