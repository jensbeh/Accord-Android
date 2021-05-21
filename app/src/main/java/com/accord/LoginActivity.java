package com.accord;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.accord.net.RestClient;
import com.accord.net.UniKsApi;

import java.util.Map;
import java.util.Objects;

//import com.accord.net.RestClient;

public class LoginActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;
    private EditText editText_username;
    private EditText editText_password;
    private Button button_logIn;
    private Button button_signIn;
    private UniKsApi uniKsApi;
    private TextView textView_info;
    private RestClient restClient;

    private String userKey;
    private Map onlineUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        try {
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        } catch (Exception e) {
        }

        editText_username = findViewById(R.id.editText_Username);
        editText_password = findViewById(R.id.editText_Password);
        button_logIn = findViewById(R.id.button_logIn);
        button_signIn = findViewById(R.id.button_signIn);
        textView_info = findViewById(R.id.textView_info);


        button_logIn.setOnClickListener(this::loginButtonClick);
        //button_signIn.setOnClickListener(this::singInButtonClick);

        textView_info.setText("");
        sharedPreferences = getSharedPreferences("UserInfo", 0);
        sharedPreferencesEditor = sharedPreferences.edit();
        editText_username.setText(sharedPreferences.getString("Username", ""));
        editText_password.setText(sharedPreferences.getString("Password", ""));

        restClient = new RestClient();
        restClient.setup();


        /*
        // RestAPI
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(REST_SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        uniKsApi = retrofit.create(UniKsApi.class);

        //userKey = restClient.getPostRequests().getData().get("userKey").toString();
        System.out.print("XXX");

        restClient.createGet(uniKsApi.getUsers(userKey));
        //onlineUser = restClient.getGetRequests().getData();
        System.out.print("XXX");*/
    }

    public void loginButtonClick(View v) {

        if (!editText_username.getText().toString().equals("") && !editText_password.getText().toString().equals("")) {

            sharedPreferencesEditor.putString("Username", editText_username.getText().toString());
            sharedPreferencesEditor.putString("Password", editText_password.getText().toString());
            sharedPreferencesEditor.apply();

            String username = editText_username.getText().toString();
            String password = editText_password.getText().toString();

            restClient.doLogin(username, password, new RestClient.LoginCallback() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onLogin(String status, Map<String, String> userKeyMap) {
                    System.out.print(status);
                    System.out.print(userKeyMap);

                    userKey = userKeyMap.get("userKey");
                    System.out.print(userKey);

                    ModelBuilder modelBuilder = new ModelBuilder();
                    modelBuilder.buildPersonalUser(username, userKey);

                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);

                }

                @Override
                public void onLoginFailed(Throwable error) {
                    System.out.print("Error: " + error.getMessage());
                }
            });

        }
    }
}