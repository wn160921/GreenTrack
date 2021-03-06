package com.example.wn.greentrack.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.wn.greentrack.Constant;
import com.example.wn.greentrack.QuanActivity;
import com.example.wn.greentrack.R;
import com.example.wn.greentrack.domain.Coupon;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.List;

/**
 * Created by wn on 2017/11/5.
 */

public class DiscountAdapter extends XRecyclerView.Adapter<DiscountAdapter.ViewHolder> {
    private List<Coupon> discountList;
    Context context;

    public DiscountAdapter(List<Coupon> discountList, Context context){
        this.discountList=discountList;
        this.context=context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.discount_layout,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constant.shangjia=holder.textView.getText().toString();
                Intent intent = new Intent(context, QuanActivity.class);
                context.startActivity(intent);
            }
        });
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constant.shangjia=holder.textView.getText().toString();
                Intent intent = new Intent(context, QuanActivity.class);
                context.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Coupon discount = discountList.get(position);
        holder.textView.setText(discount.getShow());
        Glide.with(context).load(R.mipmap.ic_launcher).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return discountList.size();
    }

    static class ViewHolder extends XRecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView;
        public ViewHolder(View view){
            super(view);
            imageView = view.findViewById(R.id.image);
            textView = view.findViewById(R.id.text);
        }
    }
}
