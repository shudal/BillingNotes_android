package moe.perci.haku.BillingNotes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import android.view.Window;


import com.example.perci.myapplication.R;

public class MeFragment extends Fragment {
    public static String nickname;
    public static int start_day;
    public static int month_fee;

    public String changeStatusMessage = "";

    private MeFragment.OnFragmentInteractionListener mListener;

    public Handler finishAc = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("token", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("timeout", "");
            editor.commit();

            Intent intent = new Intent(getContext(), LoginIndexActivity.class);
            startActivity(intent);

            getActivity().finish();

            return  true;
        }
    });

    public Handler uiHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            try {
                TextView textView = (TextView) getActivity().findViewById(R.id.MeF_nickname);
                textView.setText(" " + MeFragment.nickname);

                textView = (TextView) getActivity().findViewById(R.id.MeF_start_day);
                textView.setText(" " + MeFragment.start_day);

                textView = (TextView) getActivity().findViewById(R.id.MeF_month_fee);
                textView.setText(" " + MeFragment.month_fee);
            } catch (Exception e) {
                try {
                    Toast.makeText(getContext(), getString(R.string.initialing), Toast.LENGTH_SHORT).show();
                } catch (Exception e2) {

                }
            }
            return true;
        }

    });

    public Handler changeNickHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Toast.makeText(getContext(), changeStatusMessage, Toast.LENGTH_SHORT).show();

            if (changeStatusMessage.equals(getString(R.string.success))) {
                TextView textView = (TextView) getActivity().findViewById(R.id.MeF_nickname);
                textView.setText(MeFragment.nickname);
            }

            return true;
        }

    });

    public Handler changeSdHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Toast.makeText(getContext(), changeStatusMessage, Toast.LENGTH_SHORT).show();

            if (changeStatusMessage.equals(getString(R.string.success))) {
                TextView textView = (TextView) getActivity().findViewById(R.id.MeF_start_day);
                textView.setText(MeFragment.start_day + "");
            }

            return true;
        }

    });

    public Handler changeMfHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Toast.makeText(getContext(), changeStatusMessage, Toast.LENGTH_SHORT).show();

            if (changeStatusMessage.equals(getString(R.string.success))) {
                TextView textView = (TextView) getActivity().findViewById(R.id.MeF_month_fee);
                textView.setText(MeFragment.month_fee + "");
            }

            return true;
        }

    });

    public Handler FbHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Toast.makeText(getContext(), changeStatusMessage, Toast.LENGTH_SHORT).show();
            return true;
        }

    });

    public MeFragment() {
        // Required empty public constructor
    }

    public static MeFragment newInstance() {
        MeFragment fragment = new MeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_me, container, false);

        TextView textView12 = (TextView) v.findViewById(R.id.MeF_t_return);
        textView12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.home_container, IndexActivity.allF).commit();
            }
        });

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


        SharedPreferences sharedPreferences =  getActivity().getSharedPreferences("token", Context.MODE_PRIVATE);
        String sessionid = sharedPreferences.getString("sessionid", "");
        String token = sharedPreferences.getString("token","");
        Sha1 sha1 = new Sha1();
        sha1.setS(token);
        token = sha1.data;

        try {
            ConstraintLayout constraintLayout = getActivity().findViewById(R.id.MeF_c_nickname);
            constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    AlertDialog dialog = builder.create();
                    View dialogView = View.inflate(getContext(), R.layout.change_meta, null);
                    EditText editText = (EditText) dialogView.findViewById(R.id.changeMeta_meta);
                    editText.setText(MeFragment.nickname);

                    TextView textView = (TextView) dialogView.findViewById(R.id.alertStatus_title);
                    textView.setText(getString(R.string.nickname));

                    dialog.setView(dialogView);
                    dialog.show();
                    Window window = dialog.getWindow();
                    //这一句消除白块
                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    Button btnSubmit = (Button) dialogView.findViewById(R.id.alertStatus_certain);
                    Button btnCancel = (Button) dialogView.findViewById(R.id.changeMeta_cancle);

                    btnSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Toast.makeText(getContext() , "提交", Toast.LENGTH_SHORT).show();
                            String newUsername = editText.getText().toString();

                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("token", Context.MODE_PRIVATE);
                            String sessionid = sharedPreferences.getString("sessionid", "");
                            String token = sharedPreferences.getString("token", "");
                            Sha1 sha1 = new Sha1();
                            sha1.setS(token);
                            token = sha1.data;

                            Cer cer = new Cer();
                            OkHttpClient client = cer.getTrustAllClient();
                            FormBody formBody = new FormBody.Builder()
                                    .add("sessionid", sessionid)
                                    .add("token", token)
                                    .add("nickname", newUsername)
                                    .build();
                            final Request request = new Request.Builder()
                                    .url(MainActivity.SERVER_URL + "usermeta/changeNick")
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
                                            MeFragment.nickname = newUsername;
                                            changeStatusMessage = getString(R.string.success);
                                        } else {
                                            changeStatusMessage = getString(R.string.failure);
                                        }
                                    } catch (Exception e) {
                                        Log.e("parseJson", "" + e);

                                        changeStatusMessage = getString(R.string.failure);
                                    }

                                    dialog.dismiss();
                                    changeNickHandler.sendEmptyMessage(1);
                                }
                            };
                            call.enqueue(callBack);
                        }
                    });

                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }
            });

            constraintLayout = getActivity().findViewById(R.id.MeF_c_start_day);
            constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    AlertDialog dialog = builder.create();
                    View dialogView = View.inflate(getContext(), R.layout.change_meta, null);
                    EditText editText = (EditText) dialogView.findViewById(R.id.changeMeta_meta);
                    editText.setText(MeFragment.start_day + "");
                    TextView textView = (TextView) dialogView.findViewById(R.id.alertStatus_title);
                    textView.setText(getString(R.string.start_day));
                    dialog.setView(dialogView);
                    dialog.show();
                    Window window = dialog.getWindow();
                    //这一句消除白块
                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    Button btnSubmit = (Button) dialogView.findViewById(R.id.alertStatus_certain);
                    Button btnCancel = (Button) dialogView.findViewById(R.id.changeMeta_cancle);

                    btnSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Toast.makeText(getContext() , "提交", Toast.LENGTH_SHORT).show();
                            String start_day = editText.getText().toString();

                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("token", Context.MODE_PRIVATE);
                            String sessionid = sharedPreferences.getString("sessionid", "");
                            String token = sharedPreferences.getString("token", "");
                            Sha1 sha1 = new Sha1();
                            sha1.setS(token);
                            token = sha1.data;

                            Cer cer = new Cer();
                            OkHttpClient client = cer.getTrustAllClient();
                            FormBody formBody = new FormBody.Builder()
                                    .add("sessionid", sessionid)
                                    .add("token", token)
                                    .add("start_day", start_day)
                                    .build();
                            final Request request = new Request.Builder()
                                    .url(MainActivity.SERVER_URL + "usermeta/changeSd")
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
                                            MeFragment.start_day = Integer.parseInt(start_day);
                                            changeStatusMessage = getString(R.string.success);
                                        } else {
                                            changeStatusMessage = getString(R.string.failure);
                                        }
                                    } catch (Exception e) {
                                        Log.e("parseJson", "" + e);

                                        changeStatusMessage = getString(R.string.failure);
                                    }

                                    dialog.dismiss();
                                    changeSdHandler.sendEmptyMessage(1);
                                }
                            };
                            call.enqueue(callBack);
                        }
                    });

                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }
            });

            constraintLayout = getActivity().findViewById(R.id.MeF_c_month_fee);
            constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    AlertDialog dialog = builder.create();
                    View dialogView = View.inflate(getContext(), R.layout.change_meta, null);
                    EditText editText = (EditText) dialogView.findViewById(R.id.changeMeta_meta);
                    editText.setText(MeFragment.month_fee + "");
                    TextView textView = (TextView) dialogView.findViewById(R.id.alertStatus_title);
                    textView.setText(getString(R.string.month_fee));
                    dialog.setView(dialogView);
                    dialog.show();
                    Window window = dialog.getWindow();
                    //这一句消除白块
                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    Button btnSubmit = (Button) dialogView.findViewById(R.id.alertStatus_certain);
                    Button btnCancel = (Button) dialogView.findViewById(R.id.changeMeta_cancle);

                    btnSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Toast.makeText(getContext() , "提交", Toast.LENGTH_SHORT).show();
                            String newMf = editText.getText().toString();

                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("token", Context.MODE_PRIVATE);
                            String sessionid = sharedPreferences.getString("sessionid", "");
                            String token = sharedPreferences.getString("token", "");
                            Sha1 sha1 = new Sha1();
                            sha1.setS(token);
                            token = sha1.data;

                            Cer cer = new Cer();
                            OkHttpClient client = cer.getTrustAllClient();
                            FormBody formBody = new FormBody.Builder()
                                    .add("sessionid", sessionid)
                                    .add("token", token)
                                    .add("month_fee", newMf)
                                    .build();
                            final Request request = new Request.Builder()
                                    .url(MainActivity.SERVER_URL + "usermeta/changeMf")
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
                                            MeFragment.month_fee = Integer.parseInt(newMf);
                                            changeStatusMessage = getString(R.string.success);
                                        } else {
                                            changeStatusMessage = getString(R.string.failure);
                                        }
                                    } catch (Exception e) {
                                        Log.e("parseJson", "" + e);
                                        changeStatusMessage = getString(R.string.failure);
                                    }

                                    dialog.dismiss();
                                    changeMfHandler.sendEmptyMessage(1);
                                }
                            };
                            call.enqueue(callBack);
                        }
                    });

                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }
            });

            constraintLayout = getActivity().findViewById(R.id.MeF_c_feedback);
            constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    AlertDialog dialog = builder.create();
                    View dialogView = View.inflate(getContext(), R.layout.change_meta, null);
                    EditText editText = (EditText) dialogView.findViewById(R.id.changeMeta_meta);
                    editText.setSingleLine(false);
                    TextView textView = (TextView) dialogView.findViewById(R.id.alertStatus_title);
                    textView.setText(getString(R.string.feedback));
                    dialog.setView(dialogView);
                    dialog.show();

                    Window window = dialog.getWindow();
                    //这一句消除白块
                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    Button btnSubmit = (Button) dialogView.findViewById(R.id.alertStatus_certain);
                    Button btnCancel = (Button) dialogView.findViewById(R.id.changeMeta_cancle);

                    btnSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Toast.makeText(getContext() , "提交", Toast.LENGTH_SHORT).show();
                            String feedback = editText.getText().toString();

                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("token", Context.MODE_PRIVATE);
                            String sessionid = sharedPreferences.getString("sessionid", "");
                            String token = sharedPreferences.getString("token", "");
                            Sha1 sha1 = new Sha1();
                            sha1.setS(token);
                            token = sha1.data;

                            Cer cer = new Cer();
                            OkHttpClient client = cer.getTrustAllClient();
                            FormBody formBody = new FormBody.Builder()
                                    .add("sessionid", sessionid)
                                    .add("token", token)
                                    .add("feedback", feedback)
                                    .build();
                            final Request request = new Request.Builder()
                                    .url(MainActivity.SERVER_URL + "feedback/add")
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
                                            changeStatusMessage = getString(R.string.success);
                                        } else {
                                            changeStatusMessage = getString(R.string.failure);
                                        }
                                    } catch (Exception e) {
                                        Log.e("parseJson", "" + e);
                                        changeStatusMessage = getString(R.string.failure);
                                    }

                                    dialog.dismiss();
                                    FbHandler.sendEmptyMessage(1);
                                }
                            };
                            call.enqueue(callBack);
                        }
                    });

                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }
            });

            constraintLayout = getActivity().findViewById(R.id.MeF_c_state);
            constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    AlertDialog dialog = builder.create();
                    View dialogView = View.inflate(getContext(), R.layout.user_state, null);

                    WebView webView = (WebView) dialogView.findViewById(R.id.user_state_web);
                    webView.setWebViewClient(new WebViewClient() {
                        @Override
                        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//                super.onReceivedSslError(view, handler, error);
                            //handler.cancel();// super中默认的处理方式，WebView变成空白页
                            if (handler != null) {
                                handler.proceed();//忽略证书的错误继续加载页面内容，不会变成空白页面
                            }
                        }
                    });

                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.getSettings().setDomStorageEnabled(true);
                    webView.loadUrl(MainActivity.SERVER_URL + "/user_state");

                    dialog.setView(dialogView);
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
                }
            });



            constraintLayout = getActivity().findViewById(R.id.MeF_c_logout);
            constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    AlertDialog dialog = builder.create();
                    View dialogView = View.inflate(getContext(), R.layout.change_meta, null);
                    EditText editText = (EditText) dialogView.findViewById(R.id.changeMeta_meta);
                    editText.setText(getString(R.string.logout) + "?");
                    editText.setFocusable(false);
                    editText.setFocusableInTouchMode(false);

                    TextView textView = (TextView) dialogView.findViewById(R.id.alertStatus_title);
                    textView.setText(getString(R.string.tip));
                    dialog.setView(dialogView);
                    dialog.show();

                    Window window = dialog.getWindow();
                    //这一句消除白块
                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    Button btnSubmit = (Button) dialogView.findViewById(R.id.alertStatus_certain);
                    Button btnCancel = (Button) dialogView.findViewById(R.id.changeMeta_cancle);

                    btnSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("token", Context.MODE_PRIVATE);
                            String sessionid = sharedPreferences.getString("sessionid", "");
                            String token = sharedPreferences.getString("token", "");
                            Sha1 sha1 = new Sha1();
                            sha1.setS(token);
                            token = sha1.data;

                            Cer cer = new Cer();
                            OkHttpClient client = cer.getTrustAllClient();
                            FormBody formBody = new FormBody.Builder()
                                    .add("sessionid", sessionid)
                                    .add("token", token)
                                    .build();
                            final Request request = new Request.Builder()
                                    .url(MainActivity.SERVER_URL + "logout")
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
                                            changeStatusMessage = getString(R.string.success);
                                        } else {
                                            changeStatusMessage = getString(R.string.failure);
                                        }
                                    } catch (Exception e) {
                                        Log.e("parseJson", "" + e);
                                        changeStatusMessage = getString(R.string.failure);
                                    }

                                    dialog.dismiss();
                                    FbHandler.sendEmptyMessage(1);

                                    if (changeStatusMessage.equals(getString(R.string.success))) {
                                        finishAc.sendEmptyMessage(1);
                                    }
                                }
                            };
                            call.enqueue(callBack);
                        }
                    });

                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }
            });

            Cer cer = new Cer();
            OkHttpClient client = cer.getTrustAllClient();

            String the_url = MainActivity.SERVER_URL + "usermeta?sessionid=" + sessionid + "&token=" + token;

            Request request = new Request.Builder()
                    .get()
                    .url(the_url)
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    String result = response.body().string();
                    Log.v("my_me", "results: " + result);
                    try {

                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.getInt("status") == 1) {
                            try {
                                MeFragment.nickname = jsonObject.getString("nickname");
                                MeFragment.start_day = jsonObject.getInt("start_day");
                                MeFragment.month_fee = jsonObject.getInt("month_fee");

                                uiHandler.sendEmptyMessage(1);
                            } catch (Exception e) {

                            }
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
                        Log.v("my_me", "parse json failed ");
                    }
                }
            });
        } catch (Exception e) {
            try {
                Toast.makeText(getContext(), getString(R.string.initialing), Toast.LENGTH_SHORT).show();
            } catch (Exception e2) {

            }
        }
    }
}
