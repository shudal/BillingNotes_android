package moe.perci.haku.BillingNotes;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.perci.myapplication.R;
import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;

import org.json.JSONObject;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Calendar;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    public static String SERVER_URL;
    public static String STATIC_URL;
    public static int server_status= 0;
    public int is_prompt = -1;
    public static long versionCode;

/*
    private static final String[] m_Permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.INTERNET,Manifest.permission.ACCESS_NETWORK_STATE,Manifest.permission.SYSTEM_ALERT_WINDOW};
*/
private static final String[] m_Permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.INTERNET,Manifest.permission.ACCESS_NETWORK_STATE};
    public static void handleSSLHandshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("TLS");
            // trustAllCerts信任所有的证书
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (Exception ignored) {
        }
    }
    public  Handler serverFailedHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Intent intent = new Intent( MainActivity.this, ServerFailed.class);
            startActivity(intent);
            return true;
        }
    });

    public  Handler disPrompt=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Msg msg1 = new Msg(MainActivity.this);
            msg1.disPrompt();
            return true;
        }

    });

    public  Handler versionHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            try {
                String the_url = MainActivity.SERVER_URL + "/isPrompt?version=" + getString(R.string.versionCode);
                Request request = new Request.Builder()
                        .get()
                        .url(the_url)
                        .build();
                Cer cer = new Cer();
                OkHttpClient client = cer.getTrustAllClient();
                Call call = client.newCall(request);

                Callback callBack = new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.v("mainA","request isPormt? failed,error:" + e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = response.body().string();
                        Log.v("mainA", "versionHandler request result:" + result);
                        try {
                            JSONObject jsonObject = new JSONObject(result);

                            if (jsonObject.getInt("status") == 1) {
                                disPrompt.sendEmptyMessage(1);
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
                Log.v("mainA", "get whether update fail");
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
                try {
                    Log.v("Startjpg","set start jpg process1");
                    RequestListener mRequestListener = new RequestListener() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                            Log.d("Startjpg", "onException: " + e.toString()+"  model:"+model+" isFirstResource: "+isFirstResource);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                            Log.e("Startjpg",  "model:"+model+" isFirstResource: "+isFirstResource);
                            return false;
                        }
                    };
                    Glide.with(MainActivity.this).load(startjpg_src).listener(mRequestListener).into((ImageView) findViewById(R.id.startJpg));


                    Log.v("Startjpg","set start jpg process2");
                } catch (Exception e) {
                    Log.v("Startjpg","glide set start jpg failed,error:" + e.getMessage());
                }
            } catch (Exception e) {
                Log.v("Startjpg","setStartjpg handler failed. error:" + e.getMessage());
            }

            return true;
        }
    });

    /*
    //读写权限
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    //请求状态码
    private static int REQUEST_PERMISSION_CODE = 1;
    */
/*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                Log.i("MainActivity", "申请的权限为：" + permissions[i] + ",申请结果：" + grantResults[i]);
            }
        }
    }
*/


    private void requestCemera() {
        if (PermissionsUtil.hasPermission(this,m_Permissions)) {
            //有访问存储空间、摄像头的权限
        } else {
            PermissionsUtil.requestPermission(this, new PermissionListener() {
                @Override
                public void permissionGranted(@NonNull String[] permissions) {
                    //用户授予了访问存储空间、摄像头的权限
                }


                @Override
                public void permissionDenied(@NonNull String[] permissions) {
                    //用户拒绝了访问存储空间、摄像头的申请
                }
            }, m_Permissions);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestCemera();


        /*

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            }\
        }*/

        handleSSLHandshake();

        //得到服务器url
        Cer cer = new Cer();
        OkHttpClient client = cer.getTrustAllClient();
        String the_url = getResources().getString(R.string.SERVER_ADDRESS_TXT_URL);

        Request request = new Request.Builder()
                .get()
                .url(the_url)
                .build();
        Call call = client.newCall(request);

        Log.v("mainA","1");
        Callback callBack = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.v("mainA","call onFailure, error:" + e.getMessage());
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
        cer = new Cer();
        client = cer.getTrustAllClient();
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
                    try {
                        Toast.makeText(MainActivity.this, getString(R.string.initialing), Toast.LENGTH_SHORT);
                    } catch (Exception e) {
                        Log.v("mainAc.java", "click button when is_prompt not set, error:" + e.getMessage());
                    }
                } else {
                    Intent intent = new Intent(MainActivity.this,LoginIndexActivity.class);
                    startActivity(intent);
                }
                /*
                Intent intent = new Intent(MainActivity.this,LoginIndexActivity.class);
                startActivity(intent); */
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
