package com.accord.net;


import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.accord.util.Constants.REST_SERVER_URL;

public class RestClient {

    private UniKsApi uniKsApi;

    public void setup() {
        //Logging Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(REST_SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        uniKsApi = retrofit.create(UniKsApi.class);
    }

    // Interface
    public interface LoginCallback {
        void onLogin(String status, Map<String, String> userKey);
        void onLoginFailed(Throwable error);
    }
    public interface getUserCallback {
        void onGetUser();
        void onGetUserFailed(Throwable error);
    }

    public void doLogin(String username, String password, final LoginCallback postCallback) {

        PostRequests postRequests = new PostRequests(username, password);
        Call<PostRequests> call = uniKsApi.login(postRequests);

        call.enqueue(new Callback<PostRequests>() {
            @Override
            public void onResponse(@NotNull Call<PostRequests> call, @NotNull Response<PostRequests> response) {
                if (response != null) {
                    JSONObject obj = null;

                    // Action
                    if (postCallback != null) {
                        postCallback.onLogin(response.body().getStatus(), response.body().getData());
                    }
                }
            }
            @Override
            public void onFailure(@NotNull Call<PostRequests> call, @NotNull Throwable t) {
                Log.v("ERROR", t + "");
                if (postCallback != null)
                    postCallback.onLoginFailed(t);
            }
        });
    }

    public void getOnlineUser(String username, String password, final getUserCallback postCallback) {

        PostRequests postRequests = new PostRequests(username, password);
        Call<PostRequests> call = uniKsApi.login(postRequests);

        call.enqueue(new Callback<PostRequests>() {
            @Override
            public void onResponse(@NotNull Call<PostRequests> call, @NotNull Response<PostRequests> response) {
                if (response != null) {
                    JSONObject obj = null;

                    // Action
                    if (postCallback != null) {
                        postCallback.onGetUser();
                    }
                }
            }
            @Override
            public void onFailure(@NotNull Call<PostRequests> call, @NotNull Throwable t) {
                Log.v("ERROR", t + "");
                if (postCallback != null)
                    postCallback.onGetUserFailed(t);
            }
        });
    }



    public void createGet(Call<GetRequests> call) {
        call.enqueue(new Callback<GetRequests>() {
            @Override
            public void onResponse(Call<GetRequests> call, Response<GetRequests> response) {
                if (!response.isSuccessful()) {
                    System.out.print("XXX CODE: " + response.code());
                    return;
                }

                //getRequests = response.body();
                Map data = response.body().getData();
                System.out.print(data);
            }

            @Override
            public void onFailure(Call<GetRequests> call, Throwable t) {
                System.out.print("XXX ERROR: " + t.getMessage());
            }
        });
    }

    /*
    HttpResponse<JsonNode> jsonResponse = Unirest.post("http://httpbin.org/post")
            .header("accept", "application/json")
            .field("parameter", "value")
            .field("file", new File("/tmp/file"))
            .asJson();

    public void signIn(String username, String password, Callback<JsonNode> callback) throws JSONException {
        JSONObject jsonObj = new JSONObject().accumulate("password", password).accumulate("name", username);
        String body = jsonObj.getString("data");
        HttpRequest<?> request = Unirest.post(REST_SERVER_URL + API_PREFIX + USERS_PATH).body(body);
        sendRequest(request, callback);
    }

    public void login(String username, String password, Callback<JsonNode> callback) throws JSONException {
        JSONObject jsonObj = new JSONObject().accumulate("name", username).accumulate("password", password);
        String body = jsonObj.getString("data");
        HttpRequest<?> request = Unirest.post(REST_SERVER_URL + API_PREFIX + LOGIN_PATH).body(body);
        sendRequest(request, callback);
    }

    public void loginTemp(Callback<JsonNode> callback) {
        HttpRequest<?> request = Unirest.post(REST_SERVER_URL + API_PREFIX + TEMP_USER_PATH);
        sendRequest(request, callback);
    }

    public void logout(String userKey, Callback<JsonNode> callback) {
        HttpRequest<?> request = Unirest.post(REST_SERVER_URL + API_PREFIX + LOGOUT_PATH).header("userKey", userKey);
        sendRequest(request, callback);
    }

    public void getServers(String userKey, Callback<JsonNode> callback) {
        HttpRequest<?> request = Unirest.get(REST_SERVER_URL + API_PREFIX + SERVER_PATH).header("userKey", userKey);
        sendRequest(request, callback);
    }

    public void getUsers(String userKey, Callback<JsonNode> callback) {
        HttpRequest<?> request = Unirest.get(REST_SERVER_URL + API_PREFIX + USERS_PATH).header("userKey", userKey);
        sendRequest(request, callback);
    }

    public void getServerUsers(String serverId, String userKey, Callback<JsonNode> callback) {
        String url = REST_SERVER_URL + API_PREFIX + SERVER_PATH + serverId;
        HttpRequest<?> postRequest = Unirest.get(url).header("userKey", userKey);
        sendRequest(postRequest, callback);
    }

    public JsonNode postServer(String userKey, String serverName) {
        JSONObject jsonBody = new JSONObject();
        //jsonBody.put("name", serverName);
        HttpResponse<JsonNode> response = Unirest.post(REST_SERVER_URL + API_PREFIX + SERVER_PATH).body(jsonBody).header("userKey", userKey).asJson();
        return response.getBody();
    }

    private void sendRequest(HttpRequest<?> req, Callback<JsonNode> callback) {
        req.asJsonAsync(callback);
    }*/
}
