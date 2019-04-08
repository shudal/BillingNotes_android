package moe.perci.haku.BillingNotes;

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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.perci.myapplication.R;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class NoteAddFragment extends Fragment {
    public RecyclerView recycleView;

    public String note_tag = "";
    public String note_content = "";

    public String disCertain_msg = "";
    public String disCertain_title =  "";

    private OnFragmentInteractionListener mListener;


    public Handler add_note = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

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
                    .add("version", getString(R.string.versionCode))
                    .add("content", note_content)
                    .add("tag", note_tag)
                    .build();
            final Request request = new Request.Builder()
                    .url(MainActivity.SERVER_URL + "note/add")
                    .post(formBody)
                    .build();
            Call call = client.newCall(request);

            Callback callBack = new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.v("noteAddF", "call on failure");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = response.body().string();
                    Log.v("noteAddF", "result:" + result);
                    try {
                        JSONObject jsonObject = new JSONObject(result);

                        if (jsonObject.getInt("status") == 1) {
                             disCertain_msg = getString(R.string.add) + getString(R.string.success);
                             disCertain_title = getString(R.string.success);
                             disCertain.sendEmptyMessage(1);

                             // 清空已经输入的
                             addSuccess.sendEmptyMessage(1);

                        } else {
                            disCertain_msg = getString(R.string.add) + getString(R.string.failure);
                            disCertain_title = getString(R.string.failure);
                            disCertain.sendEmptyMessage(1);
                        }
                    } catch (Exception e) {

                    }
                }
            };
            call.enqueue(callBack);

            return  true;
        }
    });

    public Handler disCertain = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());
            builder.setTitle(disCertain_title);
            builder.setMessage(disCertain_msg);
            builder.setCancelable(true);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            android.support.v7.app.AlertDialog dialog = builder.create();
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                }
            });
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                }
            });
            dialog.show();
            return  true;
        }
    });

    public Handler addSuccess = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            note_content = "";
            note_tag      = "";

            // 清空已输入
            TextView textView4 = getActivity().findViewById(R.id.note_add_content);
            textView4.setText("");

            textView4 = getActivity().findViewById(R.id.note_add_tag);
            textView4.setText("");

            textView4 = getActivity().findViewById(R.id.note_add_tags);
            textView4.setText("");

            return  true;
        }
    });

    public NoteAddFragment() {

    }

    public static Fragment newInstance( ) {
        Fragment fragment = new NoteAddFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_note_add, container, false) ;

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
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // 工具栏返回
        TextView textView = getActivity().findViewById(R.id.NoteAddF_b_return);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.home_container, IndexActivity.noteF).commit();
            }
        });

        // 新增便签
        TextView textView1 = getActivity().findViewById(R.id.note_add_add);
        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView2 = getActivity().findViewById(R.id.note_add_tag);
                String the_text = textView2.getText().toString();
                if (the_text.equals("")) {
                    disCertain_msg  = getString(R.string.tag) + getString(R.string.cannot_be_null);
                    disCertain_title =  getString(R.string.failure);
                    disCertain.sendEmptyMessage(1);
                } else {
                    note_tag = note_tag  + " " + the_text;
                    TextView textView3 = getActivity().findViewById(R.id.note_add_tags);
                    textView3.setText(note_tag);

                    // 清空已输入
                    textView2.setText("");
                }
            }
        });

        // 提交便签
        TextView textView2 = getActivity().findViewById(R.id.note_add_submit);
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView3 = getActivity().findViewById(R.id.note_add_content);
                String the_text = textView3.getText().toString();
                if (the_text.equals("")) {
                    disCertain_msg = getString(R.string.enter_sticky_notes_content) + getString(R.string.cannot_be_null);
                    disCertain_title = getString(R.string.failure);
                    disCertain.sendEmptyMessage(1);
                } else {
                    // 添加到服务器
                    note_content = the_text;
                    add_note.sendEmptyMessage(1);
                }
            }
        });


    }
}
