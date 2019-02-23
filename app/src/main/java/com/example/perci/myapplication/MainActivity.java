package com.example.perci.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    public static String SERVER_URL;
    public static String STATIC_URL;
    public static int server_status= 0;

    public  Handler serverFailedHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Intent intent = new Intent( MainActivity.this, ServerFailed.class);
            startActivity(intent);
            return true;
        }
    });

    public  Handler setStartjpg =new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            try {
                String startjpg_src = MainActivity.STATIC_URL + "img/start.jpg";
                Log.v("Startjpg", startjpg_src);
                Glide.with(MainActivity.this).load(startjpg_src).into((ImageView) findViewById(R.id.startJpg));
            } catch (Exception e) {

            }

            return true;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //得到服务器url
        OkHttpClient client = new OkHttpClient();
        String the_url = getResources().getString(R.string.SERVER_ADDRESS_TXT_URL);

        Request request = new Request.Builder()
                .get()
                .url(the_url)
                .build();
        Call call = client.newCall(request);

        Callback callBack = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                result = result.trim();

                Log.v("serveraddress", result);
                if ( result.equals("none") ) {
                    serverFailedHandler.sendEmptyMessage(1);
                    Log.v("serveraddress", "equals!");
                } else {
                    MainActivity.SERVER_URL  = result;
                    Log.v("serveraddress", "|"+result + "|equal?|" + result.equals("none"));
                }
            }
        };
        call.enqueue(callBack);

        //得到静态资源url
        the_url = getResources().getString(R.string.static_url_txt);
        request = new Request.Builder()
                .get()
                .url(the_url)
                .build();
        client = new OkHttpClient();
        call = client.newCall(request);

        callBack = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                result = result.trim();
                MainActivity.STATIC_URL  = result;
                Log.v("my_static", "set static url success! result:" + result);
                setStartjpg.sendEmptyMessage(1);
            }
        };
        call.enqueue(callBack);

        SharedPreferences sharedPreferences =  getSharedPreferences("token", Context.MODE_PRIVATE);

        //已登陆则跳转到IndexActivity
        String timeout = sharedPreferences.getString("timeout","");
        if (timeout != "") {
            Long now = Calendar.getInstance().getTimeInMillis();
            now /= 1000; //将十三位时间戳转换为十位。

            Long timeout_2 = Long.parseLong(timeout);

            if (now < timeout_2) {
                Intent intent = new Intent(this, IndexActivity.class);
                startActivity(intent);
                finish();
            }
        }

    }

    public void loginIndex(View view) {
        Intent intent = new Intent(this,LoginIndexActivity.class);
        startActivity(intent);
    }
    public void regisIndex(View view) {
        Intent intent = new Intent(this,RegisIndexActivity.class);
        startActivity(intent);
    }
}
