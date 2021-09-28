package com.accord.net.rest;


import static com.accord.util.Constants.REST_SERVER_URL;
import static com.accord.util.Constants.SUCCESS;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {

    private RestApi restApi;

    public void setup() {
        //Logging Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(REST_SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        restApi = retrofit.create(RestApi.class);
    }

    // Interface
    public interface PostCallback {
        void onSuccess(String status, Map<String, String> data);
        void onFailed(Throwable error);
    }

    public interface GetCallback {
        void onSuccess(String status, List data);
        void onFailed(Throwable error);
    }

    public void doLogin(String username, String password, final PostCallback postCallback) {
        PostRequests postRequests = new PostRequests(username, password);
        Call<PostRequests> call = restApi.login(postRequests);

        call.enqueue(new Callback<PostRequests>() {
            @Override
            public void onResponse(@NotNull Call<PostRequests> call, @NotNull Response<PostRequests> response) {
                if (response != null) {
                    // Action
                    if (postCallback != null) {
                        if (response.body().getStatus().equals(SUCCESS)) {
                            postCallback.onSuccess(response.body().getStatus(), response.body().getData());
                        } else {
                            postCallback.onFailed(new Throwable("Can't Login! " + response.body().getMessage()));
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<PostRequests> call, @NotNull Throwable t) {
                Log.v("ERROR", t + "");
                if (postCallback != null)
                    postCallback.onFailed(t);
            }
        });
    }

    public void doLogout(String userKey, final PostCallback postCallback) {
        Call<PostRequests> call = restApi.logout(userKey);

        call.enqueue(new Callback<PostRequests>() {
            @Override
            public void onResponse(@NotNull Call<PostRequests> call, @NotNull Response<PostRequests> response) {
                if (response != null) {
                    // Action
                    if (postCallback != null) {
                        if (response.body().getStatus().equals(SUCCESS)) {
                            postCallback.onSuccess(response.body().getStatus(), response.body().getData());
                        } else {
                            postCallback.onFailed(new Throwable("Can't Logout! " + response.body().getMessage()));
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<PostRequests> call, @NotNull Throwable t) {
                Log.v("ERROR", t + "");
                if (postCallback != null)
                    postCallback.onFailed(t);
            }
        });
    }

    public void doGetOnlineUser(String userKey, final GetCallback getCallback) {
        Call<GetRequests> call = restApi.getUsers(userKey);

        call.enqueue(new Callback<GetRequests>() {
            @Override
            public void onResponse(@NotNull Call<GetRequests> call, @NotNull Response<GetRequests> response) {
                if (response != null) {
                    // Action
                    if (getCallback != null) {
                        if (response.body().getStatus().equals(SUCCESS)) {
                            getCallback.onSuccess(response.body().getStatus(), response.body().getData());
                        } else {
                            getCallback.onFailed(new Throwable("Can't Logout! " + response.body().getMessage()));
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<GetRequests> call, @NotNull Throwable t) {
                Log.v("ERROR", t + "");
                if (getCallback != null)
                    getCallback.onFailed(t);
            }
        });
    }

    public void doGetServer(String userKey, GetCallback getCallback) {
        Call<GetRequests> call = restApi.getServer(userKey);

        call.enqueue(new Callback<GetRequests>() {
            @Override
            public void onResponse(@NotNull Call<GetRequests> call, @NotNull Response<GetRequests> response) {
                if (response != null) {
                    // Action
                    if (getCallback != null) {
                        if (response.body().getStatus().equals(SUCCESS)) {
                            getCallback.onSuccess(response.body().getStatus(), response.body().getData());
                        } else {
                            getCallback.onFailed(new Throwable("Can't Logout! " + response.body().getMessage()));
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<GetRequests> call, @NotNull Throwable t) {
                Log.v("ERROR", t + "");
                if (getCallback != null)
                    getCallback.onFailed(t);
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
