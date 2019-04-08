package moe.perci.haku.BillingNotes;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.perci.myapplication.R;

import java.util.Calendar;

public class IndexActivity extends AppCompatActivity implements  AddFragment.OnFragmentInteractionListener, AllFragment.OnFragmentInteractionListener, MeFragment.OnFragmentInteractionListener, NoteFragment.OnFragmentInteractionListener, NoteAddFragment.OnFragmentInteractionListener{
    private BottomNavigationView mBottomNavigationView;

    public static String which_year;
    public static String which_month;
    public static String which_day;

    public static Fragment addF;
    public static Fragment allF;
    public static Fragment meF;
    public static Fragment noteF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        addF = AddFragment.newInstance();
        allF = AllFragment.newInstance();
        meF  = MeFragment.newInstance();
        noteF = NoteFragment.newInstance();

        getSupportFragmentManager().beginTransaction().replace(R.id.home_container, noteF).commit();
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
                        fragment = noteF;
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
