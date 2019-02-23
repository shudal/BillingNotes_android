package com.example.perci.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class Msg {
    public Map<String, String> map = new HashMap<String, String>();
    public Context context;

    Msg(Context c){
        context = c;
        map.put("username_require", context.getString(R.string.username_require));
        map.put("password_require", context.getString(R.string.password_require));
        map.put("username_too_long", context.getString(R.string.username_too_long));
        map.put("password_too_long", context.getString(R.string.password_too_long));
        map.put("username_not_exist", context.getString(R.string.username_not_exist));
        map.put("password_wrong", context.getString(R.string.password_wrong));

        map.put("invalid_request_way", context.getString(R.string.invalid_request_way));
        map.put("login_expired", context.getString(R.string.login_expired));
        map.put("login_invalid", context.getString(R.string.login_invalid));
        map.put("unknown_error", context.getString(R.string.unknown_error));
        map.put("illegal_request", context.getString(R.string.illegal_request));

        map.put("year_wrong", context.getString(R.string.year_wrong));
        map.put("month_wrong", context.getString(R.string.month_wrong));
        map.put("day_wrong", context.getString(R.string.day_wrong));

        map.put("amount_require", context.getString(R.string.amount_require));

        map.put("login_success", context.getString(R.string.login_success));
        map.put("add_success", context.getString(R.string.add_success));
        map.put("add_failed", context.getString(R.string.add_failed));

        map.put("empty", "空");

        map.put("too_quick", context.getString(R.string.too_quick));
    }

    public String get(String key) {
        String message = context.getString(R.string.unknown_error);
        try {
            message = map.get(key);

            if (message == null) {
                message =  context.getString(R.string.unknown_error);
            }

            if (message.equals(context.getString(R.string.login_expired))) {
                SharedPreferences sharedPreferences = context.getSharedPreferences("token", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("timeout", "");
                editor.commit();

                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
                builder.setTitle(context.getString(R.string.failure));

                builder.setMessage(context.getString(R.string.login_expired));

                builder.setCancelable(true);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(context, LoginIndexActivity.class);
                        context.startActivity(intent);
                    }
                });
                Looper.prepare();
                android.support.v7.app.AlertDialog dialog = builder.create();
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
            Log.v("the_message", ":" + message);
        } catch (Exception e) {
            Log.v("my_msg","error:" + e.getMessage());
        }

        return  message;
    }

    public String certainMsg(String msg, String title) {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setCancelable(true);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(context, LoginIndexActivity.class);
                context.startActivity(intent);
            }
        });
        Looper.prepare();
        android.support.v7.app.AlertDialog dialog = builder.create();
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

        return "";
    }
}
