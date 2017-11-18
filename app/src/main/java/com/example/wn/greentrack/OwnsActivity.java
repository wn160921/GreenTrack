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

import com.bumptech.glide.Glide;
import com.example.wn.greentrack.net.OkHttpManager;
import com.example.wn.greentrack.util.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;

public class OwnsActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<Quan> quanList;
    OwnAdapter ownAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owns);
        recyclerView = findViewById(R.id.ownrecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        quanList = new ArrayList<>();
        ownAdapter = new OwnAdapter(quanList,this);
        recyclerView.setAdapter(ownAdapter);
        getdata();
    }
    public void getdata(){
        Log.d("discounts",Constant.username);
        OkHttpManager okHttpManager = OkHttpManager.getInstance();
        okHttpManager.postNet(Constant.url + "own", new OkHttpManager.ResultCallback() {
            @Override
            public void onFailed(Request request, IOException e) {
                Toast.makeText(getBaseContext(),"哎呀呀，服务器炸了",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSuccess(String s) {
                Log.d("discounts",s);
                /*
                突出亮点
                伟大的事情

                亮出市场调研
                拉近距离

                商业模式！！！

                 */


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
                ownAdapter.notifyDataSetChanged();
            }
        },new OkHttpManager.Param("user",Constant.username));
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

    class OwnAdapter extends RecyclerView.Adapter<OwnAdapter.QuanVH>{
        List<Quan> quanList;
        Context context;

        public OwnAdapter(List<Quan> quans, Context context){
            this.quanList = quans;
            this.context = context;
        }

        @Override
        public QuanVH onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.own_layout,parent,false);
            final OwnAdapter.QuanVH holder = new OwnAdapter.QuanVH(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(OwnAdapter.QuanVH holder, int position) {
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
            public QuanVH(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.image);
                text = itemView.findViewById(R.id.text);
                costs = itemView.findViewById(R.id.cost_text);
            }
        }


    }
}
