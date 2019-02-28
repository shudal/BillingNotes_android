package moe.perci.haku.BillingNotes;

import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.perci.myapplication.R;

import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RegisIndexActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regis_index);
    }

    public boolean regis(View view) {
        Msg msg = new Msg(this);

        EditText editText = (EditText) findViewById(R.id.RegisIndexMail);
        String mail = editText.getText().toString();

        String REGEX="^\\w+((-\\w+)|(\\.\\w+))*@\\w+(\\.\\w{2,3}){1,3}$";
        Pattern p = Pattern.compile(REGEX);
        Matcher matcher=p.matcher(mail);

        if (!matcher.matches()) {
            Toast.makeText(getApplicationContext(), getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
            return false;
        }

        editText = (EditText) findViewById(R.id.RegisIndexPassword);
        String password = editText.getText().toString();
        if (password.equals("") ) {
            Toast.makeText(getApplicationContext(), getString(R.string.password) + getString(R.string.cannot_be_null) , Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.length() > 10) {
            Toast.makeText(getApplicationContext(), getString(R.string.password_too_long) , Toast.LENGTH_SHORT).show();
            return false;
        }

        Sha1 sha1 = new Sha1();
        sha1.setS(password);
        password = sha1.data;


        editText = (EditText) findViewById(R.id.RegisIndexUsername);
        String username = editText.getText().toString();
        if (username.equals("") ) {
            Toast.makeText(getApplicationContext(), getString(R.string.username) + getString(R.string.cannot_be_null), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (username.length() > 10) {
            Toast.makeText(getApplicationContext(), getString(R.string.username_too_long), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (username.length() < 5) {
            Toast.makeText(getApplicationContext(), getString(R.string.username) + getString(R.string.too_short), Toast.LENGTH_SHORT).show();
            return false;
        }

        editText = (EditText) findViewById(R.id.editText);
        String nickname = editText.getText().toString();

        Cer cer = new Cer();
        OkHttpClient client = cer.getTrustAllClient();
        FormBody formBody = new FormBody.Builder()
                .add("username",username)
                .add("password",password)
                .add("email", mail)
                .add("nickname", nickname)
                .build();
        final Request request = new Request.Builder()
                .url( MainActivity.SERVER_URL + "regis")
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
                Log.v("my_regis", "response : " + result);
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    Looper.prepare();
                    if (jsonObject.getInt("status") == 1) {
                        Toast.makeText(getApplicationContext(), getString(R.string.plaese_confirm_email), Toast.LENGTH_LONG).show();
                        Intent intent =  new Intent(getApplicationContext(), LoginIndexActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        String msgname = jsonObject.getString("msgname");
                        if (msgname.equals("email_existed")) {
                            Toast.makeText(getApplicationContext(), getString(R.string.email_existed), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
                        }
                    }

                    Looper.loop();
                } catch (Exception e) {
                    Log.e("my_regis","parese json faield:" + e);
                }
            }
        };
        call.enqueue(callBack);
        return true;
    }
}
