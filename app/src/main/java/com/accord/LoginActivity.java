package com.accord;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.accord.net.RestClient;
import com.accord.net.UniKsApi;
import com.google.gson.Gson;

import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;
    private EditText editText_username;
    private EditText editText_password;
    private Button button_logIn;
    private Button button_signIn;
    private UniKsApi uniKsApi;
    private TextView textView_info;
    private CheckBox checkBox_rememberMe;
    private CheckBox checkbox_loginTempUser;
    private RestClient restClient;

    private String userKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editText_username = findViewById(R.id.editText_Username);
        editText_password = findViewById(R.id.editText_Password);
        button_logIn = findViewById(R.id.button_logIn);
        button_signIn = findViewById(R.id.button_signIn);
        textView_info = findViewById(R.id.textView_info);
        checkBox_rememberMe = findViewById(R.id.checkbox_rememberMe);
        checkbox_loginTempUser = findViewById(R.id.checkbox_loginTempUser);

        button_logIn.setOnClickListener(this::loginButtonClick);
        button_signIn.setOnClickListener(this::singInButtonClick);

        textView_info.setText("");
        sharedPreferences = getSharedPreferences("UserInfo", 0);
        sharedPreferencesEditor = sharedPreferences.edit();

        if (sharedPreferences.getBoolean("rememberMe", false)) {
            editText_username.setText(sharedPreferences.getString("Username", ""));
            editText_password.setText(sharedPreferences.getString("Password", ""));
            checkBox_rememberMe.setChecked(true);
        }

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

            if (checkBox_rememberMe.isChecked()) {
                sharedPreferencesEditor.putString("Username", editText_username.getText().toString());
                sharedPreferencesEditor.putString("Password", editText_password.getText().toString());
                sharedPreferencesEditor.putBoolean("rememberMe", true);
            } else {
                sharedPreferencesEditor.putString("Username", "");
                sharedPreferencesEditor.putString("Password", "");
                sharedPreferencesEditor.putBoolean("rememberMe", false);
            }
            sharedPreferencesEditor.apply();

            String username = editText_username.getText().toString();
            String password = editText_password.getText().toString();

            restClient.doLogin(username, password, new RestClient.PostCallback() {
                @Override
                public void onSuccess(String status, Map<String, String> data) {
                    System.out.print(status);
                    System.out.print(data);

                    userKey = data.get("userKey");
                    System.out.print(userKey);

                    ModelBuilder modelBuilder = new ModelBuilder();
                    modelBuilder.buildPersonalUser(username, userKey);

                    Gson gson = new Gson();
                    String modelBuilderAsAString = gson.toJson(modelBuilder);

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("ModelBuilder", modelBuilderAsAString);
                    startActivity(intent);
                }

                @Override
                public void onFailed(Throwable error) {
                    System.out.print("Error: " + error.getMessage());
                }
            });

        }
    }

    private void singInButtonClick(View view) {
        Toast.makeText(this, "Coming Soon!", Toast.LENGTH_SHORT).show();
    }
}