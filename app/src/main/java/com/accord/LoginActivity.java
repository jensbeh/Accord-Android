package com.accord;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.accord.net.rest.RestClient;
import com.accord.net.rest.responses.ResponseWithJsonObject;
import com.accord.util.Constants;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class LoginActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;
    private EditText editText_ipaddress;
    private EditText editText_username;
    private EditText editText_password;
    private Button button_logIn;
    private Button button_signIn;
    private TextView textView_info;
    private CheckBox checkBox_rememberMe;
    private CheckBox checkbox_loginTempUser;
    private RestClient restClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editText_ipaddress = findViewById(R.id.editText_IpAddress);
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
            editText_ipaddress.setText(sharedPreferences.getString("IP-Address", ""));
            editText_username.setText(sharedPreferences.getString("Username", ""));
            editText_password.setText(sharedPreferences.getString("Password", ""));
            checkBox_rememberMe.setChecked(true);
        }

        restClient = new RestClient();
    }

    public void loginButtonClick(View v) {

        if (!editText_ipaddress.getText().toString().equals("") && !editText_username.getText().toString().equals("") && !editText_password.getText().toString().equals("")) {

            if (checkBox_rememberMe.isChecked()) {
                sharedPreferencesEditor.putString("IP-Address", editText_ipaddress.getText().toString());
                sharedPreferencesEditor.putString("Username", editText_username.getText().toString());
                sharedPreferencesEditor.putString("Password", editText_password.getText().toString());
                sharedPreferencesEditor.putBoolean("rememberMe", true);
            } else {
                sharedPreferencesEditor.putString("Username", "");
                sharedPreferencesEditor.putString("Password", "");
                sharedPreferencesEditor.putBoolean("rememberMe", false);
            }
            sharedPreferencesEditor.apply();

            String ipAddress = editText_ipaddress.getText().toString();
            String username = editText_username.getText().toString();
            String password = editText_password.getText().toString();

            // Check if ip address is valid
            if (!isIpAddress(ipAddress)) {
                return;
            }

            // Set IpAddress
            Constants.setIpAddress(ipAddress);
            restClient.setup();

            restClient.doLogin(username, password, new RestClient.ResponseCallbackWithObject() {
                @Override
                public void onSuccess(String status, ResponseWithJsonObject.Data data) {
                    System.out.print(status);
                    System.out.print(data);

                    String userKey = data.getUserKey();
                    System.out.print(userKey);

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("password", password);
                    intent.putExtra("userKey", userKey);
                    startActivity(intent);
                }

                @Override
                public void onFailed(Throwable error) {
                    System.out.print("Error: " + error.getMessage());
                }
            });

        }
    }

    private boolean isIpAddress(String ipAddress) {
        try {
            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            return inetAddress instanceof Inet4Address;
        } catch (UnknownHostException ex) {
            return false;
        }
    }


    private void singInButtonClick(View view) {
        String ipAddress = editText_ipaddress.getText().toString();
        String username = editText_username.getText().toString();
        String password = editText_password.getText().toString();

        // Check if ip address is valid
        if (!isIpAddress(ipAddress)) {
            return;
        }

        // Set IpAddress
        Constants.setIpAddress(ipAddress);
        restClient.setup();

        restClient.doSignIn(username, password, new RestClient.ResponseCallbackWithObject() {
            @Override
            public void onSuccess(String status, ResponseWithJsonObject.Data data) {
                textView_info.setText("SignIn successfully!");
            }

            @Override
            public void onFailed(Throwable error) {
                System.out.print("Error: " + error.getMessage());
            }
        });
    }
}