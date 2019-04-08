package moe.perci.haku.BillingNotes;

import android.content.Context;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NoteFragment extends Fragment {
    public RecyclerView recycleView;
    public static Fragment noteAddF;

    public  List<HashMap> lists;

    private OnFragmentInteractionListener mListener;

    public Handler getNotesSuccess= new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            try {
                // 设置recycler view
                recycleView = (RecyclerView) getActivity().findViewById(R.id.NoteF_re);
                recycleView.setAdapter(new NoteF_rva(getActivity(), lists));

                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                recycleView.setLayoutManager(layoutManager);
                Log.v("noteF", "set recyclerview success");
            } catch (Exception e) {
                Log.v("noteF", "set getNotesSuccss error:" + e.getMessage());
            }
            return  true;
        }
    });

    public NoteFragment() {
        // Required empty public constructor
    }

    public static NoteFragment newInstance( ) {
        NoteFragment fragment = new NoteFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_note, container, false) ;

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

    // 得到笔记
    public void getNotes(String tag) {
        SharedPreferences sharedPreferences =  getActivity().getSharedPreferences("token", Context.MODE_PRIVATE);
        String sessionid = sharedPreferences.getString("sessionid", "");
        String token = sharedPreferences.getString("token","");
        Sha1 sha1 = new Sha1();
        sha1.setS(token);
        token = sha1.data;

        String  the_url = MainActivity.SERVER_URL + "notes?sessionid=" + sessionid + "&token=" + token + "&version=" + getString(R.string.versionCode);

        // 按tag搜索。
        if (!tag.equals("")) {

        }

        Cer cer = new Cer();
        OkHttpClient client = cer.getTrustAllClient();

        Request request;
        request = new Request.Builder()
                .get()
                .url(the_url)
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
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        try {
                            lists = new ArrayList<HashMap>();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                HashMap map = new HashMap();

                                map.put("content", jsonObject1.getString("content"));
                                map.put("create_time", jsonObject1.getInt("create_time"));
                                map.put("tag", jsonObject1.getString("tag"));

                                lists.add(map);

                            }

                            Log.v("noteF", lists + "");
                            getNotesSuccess.sendEmptyMessage(1);
                        } catch (Exception e) {
                            Log.v("noteF", "status = 1, but error:" +e.getMessage());
                        }
                    } else {
                        Log.v("noteF", "staus != 1");
                    }
                } catch (Exception e) {
                    Log.v("noteF","parse json failed, error:" + e.getMessage());
                }
            }
        };
        call.enqueue(callBack);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try {
            getNotes("");
        } catch (Exception e) {

        }

        try {

            noteAddF = NoteAddFragment.newInstance();

            // toolbar事件监听
            TextView textView = getActivity().findViewById(R.id.NoteF_t_add);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.home_container, noteAddF).commit();
                }
            });

            TextView refresh = getActivity().findViewById(R.id.NoteF_t_refresh);
            refresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.home_container, IndexActivity.noteF).commit();
                }
            });

        } catch (Exception e) {
            Log.v("noteF", "set recycler view failed, error: " +e.getMessage());
        }
    }
}
