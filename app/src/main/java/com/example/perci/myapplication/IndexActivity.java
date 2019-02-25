package com.example.perci.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.bottomappbar.BottomAppBar;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hjm.bottomtabbar.BottomTabBar;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class IndexActivity extends AppCompatActivity implements  AddFragment.OnFragmentInteractionListener, AllFragment.OnFragmentInteractionListener, MeFragment.OnFragmentInteractionListener{
    private BottomNavigationView mBottomNavigationView;

    public static String which_year;
    public static String which_month;
    public static String which_day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        Fragment addF = AddFragment.newInstance();
        Fragment allF = AllFragment.newInstance();
        Fragment meF  = MeFragment.newInstance();

        getSupportFragmentManager().beginTransaction().replace(R.id.home_container, AddFragment.newInstance()).commit();
        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_view);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (MainActivity.SERVER_URL == null) {
                    Toast.makeText(getApplicationContext(), getString(R.string.initialing), Toast.LENGTH_SHORT).show();
                    return true;
                }

                Fragment fragment = null;
                switch (item.getItemId()){
                    case R.id.tab_menu_add:
                        fragment = addF;
                        break;
                    case R.id.tab_menu_all:
                        fragment = allF;
                        break;

                    case R.id.tab_menu_me:
                        fragment = meF;
                        break;
                }
                if(fragment!=null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.home_container,fragment).commit();
                }
                return true;
            }
        });

        Calendar cal = Calendar.getInstance();
        int default_year = cal.get(Calendar.YEAR);
        int default_month = cal.get(Calendar.MONTH) + 1;
        int default_day = cal.get(Calendar.DAY_OF_MONTH);

        IndexActivity.which_year = default_year + "";
        IndexActivity.which_month  = default_month + "";
        IndexActivity.which_day = default_day + "";
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}
