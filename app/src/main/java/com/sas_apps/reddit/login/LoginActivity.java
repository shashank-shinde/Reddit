package com.sas_apps.reddit.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sas_apps.reddit.MainActivity;
import com.sas_apps.reddit.R;
import com.sas_apps.reddit.WebViewActivity;
import com.sas_apps.reddit.comments.CommentsActivity;
import com.sas_apps.reddit.login.reddit_login.CheckLogin;
import com.sas_apps.reddit.retrofit.LogInApi;
import com.sas_apps.reddit.utils.Url;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private ProgressBar progressBar;
    private EditText editUserName;
    private EditText editPassword;
    private Button buttonSignIn;
    TextView textSignUp,textContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressBar = findViewById(R.id.progressBar_login);
        progressBar.setVisibility(View.GONE);
        editUserName = findViewById(R.id.edit_username);
        editPassword = findViewById(R.id.edit_password);
        buttonSignIn = findViewById(R.id.button_login);
        textSignUp = findViewById(R.id.text_signUp);
        textContinue=findViewById(R.id.text_continue);
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editUserName.getText().toString();
                String password = editPassword.getText().toString();
                if (!username.equals("") && !password.equals("")) {
                    //    progressBar.setVisibility(View.VISIBLE);
                    attemptLogin(username, password);
                } else {
                    Toast.makeText(LoginActivity.this, "Empty text", Toast.LENGTH_SHORT).show();
                }
            }
        });
        textSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.reddit.com/register/");
//                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                Intent intent=new Intent(LoginActivity.this,WebViewActivity.class);
                intent.putExtra("url","https://www.reddit.com/register/");
                startActivity(intent);
            }
        });
        textContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    private void attemptLogin(final String username, String password) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Url.LOGIN_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        LogInApi logInApi = retrofit.create(LogInApi.class);

        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "application/json");
        Call<CheckLogin> call = logInApi.signIn(headerMap, username, username, password, "json");
        call.enqueue(new Callback<CheckLogin>() {
            @Override
            public void onResponse(Call<CheckLogin> call, Response<CheckLogin> response) {
//                Log.d(TAG, "onResponse: feed: " + response.body().toString());
//                Log.d(TAG, "onResponse: Server Response: " + response.toString());
                try {
                    String modhash = response.body().getJson().getData().getModhash();
                    String cookie = response.body().getJson().getData().getCookie();
                    Log.d(TAG, "onResponse: modhash: " + modhash);
                    Log.d(TAG, "onResponse: cookie: " + cookie);

                    if (!modhash.equals("")) {
                        saveSessionParameters(username, modhash, cookie);
                        editUserName.setText("");
                        editPassword.setText("");
                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                    }
                } catch (NullPointerException e) {
                    Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onResponse: NullPointerException: " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<CheckLogin> call, Throwable t) {
                Log.e(TAG, "onFailure: Unable to retrieve RSS: " + t.getMessage());
                Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveSessionParameters(String username, String modhash, String cookie) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getResources().getString(R.string.userName), username);
        editor.commit();
        editor.putString(getResources().getString(R.string.modHash), modhash);
        editor.commit();
        editor.putString(getResources().getString(R.string.cookie), cookie);
        editor.commit();
        Log.d(TAG, "saveSessionParameters: Session parameters saved....");
    }
}
