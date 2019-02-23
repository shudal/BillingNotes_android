package com.example.perci.myapplication;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ServerFailed extends AppCompatActivity {
    public String server_failed_message;

    public Handler uiHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            try {
                TextView textView = (TextView) findViewById(R.id.server_failed_message);
                textView.setText(server_failed_message);
                return true;
            } catch (Exception e) {
                Log.v("serverfailedmessage","set text failed:" + e.getMessage());
                return true;
            }
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_failed);

        OkHttpClient client = new OkHttpClient();
        String the_url = getResources().getString(R.string.server_failed_message_text_url);

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
                server_failed_message = response.body().string();
                Log.v("serverfailedmessage", server_failed_message);
                uiHandler.sendEmptyMessage(1);
            }
        };
        call.enqueue(callBack);
    }
}
