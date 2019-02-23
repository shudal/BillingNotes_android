package com.example.perci.myapplication;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder>  {
    private List<Map<String, String>> lists= new ArrayList<Map<String, String>>();
    private Context context;

    public RecycleViewAdapter(Context context, List<Map<String, String>> lists) {
        this.context = context;
        this.lists = lists;
    }

    //创建View,被LayoutManager所用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.bill,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    //数据的绑定
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Map<String, String> map = lists.get(position);

        try {
            String bi = "";

            String io = map.get("io");
            if (io.equals("get")) {
                bi = bi + context.getResources().getString(R.string.get) + map.get("amount") + " " + context.getResources().getString(R.string.in) + " " + map.get("form") + " ";
            } else {
                bi = bi + context.getResources().getString(R.string.use) + map.get("amount") + " " + context.getResources().getString(R.string.in) + " " + map.get("form") + " " ;
            }

            if (!map.get("main").equals("")) {
                if (io.equals("get")) {
                    bi =  bi + context.getResources().getString(R.string.from) + " " + map.get("main") + " ";
                } else {
                    bi = bi + context.getResources().getString(R.string.to)  + " " + map.get("main") + " ";
                }
            }
            holder.bill_information.setText(bi);
            holder.bill_content.setText(map.get( "P.S : " + "content"));
            Log.v("AllF", "set bill information success  ");
        } catch (Exception e) {
            Log.v("AllF", "set bill informtion  faild:" + e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }
    //自定义ViewHolder,包含item的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView bill_information;
        public final TextView bill_content;
        public ViewHolder(View itemView) {
            super(itemView);
            bill_information       = (TextView) itemView.findViewById(R.id.bill_information);
            bill_content            = (TextView) itemView.findViewById(R.id.bill_content);
        }
    }
}
