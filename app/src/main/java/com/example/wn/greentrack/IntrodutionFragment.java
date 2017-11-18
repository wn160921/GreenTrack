package com.example.wn.greentrack;


import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class IntrodutionFragment extends Fragment {
    Button situation;
    LinearLayout xzll;
    Button canhe;
    LinearLayout chll;
    Button yinhuan;
    LinearLayout yhll;

    public IntrodutionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_blank, container, false);
        situation = view.findViewById(R.id.situation);
        xzll = view.findViewById(R.id.xzll);
        canhe = view.findViewById(R.id.canhe);
        chll = view.findViewById(R.id.chll);
        yinhuan = view.findViewById(R.id.yinhuan);
        yhll = view.findViewById(R.id.yhll);
        setOnclick();
        return view;
    }
    public void setOnclick(){
        situation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(xzll.getVisibility() == View.VISIBLE){
                    changeIcon(situation,0);
                    xzll.setVisibility(View.GONE);
                }else {
                    changeIcon(situation,1);
                    xzll.setVisibility(View.VISIBLE);
                }
            }
        });
        canhe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chll.getVisibility() == View.VISIBLE){
                    changeIcon(canhe,0);
                    chll.setVisibility(View.GONE);
                }else {
                    changeIcon(canhe,1);
                    chll.setVisibility(View.VISIBLE);
                }
            }
        });
        yinhuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(yhll.getVisibility() == View.VISIBLE){
                    changeIcon(yinhuan,0);
                    yhll.setVisibility(View.GONE);
                }else {
                    changeIcon(yinhuan,1);
                    yhll.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void changeIcon(Button b,int i){
        if(i==0){
            Drawable drawable = ContextCompat.getDrawable(getContext(),R.mipmap.up);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            b.setCompoundDrawables(null,null,drawable,null);
        }else {
            Drawable drawable = ContextCompat.getDrawable(getContext(),R.mipmap.down);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            b.setCompoundDrawables(null,null,drawable,null);
        }
    }
}
