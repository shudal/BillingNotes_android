package com.example.perci.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    public int is_prompt = -1;
    public static long versionCode;

    public  Handler serverFailedHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Intent intent = new Intent( MainActivity.this, ServerFailed.class);
            startActivity(intent);
            return true;
        }
    });

    public  Handler versionHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            try {
                String the_url = MainActivity.SERVER_URL + "/isPrompt";
                Request request = new Request.Builder()
                        .get()
                        .url(the_url)
                        .build();
                OkHttpClient client = new OkHttpClient();
                Call call = client.newCall(request);

                Callback callBack = new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = response.body().string();
                        Log.v("mainA", "versionHandler request result:" + result);
                        try {
                            JSONObject jsonObject = new JSONObject(result);

                            if (jsonObject.getInt("status") == 1) {
                                alertPrompt.sendEmptyMessage(1);
                                is_prompt = 1;
                            } else {
                                is_prompt = 0;

                                SharedPreferences sharedPreferences =  getSharedPreferences("token", Context.MODE_PRIVATE);

                                //已登陆则跳转到IndexActivity
                                String timeout = sharedPreferences.getString("timeout","");
                                if (timeout != "") {
                                    Long now = Calendar.getInstance().getTimeInMillis();
                                    now /= 1000; //将十三位时间戳转换为十位。

                                    Long timeout_2 = Long.parseLong(timeout);

                                    if (now < timeout_2) {
                                        Intent intent = new Intent(MainActivity.this, IndexActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            }
                        } catch (Exception e) {
                            Log.v("mainA", "parse whether update failed,error:" + e.getMessage());
                        }
                    }
                };
                call.enqueue(callBack);
            } catch (Exception e) {
                Log.v("MainA", "get whether update fail");
            }

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

    public  Handler alertPrompt =new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            try {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                AlertDialog dialog = builder.create();
                View dialogView = View.inflate(MainActivity.this, R.layout.user_state, null);

                WebView webView = (WebView) dialogView.findViewById(R.id.user_state_web);
                webView.setWebViewClient(new WebViewClient() {

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        return true;
                    }
                });

                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setDomStorageEnabled(true);
                webView.getSettings().setBuiltInZoomControls(true);
                webView.loadUrl(MainActivity.SERVER_URL + "/prompt");

                dialog.setView(dialogView);
                dialog.setCancelable(false);
                dialog.show();

                Window window = dialog.getWindow();
                //这一句消除白块
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                Button btnSubmit = (Button) dialogView.findViewById(R.id.user_state_certain);

                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            } catch (Exception e) {
                Log.v("mainA","alertPromt failed,error:" + e.getMessage());
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
                    versionHandler.sendEmptyMessage(1);
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

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_prompt == -1) {
                    Toast.makeText(MainActivity.this, getString(R.string.initialing), Toast.LENGTH_SHORT);
                } else {
                    Intent intent = new Intent(MainActivity.this,LoginIndexActivity.class);
                    startActivity(intent);
                }
            }
        });

        button = (Button) findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_prompt == -1) {
                    Toast.makeText(MainActivity.this, getString(R.string.initialing), Toast.LENGTH_SHORT);
                } else {
                    Intent intent = new Intent(MainActivity.this,RegisIndexActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}
