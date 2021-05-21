package com.accord;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.accord.net.PostRequests;
import com.accord.net.RestClient;
import com.accord.net.UniKsApi;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.accord.util.Constants.REST_SERVER_URL;

//import com.accord.net.RestClient;

public class LoginActivity extends AppCompatActivity {
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
    //private RestClient restClient;

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

        SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", 0);
        sharedPreferencesEditor = sharedPreferences.edit();
        editText_username.setText(sharedPreferences.getString("Username", ""));
        editText_password.setText(sharedPreferences.getString("Password", ""));

        restClient = new RestClient();



        // RestAPI
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(REST_SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        uniKsApi = retrofit.create(UniKsApi.class);

        PostRequests postRequests = new PostRequests("Jens", "1234");
        restClient.createPost(uniKsApi.login(postRequests));
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //userKey = restClient.getPostRequests().getData().get("userKey").toString();
        System.out.print("XXX");

        restClient.createGet(uniKsApi.getUsers(userKey));
        //onlineUser = restClient.getGetRequests().getData();
        System.out.print("XXX");
    }

    public void loginButtonClick(View v) {

        if (!editText_username.getText().toString().equals("") && !editText_password.getText().toString().equals("")) {

            try {

                //apiPostLogin(Constants.ANDROID_KEY + ":" + textUsername.getText().toString() + ":" + Password.getText().toString());
                sharedPreferencesEditor.putString("textUsername", editText_username.getText().toString());
                sharedPreferencesEditor.putString("txtPassword", editText_password.getText().toString());
                sharedPreferencesEditor.commit();

                editText_username.setText("Jens");
                editText_password.setText("1234");


                String username = editText_username.getText().toString();
                String password = editText_password.getText().toString();

                // JSONObject jsonObj = new JSONObject().accumulate("name", username).accumulate("password", password);
                // String body = jsonObj.getString("data");

                RequestQueue requestQueue = Volley.newRequestQueue(this);
                String URL = " https://ac.uniks.de/api/users/login";
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("name", username);
                jsonBody.put("password", password);
                final String requestBody = jsonBody.toString();

                HashMap<String, String> capitalCities = new HashMap<String, String>();

                // Add keys and values (Country, City)
                capitalCities.put("name", username);
                capitalCities.put("password", password);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}