package moe.perci.haku.BillingNotes;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.perci.myapplication.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoteF_rva extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {
    private List<HashMap> lists= new ArrayList<HashMap>();
    private Context context;

    public NoteF_rva(Context context, List<HashMap> lists) {
        this.context = context;
        this.lists = lists;
    }

    //创建View,被LayoutManager所用
    @Override
    public RecycleViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.note,parent,false);
        RecycleViewAdapter.ViewHolder holder = new RecycleViewAdapter.ViewHolder(view);
        return holder;
    }

    //数据的绑定
    @Override
    public void onBindViewHolder(RecycleViewAdapter.ViewHolder holder, int position) {
        try {
            HashMap map = lists.get(position);

        } catch (Exception e) {
            Log.v("noteF", "noteF_rva, on bind view holder error:" + e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        try {
            return lists.size();
        } catch (Exception e) {
            Log.v("noteF", "noteF_rva, get item count faield ,error:" + e.getMessage());
            return 1;
        }
    }
    //自定义ViewHolder,包含item的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView note_tag;
        public final TextView note_content;
        public ViewHolder(View itemView) {
            super(itemView);
            note_tag = (TextView) itemView.findViewById(R.id.a_note_tag);
            note_content = (TextView) itemView.findViewById(R.id.a_note_content);
        }
    }
}
