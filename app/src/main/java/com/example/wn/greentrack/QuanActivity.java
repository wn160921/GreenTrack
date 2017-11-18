package com.example.wn.greentrack;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
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

import com.bumptech.glide.Glide;
import com.example.wn.greentrack.net.OkHttpManager;
import com.example.wn.greentrack.util.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;

public class QuanActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<Quan> quanList;
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
        Log.d("discounts",Constant.shangjia);
        OkHttpManager okHttpManager = OkHttpManager.getInstance();
        okHttpManager.postNet(Constant.url + "quan", new OkHttpManager.ResultCallback() {
            @Override
            public void onFailed(Request request, IOException e) {
                Toast.makeText(getBaseContext(),"哎呀呀，服务器炸了",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(String s) {
                Log.d("discounts",s);
                String[] one = s.split(";");
                if(one.length>=1){
                    for(int i=0;i<one.length;i++){
                        String[] two = one[i].split(",");
                        if(two.length>2){
                            Log.d("discounts",two[0]+two[1]+two[2]);
                            Quan quan=new Quan(two[0],two[1],Integer.valueOf(two[2]));
                            quanList.add(quan);
                        }
                    }
                }
                quanAdapter.notifyDataSetChanged();
            }
        },new OkHttpManager.Param("text",Constant.shangjia));
    }

    class Quan{
        String imageUrl;
        String text;
        int cost;
        public Quan(String imageUrl, String text, int cost){
            this.imageUrl=imageUrl;
            this.text=text;
            this.cost=cost;
        }
    }

    class QuanAdapter extends RecyclerView.Adapter<QuanAdapter.QuanVH>{
        List<Quan> quanList;
        Context context;

        public QuanAdapter(List<Quan> quans, Context context){
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
                    if(Constant.jifen>quanList.get(holder.getLayoutPosition()).cost){
                        Quan add = quanList.get(holder.getLayoutPosition());
                        tocheck(quanList.get(holder.getLayoutPosition()).text,add);
                    }
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(QuanVH holder, int position) {
            Quan quan=quanList.get(position);
            holder.text.setText(quan.text);
            holder.costs.setText("积分："+String.valueOf(quan.cost));
            Glide.with(context).load(Constant.url+quan.imageUrl).into(holder.imageView);
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
    public void tocheck(final String check, final Quan add){
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
        },new OkHttpManager.Param("user",Constant.username));
    }
    public void setown(final Quan add){
        OkHttpManager okHttpManager = OkHttpManager.getInstance();
        okHttpManager.postNet(Constant.url + "setown", new OkHttpManager.ResultCallback() {
            @Override
            public void onFailed(Request request, IOException e) {
                Toast.makeText(getBaseContext(),"哎呀呀，服务器炸了",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(String s) {
                if (Integer.valueOf(s)==1){
                    Utils.addIntegral(-add.cost);
                    Toast.makeText(getApplicationContext(),"兑换成功！请前往我的优惠券查看",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getBaseContext(),"哎呀呀，服务器炸了",Toast.LENGTH_SHORT).show();
                }
            }
        },new OkHttpManager.Param("user",Constant.username),new OkHttpManager.Param("intro",add.text),new OkHttpManager.Param("text",Constant.shangjia));
    }
}
