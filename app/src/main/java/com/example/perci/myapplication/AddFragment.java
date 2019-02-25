package com.example.perci.myapplication;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.support.constraint.Constraints.TAG;

public class AddFragment extends Fragment {
    public String AddF_year;
    public String AddF_month;
    public String AddF_day;

    public int default_year;
    public int default_month;
    public int default_day;

    public String addStatusMessage;
    public String addFailedMessage;

    private OnFragmentInteractionListener mListener;

    public Handler uiHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            AlertDialog dialog = builder.create();
            View dialogView = View.inflate(getContext(), R.layout.alert_status, null);

            TextView textView1 = (TextView) dialogView.findViewById(R.id.alertStatus_content);
            if (addStatusMessage.equals(getString(R.string.success))) {
                textView1.setText(getString(R.string.add_success));
            }  else {
                textView1.setText(addFailedMessage);
            }

            dialog.setView(dialogView);
            dialog.show();
            Window window = dialog.getWindow();
            //这一句消除白块
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            Button button = (Button) dialogView.findViewById(R.id.alertStatus_certain);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.dismiss();

                    if (addStatusMessage.equals(getString(R.string.success))) {
                        EditText editText = (EditText) getActivity().findViewById(R.id.AddP_main);
                        editText.setText("");

                        editText = (EditText) getActivity().findViewById(R.id.AddP_sum);
                        editText.setText("");

                        editText = (EditText) getActivity().findViewById(R.id.AddF_content);
                        editText.setText("");
                    }
                }
            });

            return true;
        }
    });

    public AddFragment() {
        // Required empty public constructor
    }

    public static AddFragment newInstance() {
        AddFragment fragment = new AddFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v =  inflater.inflate(R.layout.fragment_add, container, false);

        //选择 其他 时显示输入框
        RadioGroup radioGroup = (RadioGroup) v.findViewById(R.id.AddF_rg_2);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                EditText editText = (EditText) v.findViewById(R.id.AddF_other);
                if (checkedId == R.id.radioButton) {
                    editText.setVisibility(View.VISIBLE);
                } else {
                    editText.setVisibility(View.GONE);
                }
            }
        });


        //设置默认时间
        Calendar cal = Calendar.getInstance();
        default_year = cal.get(Calendar.YEAR);
        default_month = cal.get(Calendar.MONTH) + 1;
        default_day = cal.get(Calendar.DAY_OF_MONTH);
        AddF_year = default_year + "";
        AddF_month = default_month + "";
        AddF_day= default_day + "";


        Spinner spinner = (Spinner) v.findViewById(R.id.AddF_sp_year);
        final ArrayList datalist1 = new ArrayList<String>();
        datalist1.add("" + default_year);
        datalist1.add("" + (default_year - 1) );
        SpinnerAdapter adaper = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, datalist1);
        spinner.setAdapter(adaper);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AddF_year = datalist1.get(position) + "";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner = (Spinner) v.findViewById(R.id.AddF_sp_month);
        final ArrayList datalist2 = new ArrayList<String>();
        datalist2.add(default_month + "");
        for (int i = 1; i <= 12; i++) {
            if (i != default_month) {
                datalist2.add(i + "");
            }
        }
        adaper = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, datalist2);
        spinner.setAdapter(adaper);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AddF_month = datalist2.get(position) + "";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner = (Spinner) v.findViewById(R.id.AddF_sp_day);
        final ArrayList datalist3 = new ArrayList<String>();
        for (int i = default_day ; i >= default_day - 1; i--) {
            datalist3.add(i + "");
        }
        adaper = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, datalist3);
        spinner.setAdapter(adaper);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AddF_day = datalist3.get(position) + "";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    @Override
    public  void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try {

            Button submit = (Button) getActivity().findViewById(R.id.AddF_submit);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView editText = (EditText) getActivity().findViewById(R.id.AddF_other);
                    String other = editText.getText().toString();

                    editText = (EditText) getActivity().findViewById(R.id.AddP_main);
                    String main = editText.getText().toString();

                    editText = (EditText) getActivity().findViewById(R.id.AddP_sum);
                    String sum = editText.getText().toString();

                    editText = (EditText) getActivity().findViewById(R.id.AddF_content);
                    String content = editText.getText().toString();

                    RadioGroup group_demo = (RadioGroup) getActivity().findViewById(R.id.AddF_rg_1);
                    RadioButton checkRadioButton = (RadioButton) group_demo.findViewById(group_demo.getCheckedRadioButtonId());
                    String io = checkRadioButton.getText().toString();
                    if (io == getResources().getString(R.string.use)) {
                        io = "out";
                    } else {
                        io = "get";
                    }

                    group_demo = (RadioGroup) getActivity().findViewById(R.id.AddF_rg_2);
                    checkRadioButton = (RadioButton) group_demo.findViewById(group_demo.getCheckedRadioButtonId());
                    String form = checkRadioButton.getText().toString();
                    if (form == getResources().getString(R.string.other)) {
                        form = "other";
                        if (other.equals("")) {
                            other = getString(R.string.other);
                        }
                    }

                    Log.v("my_add", "io:" + io + "  form:" + form);
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("token", Context.MODE_PRIVATE);
                    String sessionid = sharedPreferences.getString("sessionid", "");
                    String token = sharedPreferences.getString("token", "");
                    Sha1 sha1 = new Sha1();
                    sha1.setS(token);

                    token = sha1.data;

                    try {
                        OkHttpClient client = new OkHttpClient();
                        FormBody formBody = new FormBody.Builder()
                                .add("year", AddF_year)
                                .add("month", AddF_month)
                                .add("day", AddF_day)
                                .add("io", io)
                                .add("form", form)
                                .add("other", other)
                                .add("main", main)
                                .add("sum", sum)
                                .add("content", content)
                                .add("sessionid", sessionid)
                                .add("token", token)
                                .add("version", getString(R.string.versionCode))
                                .build();

                        final Request request = new Request.Builder()
                                .url(MainActivity.SERVER_URL + "add")
                                .post(formBody)
                                .build();
                        Call call = client.newCall(request);

                        Callback callBack = new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.v("my_request", "call fail");
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String result = response.body().string();
                                Log.v("requestadd", "result : " + result);
                                try {
                                    JSONObject jsonObject = new JSONObject(result);

                                    Msg msg = new Msg(getContext());

                                    if (jsonObject.getInt("status") == 1) {
                                        addStatusMessage = getString(R.string.success);
                                    } else {
                                        addStatusMessage = getString(R.string.failure);
                                        addFailedMessage = msg.get(jsonObject.getString("msgname"));
                                    }


                                    uiHandler.sendEmptyMessage(1);
                                } catch (Exception e) {
                                    Log.v("my_request", "add,parse json failed,error messag:" + e.getMessage());
                                }
                            }
                        };
                        call.enqueue(callBack);
                    } catch (Exception e) {
                        try {
                            Toast.makeText(getContext(), getString(R.string.initialing), Toast.LENGTH_SHORT).show();
                        } catch (Exception e2) {

                        }
                    }
                }
            });
        } catch (Exception e) {
            Toast.makeText(getContext(), getString(R.string.initialing), Toast.LENGTH_SHORT).show();
        }
    }
}
