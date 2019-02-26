package com.example.perci.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
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

    public String selected_year;
    public String selected_month;
    public String selected_day;

    private OnFragmentInteractionListener mListener;

    public  ArrayList datalist1;
    public  ArrayList datalist2;
    public  ArrayList datalist3;

    public Spinner sp_year;
    public Spinner sp_month;
    public Spinner sp_day;

    public Button btn_certain;
    public Button btn_cancel;

    public int default_year;
    public int default_month;
    public int default_day;

    public Handler disCertain = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            try {
                selected_year = sp_year.getSelectedItem().toString();
                selected_month = sp_month.getSelectedItem().toString();
                selected_day = sp_day.getSelectedItem().toString();

                if ((!selected_year.equals(IndexActivity.which_year)) || (!selected_month.equals(IndexActivity.which_month)) || (!selected_day.equals(IndexActivity.which_day))) {
                    btn_certain.setVisibility(View.VISIBLE);
                    btn_cancel.setVisibility(View.VISIBLE);
                } else {
                    btn_certain.setVisibility(View.GONE);
                    btn_cancel.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                try {
                    Toast.makeText(getContext(), getString(R.string.unknown_error), Toast.LENGTH_SHORT);
                } catch (Exception e2) {

                }
            }
            return  true;
        }
    });

    public  Handler disPrompt=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Msg msg1 = new Msg(getContext());
            msg1.disPrompt();
            return true;
        }

    });

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

                sp_year.setSelection(0, true);
                sp_month.setSelection(0,true);
                sp_day.setSelection(0,true);

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

        sp_year  = (Spinner) v.findViewById(R.id.AllF_sp_year);
        sp_month = (Spinner) v.findViewById(R.id.AllF_sp_month);
        sp_day   = (Spinner) v.findViewById(R.id.AllF_sp_day);

        btn_certain = (Button) v.findViewById(R.id.AllF_b_certain);
        btn_cancel  = (Button) v.findViewById(R.id.AllF_b_cancle);

        try {
            btn_certain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        IndexActivity.which_year = selected_year;
                        IndexActivity.which_month = selected_month;
                        IndexActivity.which_day = selected_day;

                        iniSpin();
                        setRV();

                        btn_certain.setVisibility(View.GONE);
                        btn_cancel.setVisibility(View.GONE);
                    } catch (Exception e) {

                    }
                }
            });

            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btn_certain.setVisibility(View.GONE);
                    btn_cancel.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {

        }

        Calendar cal = Calendar.getInstance();
        default_year = cal.get(Calendar.YEAR);
        default_month = cal.get(Calendar.MONTH) + 1;
        default_day = cal.get(Calendar.DAY_OF_MONTH);

        iniSpin();

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

    public void iniSpin() {
        datalist1 = new ArrayList<String>();
        datalist1.add("" + IndexActivity.which_year);
        for(int i = default_year ; i >= default_year-2; i--) {
            if (i != Integer.parseInt(IndexActivity.which_year)) {
                datalist1.add("" + i);
            }
        }
        SpinnerAdapter adaper = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, datalist1);
        sp_year.setAdapter(adaper);
        sp_year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                disCertain.sendEmptyMessage(1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                disCertain.sendEmptyMessage(0);
            }
        });


        datalist2 = new ArrayList<String>();
        datalist2.add(IndexActivity.which_month + "");
        for (int i = default_month; ; i--) {
            if (i == default_month && (datalist2.size() == 12)) {
                break;
            }
            if (i != Integer.parseInt(IndexActivity.which_month)) {
                datalist2.add(i + "");
            }
            if (i == 1) {
                i = 13;
            }
        }
        adaper = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, datalist2);
        sp_month.setAdapter(adaper);
        sp_month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                disCertain.sendEmptyMessage(1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                disCertain.sendEmptyMessage(0);
            }
        });

        datalist3 = new ArrayList<String>();
        datalist3.add(IndexActivity.which_day + "");
        MonthDay monthDay = new MonthDay();
        for (int i = default_day; ; i--) {
            if (i == default_day && (datalist3.size() >= 28) ) {
                break;
            }
            if ((i != Integer.parseInt(IndexActivity.which_day))) {
                datalist3.add(i + "");
            }
            if (i == 1) i= monthDay.getByMonthDay(Integer.parseInt(IndexActivity.which_month)) + 1;

        }
        adaper = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, datalist3);
        sp_day.setAdapter(adaper);
        sp_day.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                disCertain.sendEmptyMessage(1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                disCertain.sendEmptyMessage(0);
            }
        });
    }

    public void setRV() {
        OkHttpClient client = new OkHttpClient();

        SharedPreferences sharedPreferences =  getActivity().getSharedPreferences("token", Context.MODE_PRIVATE);
        String sessionid = sharedPreferences.getString("sessionid", "");
        String token = sharedPreferences.getString("token","");
        Sha1 sha1 = new Sha1();
        sha1.setS(token);
        token = sha1.data;

        String  str_now_day = IndexActivity.which_year + "-" + IndexActivity.which_month + "-" + IndexActivity.which_day;
        String  the_url = MainActivity.SERVER_URL + "bills?sessionid=" + sessionid + "&token=" + token + "&start_time=" + str_now_day + "&end_time=" + str_now_day + "&version=" + getString(R.string.versionCode);

        Request request;
        request = new Request.Builder()
                .get()
                .url(the_url)
                .build();

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

                        try {
                            bills = new ArrayList<Map<String, String>>();
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

                        }

                    } else {
                        if (jsonObject.getString("msgname").equals("need_update")) {
                            disPrompt.sendEmptyMessage(1);
                        } else {
                            Msg msg = new Msg(getActivity());
                            msg.certainMsg(msg.get(jsonObject.getString("msgname")), getString(R.string.failure));
                        }
                    }
                } catch (Exception e) {

                }
            }
        };

        try {
            call.enqueue(callback);
        } catch (Exception e) {
            Msg  msg = new Msg(getContext());
            msg.certainMsg(getString(R.string.unknown_error),getString(R.string.certain));
        }


        try {
            this.recyclerView = (RecyclerView) getView().findViewById(R.id.AllF_re);
        } catch (Exception e) {

        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        AllFragment.recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)  {
        super.onActivityCreated(savedInstanceState);

        setRV();

    }

    @Override
    public void onStart() {
        super.onStart();
    }

}
