package com.example.wn.greentrack;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.wn.greentrack.net.OkHttpManager;
import com.example.wn.greentrack.util.Discount;
import com.example.wn.greentrack.util.DiscountAdapter;
import com.example.wn.greentrack.util.DividerItemDecoration;
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
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL_LIST));
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
                recyclerView.loadMoreComplete();
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
                Toast.makeText(getContext(),"哎呀，好像出问题了"+Constant.url+"discount",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSuccess(String s) {
                Log.d("discount",Constant.url+"discount");
                if(s.length()>0){
                    discountList.clear();
                }else {
                    recyclerView.refreshComplete();
                    return;
                }
                String[] splitefirst = s.split(";");
                for(int i=0;i<splitefirst.length;i++){
                    String[] splite_second = splitefirst[i].split(",");
                    if(splite_second.length==2){
                        discountList.add(new Discount(splite_second[0],splite_second[1]));
                    }
                }
                recyclerView.refreshComplete();
                discountAdapter.notifyDataSetChanged();
            }
        });
    }

}
