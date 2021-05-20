package com.accord;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private SharedPreferences.Editor sharedPreferencesEditor;
    private EditText editText_username;
    private EditText editText_password;
    private Button button_logIn;
    private Button button_signIn;

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

        button_logIn.setOnClickListener(this::loginButtonClick);
        button_signIn.setOnClickListener(this::singInButtonClick);


        SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", 0);
        sharedPreferencesEditor = sharedPreferences.edit();
        editText_username.setText(sharedPreferences.getString("Username", ""));
        editText_password.setText(sharedPreferences.getString("Password", ""));
    }

    public void loginButtonClick(View v) {
        if (!editText_username.getText().toString().equals("") && !editText_password.getText().toString().equals("")) {
            sharedPreferencesEditor.putString("textUsername", editText_username.getText().toString());
            sharedPreferencesEditor.putString("txtPassword", editText_password.getText().toString());
            sharedPreferencesEditor.commit();
        } else {
        }
    }

    public void singInButtonClick(View v) {
        if (!editText_username.getText().toString().equals("") && !editText_password.getText().toString().equals("")) {

        } else {
        }
    }
}