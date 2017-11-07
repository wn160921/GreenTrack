package com.example.wn.greentrack;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.wn.greentrack.net.OkHttpManager;
import com.example.wn.greentrack.util.Discount;
import com.example.wn.greentrack.util.DiscountAdapter;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;


/**
 * A simple {@link Fragment} subclass.
 */
public class BusinessFragment extends Fragment {
    XRecyclerView recyclerView;
    DiscountAdapter discountAdapter;
    private List<Discount> discountList = new ArrayList<>();
    public BusinessFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_business, container, false);
        recyclerView = view.findViewById(R.id.recycle);
        initDiscount();
        discountAdapter = new DiscountAdapter(discountList,this.getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(discountAdapter);
        recyclerView.setLoadingMoreEnabled(true);
        recyclerView.setLoadingMoreProgressStyle(ProgressStyle.SquareSpin);
        recyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                getdata();
            }
            @Override
            public void onLoadMore() {

            }
        });
        return view;
    }

    private void initDiscount(){
        recyclerView.refresh();
        getdata();
    }
    private void getdata(){
        OkHttpManager okHttpManager = OkHttpManager.getInstance();
        okHttpManager.getNet(Constant.url + "discount", new OkHttpManager.ResultCallback() {
            @Override
            public void onFailed(Request request, IOException e) {
                recyclerView.refreshComplete();
                Toast.makeText(getContext(),"哎呀，好像出问题了",Toast.LENGTH_SHORT);
            }

            @Override
            public void onSuccess(String s) {
                if(s.length()>0){
                    discountList.clear();
                }
                String[] splitefirst = s.split(";");
                for(int i=0;i<splitefirst.length;i++){
                    String[] splite_second = splitefirst[i].split(",");
                    discountList.add(new Discount(splite_second[0],splite_second[1],splite_second[2],Integer.valueOf(splite_second[3])));
                }
                recyclerView.refreshComplete();
                discountAdapter.notifyDataSetChanged();
            }
        });
    }

}
