package com.example.wn.greentrack.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.wn.greentrack.Constant;
import com.example.wn.greentrack.R;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.List;

/**
 * Created by wn on 2017/11/5.
 */

public class DiscountAdapter extends XRecyclerView.Adapter<DiscountAdapter.ViewHolder> {
    private List<Discount> discountList;
    Context context;

    public DiscountAdapter(List<Discount> discountList,Context context){
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
                dialog_build(discountList.get(holder.getLayoutPosition()-1));
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Discount discount = discountList.get(position);
        holder.textView.setText(discount.text);
        Glide.with(context).load(Constant.url+discount.url).into(holder.imageView);
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
    public void dialog_build(final Discount discount){
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("优惠信息");
        dialog.setMessage("积分消耗："+String.valueOf(discount.cost));
        dialog.setCancelable(true);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Utils.addIntegral(-Integer.valueOf(discount.cost));
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.show();
    }

}
