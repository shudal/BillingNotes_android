package com.example.perci.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AllFragment extends Fragment {
    public  static  List<Map<String, String>> bills = new ArrayList<Map<String, String>>();
    public static RecyclerView recyclerView;
    public static int month_used = 0;
    public static int sum = 0;
    public static int my_get = 0;
    public static int my_out = 0;
    public static int month_fee = 0;
    public static int month_remaining = 0;
    public static double day_average;
    public static int remaining_day = 0;

    private OnFragmentInteractionListener mListener;

    public Handler uiHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            try {
                //设定统计数字
                TextView textView = (TextView) getActivity().findViewById(R.id.AllF_sum);
                textView.setText(" " + AllFragment.sum);

                textView = (TextView) getActivity().findViewById(R.id.AllF_out);
                textView.setText(" " + AllFragment.my_out);

                textView = (TextView) getActivity().findViewById(R.id.AllF_get);
                textView.setText(" " + AllFragment.my_get);

                textView = (TextView) getActivity().findViewById(R.id.AllF_month_used);
                textView.setText((" " + AllFragment.month_used));

                textView = (TextView) getActivity().findViewById(R.id.AllF_month_remaining);
                AllFragment.month_remaining = AllFragment.month_fee - AllFragment.month_used;
                textView.setText(" " + AllFragment.month_remaining);

                textView = (TextView) getActivity().findViewById(R.id.AllF_day_average);
                AllFragment.day_average = AllFragment.month_remaining * 1.0 / AllFragment.remaining_day;
                textView.setText(" " + AllFragment.day_average);

                //设定各统计数字题目的宽度一致

                TextView textView2 = (TextView) getActivity().findViewById(R.id.AllF_day_average_title);

                textView = (TextView) getActivity().findViewById(R.id.AllF_sum_title);
                textView.setWidth(textView2.getWidth());

                textView = (TextView) getActivity().findViewById(R.id.AllF_out_title);
                textView.setWidth(textView2.getWidth());

                textView = (TextView) getActivity().findViewById(R.id.AllF_get_title);
                textView.setWidth(textView2.getWidth());

                textView = (TextView) getActivity().findViewById(R.id.AllF_month_used_title);
                textView.setWidth(textView2.getWidth());

                AllFragment.recyclerView.setAdapter(new RecycleViewAdapter(getActivity(), AllFragment.bills));
            } catch (Exception e) {
                try {
                    Toast.makeText(getContext(), getString(R.string.initialing), Toast.LENGTH_SHORT).show();
                } catch (Exception e2) {

                }
            }
            return true;
        }

    });

    public AllFragment() {
        // Required empty public constructor
    }

    public static AllFragment newInstance() {
        AllFragment fragment = new AllFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_all, container, false);
        return v;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)  {
        super.onActivityCreated(savedInstanceState);

        Calendar cal = Calendar.getInstance();
        int default_year = cal.get(Calendar.YEAR);
        int default_month = cal.get(Calendar.MONTH) + 1;
        int default_day = cal.get(Calendar.DAY_OF_MONTH);
        try {
            TextView textView = getActivity().findViewById(R.id.AllF_today);
            textView.setText(" " + default_year + "  " + default_month + "  " + default_day);
        } catch (Exception e) {
            Toast.makeText(getContext(), getString(R.string.initialing), Toast.LENGTH_SHORT).show();
        }
        try {
            this.recyclerView = (RecyclerView) getView().findViewById(R.id.AllF_re);
            Log.v("AllF", "get recycler view success.:" + this.recyclerView);
        } catch (Exception e) {
            Log.v("AllF", "get recuclerview feiled," + e.getMessage());
        }

        recyclerView.removeAllViews();
        bills.clear();
        Log.v("my_bills:", bills.toString());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        AllFragment.recyclerView.setLayoutManager(layoutManager);

        OkHttpClient client = new OkHttpClient();

        SharedPreferences sharedPreferences =  getActivity().getSharedPreferences("token", Context.MODE_PRIVATE);
        String sessionid = sharedPreferences.getString("sessionid", "");
        String token = sharedPreferences.getString("token","");
        Sha1 sha1 = new Sha1();
        sha1.setS(token);
        token = sha1.data;

        String  the_url = MainActivity.SERVER_URL + "bills?sessionid=" + sessionid + "&token=" + token;

        Request request;
        request = new Request.Builder()
                .get()
                .url(the_url)
                .build();
        Log.v("my_all", " construt url : the_url" + the_url);

        Call call = client.newCall(request);
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                String result = response.body().string();
                Log.v("my_all", "bills: " + result);
                try {

                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getInt("status") == 1) {
                        AllFragment.my_get = jsonObject.getInt("my_get");
                        AllFragment.my_out = jsonObject.getInt("my_out");
                        AllFragment.sum  = jsonObject.getInt("sum");
                        AllFragment.month_used = jsonObject.getInt("month_used");
                        AllFragment.month_fee  = jsonObject.getInt("month_fee");
                        AllFragment.remaining_day = jsonObject.getInt("remaining_day");

                        JSONArray jsonArray = jsonObject.getJSONArray("bills");

                        JSONObject jsonObject2 = jsonArray.getJSONObject(0);

                        String s1 = jsonObject2.getString("io");
                        Log.v("my_all","j1 :get io success:" + s1);

                        try {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                Map<String, String> map = new HashMap<String, String>();
                                map.put("year", jsonObject1.getInt("year") + "");
                                map.put("month", jsonObject1.getInt("month") + "");
                                map.put("day", jsonObject1.getInt("day") + "");

                                map.put("io", jsonObject1.getString("io"));
                                map.put("form", jsonObject1.getString("form"));
                                map.put("main", jsonObject1.getString("main"));
                                map.put("content", jsonObject1.getString("content"));
                                map.put("amount", jsonObject1.getInt("sum") + "");
                                Log.v("my_all", "map :" + map);
                                AllFragment.bills.add(map);

                                try {
                                    uiHandler.sendEmptyMessage(1);
                                } catch (Exception e) {
                                    Toast.makeText(getContext(), getString(R.string.initialing), Toast.LENGTH_SHORT).show();
                                }
                            }


                        } catch (Exception e) {
                            Log.v("my_all", "generate array fied" + e.getMessage());
                        }
                        Log.v("my_all", "json array: " +  jsonArray);
                        Log.v("my_all", "real bills: " +  bills);

                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle(getResources().getString(R.string.failure));

                        Msg msg = new Msg(getActivity());

                        builder.setMessage(msg.get(jsonObject.getString("msgname")));

                        builder.setCancelable(true);
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        Looper.prepare();
                        AlertDialog dialog = builder.create();
                        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialog) {
                            }
                        });
                        //对话框消失的监听事件
                        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                            }
                        });
                        dialog.show();
                        Looper.loop();
                    }
                } catch (Exception e) {
                    Log.v("my_all", "parse json failed ");
                }
            }
        };

        try {
            call.enqueue(callback);
        } catch (Exception e) {
            Msg  msg = new Msg(getContext());
            msg.certainMsg(getString(R.string.unknown_error),getString(R.string.certain));
        }
    }
}
