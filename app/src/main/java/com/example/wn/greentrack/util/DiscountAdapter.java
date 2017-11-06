package com.example.wn.greentrack.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wn.greentrack.R;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.List;

/**
 * Created by wn on 2017/11/5.
 */

public class DiscountAdapter extends XRecyclerView.Adapter<DiscountAdapter.ViewHolder> {
    private List<Discount> discountList;

    public DiscountAdapter(List<Discount> discountList){
        this.discountList=discountList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.discount_layout,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Discount discount = discountList.get(position);
        holder.textView.setText(discount.text);
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
