package com.example.perci.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginIndexActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_index);

        //已登陆则跳转到IndexActivity
        SharedPreferences sharedPreferences =  getSharedPreferences("token", Context.MODE_PRIVATE);
        String timeout = sharedPreferences.getString("timeout","");
        if (timeout != "") {
            Long now = Calendar.getInstance().getTimeInMillis();
            now /= 1000; //将十三位时间戳转换为十位。

            Long timeout_2 = Long.parseLong(timeout);

            if (now < timeout_2) {
                Intent intent = new Intent(this, IndexActivity.class);
                startActivity(intent);
            }
        }
    }

    public void login(View view) {
        EditText editText= (EditText) findViewById(R.id.RegisIndexUsername);
        String username=editText.getText().toString();

        editText= (EditText) findViewById(R.id.RegisIndexPassword);
        String password=editText.getText().toString();

        Sha1 sha1 = new Sha1();
        sha1.setS(password);

        password = sha1.data;

        Long timestamp_2 = Calendar.getInstance().getTimeInMillis();
        String timestamp = timestamp_2.toString();

        sha1.setS(username+password+timestamp);
        String token = sha1.data;

        OkHttpClient client = new OkHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add("username",username)
                .add("password",password)
                .add("timestamp",timestamp)
                .add("token",token)
                .build();
        final Request request = new Request.Builder()
                .url( MainActivity.SERVER_URL + "login")
                .post(formBody)
                .build();
        Call call = client.newCall(request);

        Callback callBack = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.v("my_login", "response : " + result);
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    if (jsonObject.getInt("status") == 1) {
                        SharedPreferences sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("sessionid", jsonObject.getString("sessionid"));
                        editor.putString("token", jsonObject.getString("token"));
                        editor.putString("timeout",jsonObject.getString("timeout"));
                        editor.putString("start_day", jsonObject.getString("start_day"));
                        editor.commit();

                        Intent intent = new Intent(LoginIndexActivity.this,IndexActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginIndexActivity.this);
                        builder.setTitle(getResources().getString(R.string.failure));

                        Msg msg = new Msg(LoginIndexActivity.this);

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
                    Log.e("parseJson","" + e);
                }
            }
        };
        call.enqueue(callBack);
    }
}
