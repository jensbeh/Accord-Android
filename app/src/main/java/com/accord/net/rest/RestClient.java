package com.accord.net.rest;


import static com.accord.util.Constants.REST_SERVER_URL;
import static com.accord.util.Constants.SUCCESS;

import android.util.Log;

import com.accord.net.rest.requests.PostRequestsCreateChannel;
import com.accord.net.rest.requests.PostRequestsCreateServer;
import com.accord.net.rest.requests.PostRequestsNamePassword;
import com.accord.net.rest.responses.ResponseWithJsonList;
import com.accord.net.rest.responses.ResponseWithJsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

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
    public interface ResponseCallbackWithList {
        void onSuccess(String status, ArrayList<ResponseWithJsonList.Data> dataArrayList);

        void onFailed(Throwable error);
    }

    public interface ResponseCallbackWithObject {
        void onSuccess(String status, ResponseWithJsonObject.Data data);

        void onFailed(Throwable error);
    }

    public void doSignIn(String username, String password, final ResponseCallbackWithObject responseCallbackWithObject) {
        PostRequestsNamePassword postRequestsNamePassword = new PostRequestsNamePassword(username, password);
        Call<ResponseWithJsonObject> call = restApi.signIn(postRequestsNamePassword);

        call.enqueue(new Callback<ResponseWithJsonObject>() {
            @Override
            public void onResponse(@NotNull Call<ResponseWithJsonObject> call, @NotNull Response<ResponseWithJsonObject> response) {
                if (response != null) {
                    // Action
                    if (responseCallbackWithObject != null) {
                        if (response.body().getStatus().equals(SUCCESS)) {
                            responseCallbackWithObject.onSuccess(response.body().getStatus(), response.body().getData());
                        } else {
                            responseCallbackWithObject.onFailed(new Throwable("Can't signIn! " + response.body().getMessage()));
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseWithJsonObject> call, @NotNull Throwable t) {
                Log.v("ERROR", t + "");
                if (responseCallbackWithObject != null)
                    responseCallbackWithObject.onFailed(t);
            }
        });
    }

    public void doLogin(String username, String password, final ResponseCallbackWithObject responseCallbackWithObject) {
        PostRequestsNamePassword postRequestsNamePassword = new PostRequestsNamePassword(username, password);
        Call<ResponseWithJsonObject> call = restApi.login(postRequestsNamePassword);

        call.enqueue(new Callback<ResponseWithJsonObject>() {
            @Override
            public void onResponse(@NotNull Call<ResponseWithJsonObject> call, @NotNull Response<ResponseWithJsonObject> response) {
                if (response != null) {
                    // Action
                    if (responseCallbackWithObject != null) {
                        if (response.body().getStatus().equals(SUCCESS)) {
                            responseCallbackWithObject.onSuccess(response.body().getStatus(), response.body().getData());
                        } else {
                            responseCallbackWithObject.onFailed(new Throwable("Can't login! " + response.body().getMessage()));
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseWithJsonObject> call, @NotNull Throwable t) {
                Log.v("ERROR", t + "");
                if (responseCallbackWithObject != null)
                    responseCallbackWithObject.onFailed(t);
            }
        });
    }

    public void doLogout(String userKey, final ResponseCallbackWithObject responseCallbackWithObject) {
        Call<ResponseWithJsonObject> call = restApi.logout(userKey);

        call.enqueue(new Callback<ResponseWithJsonObject>() {
            @Override
            public void onResponse(@NotNull Call<ResponseWithJsonObject> call, @NotNull Response<ResponseWithJsonObject> response) {
                if (response != null) {
                    // Action
                    if (responseCallbackWithObject != null) {
                        if (response.body().getStatus().equals(SUCCESS)) {
                            responseCallbackWithObject.onSuccess(response.body().getStatus(), response.body().getData());
                        } else {
                            responseCallbackWithObject.onFailed(new Throwable("Can't logout! " + response.body().getMessage()));
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseWithJsonObject> call, @NotNull Throwable t) {
                Log.v("ERROR", t + "");
                if (responseCallbackWithObject != null)
                    responseCallbackWithObject.onFailed(t);
            }
        });
    }

    public void doGetOnlineUser(String userKey, final ResponseCallbackWithList responseCallbackWithList) {
        Call<ResponseWithJsonList> call = restApi.getUsers(userKey);

        call.enqueue(new Callback<ResponseWithJsonList>() {
            @Override
            public void onResponse(@NotNull Call<ResponseWithJsonList> call, @NotNull Response<ResponseWithJsonList> response) {
                if (response != null) {
                    // Action
                    if (responseCallbackWithList != null) {
                        if (response.body().getStatus().equals(SUCCESS)) {
                            responseCallbackWithList.onSuccess(response.body().getStatus(), response.body().getData());
                        } else {
                            responseCallbackWithList.onFailed(new Throwable("Can't get online users! " + response.body().getMessage()));
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseWithJsonList> call, @NotNull Throwable t) {
                Log.v("ERROR", t + "");
                if (responseCallbackWithList != null)
                    responseCallbackWithList.onFailed(t);
            }
        });
    }

    public void doGetServer(String userKey, ResponseCallbackWithList responseCallbackWithList) {
        Call<ResponseWithJsonList> call = restApi.getServer(userKey);

        call.enqueue(new Callback<ResponseWithJsonList>() {
            @Override
            public void onResponse(@NotNull Call<ResponseWithJsonList> call, @NotNull Response<ResponseWithJsonList> response) {
                if (response != null) {
                    // Action
                    if (responseCallbackWithList != null) {
                        if (response.body().getStatus().equals(SUCCESS)) {
                            responseCallbackWithList.onSuccess(response.body().getStatus(), response.body().getData());
                        } else {
                            responseCallbackWithList.onFailed(new Throwable("Can't get servers! " + response.body().getMessage()));
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseWithJsonList> call, @NotNull Throwable t) {
                Log.v("ERROR", t + "");
                if (responseCallbackWithList != null)
                    responseCallbackWithList.onFailed(t);
            }
        });
    }

    public void doGetServerUsers(String serverId, String userKey, ResponseCallbackWithObject responseCallbackWithObject) {
        Call<ResponseWithJsonObject> call = restApi.getServerUsers(serverId, userKey);

        call.enqueue(new Callback<ResponseWithJsonObject>() {
            @Override
            public void onResponse(@NotNull Call<ResponseWithJsonObject> call, @NotNull Response<ResponseWithJsonObject> response) {
                if (response != null) {
                    // Action
                    if (responseCallbackWithObject != null) {
                        if (response.body().getStatus().equals(SUCCESS)) {
                            responseCallbackWithObject.onSuccess(response.body().getStatus(), response.body().getData());
                        } else {
                            responseCallbackWithObject.onFailed(new Throwable("Can't get server users! " + response.body().getMessage()));
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseWithJsonObject> call, @NotNull Throwable t) {
                Log.v("ERROR", t + "");
                if (responseCallbackWithObject != null)
                    responseCallbackWithObject.onFailed(t);
            }
        });
    }

    public void doGetCategories(String serverId, String userKey, ResponseCallbackWithList responseCallbackWithList) {
        Call<ResponseWithJsonList> call = restApi.getCategories(serverId, userKey);

        call.enqueue(new Callback<ResponseWithJsonList>() {
            @Override
            public void onResponse(@NotNull Call<ResponseWithJsonList> call, @NotNull Response<ResponseWithJsonList> response) {
                if (response != null) {
                    // Action
                    if (responseCallbackWithList != null) {
                        if (response.body().getStatus().equals(SUCCESS)) {
                            responseCallbackWithList.onSuccess(response.body().getStatus(), response.body().getData());
                        } else {
                            responseCallbackWithList.onFailed(new Throwable("Can't get categories! " + response.body().getMessage()));
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseWithJsonList> call, @NotNull Throwable t) {
                Log.v("ERROR", t + "");
                if (responseCallbackWithList != null)
                    responseCallbackWithList.onFailed(t);
            }
        });
    }

    public void doGetChannels(String serverId, String categoryId, String userKey, ResponseCallbackWithList responseCallbackWithList) {
        Call<ResponseWithJsonList> call = restApi.getChannels(serverId, categoryId, userKey);

        call.enqueue(new Callback<ResponseWithJsonList>() {
            @Override
            public void onResponse(@NotNull Call<ResponseWithJsonList> call, @NotNull Response<ResponseWithJsonList> response) {
                if (response != null) {
                    // Action
                    if (responseCallbackWithList != null) {
                        if (response.body().getStatus().equals(SUCCESS)) {
                            responseCallbackWithList.onSuccess(response.body().getStatus(), response.body().getData());
                        } else {
                            responseCallbackWithList.onFailed(new Throwable("Can't get channel! " + response.body().getMessage()));
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseWithJsonList> call, @NotNull Throwable t) {
                Log.v("ERROR", t + "");
                if (responseCallbackWithList != null)
                    responseCallbackWithList.onFailed(t);
            }
        });
    }

    public void doGetMessages(long timestamp, String serverId, String categoryId, String channelId, String userKey, ResponseCallbackWithList responseCallbackWithList) {
        Call<ResponseWithJsonList> call = restApi.getMessages(serverId, categoryId, channelId, timestamp, userKey);

        call.enqueue(new Callback<ResponseWithJsonList>() {
            @Override
            public void onResponse(@NotNull Call<ResponseWithJsonList> call, @NotNull Response<ResponseWithJsonList> response) {
                if (response != null) {
                    // Action
                    if (responseCallbackWithList != null) {
                        if (response.body().getStatus().equals(SUCCESS)) {
                            responseCallbackWithList.onSuccess(response.body().getStatus(), response.body().getData());
                        } else {
                            responseCallbackWithList.onFailed(new Throwable("Can't get messages! " + response.body().getMessage()));
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseWithJsonList> call, @NotNull Throwable t) {
                Log.v("ERROR", t + "");
                if (responseCallbackWithList != null)
                    responseCallbackWithList.onFailed(t);
            }
        });
    }

    public void doCreateChannel(String serverId, String categoryId, String channelName, String channelType, boolean privileged, String userKey, ResponseCallbackWithObject responseCallbackWithObject) {
        PostRequestsCreateChannel postRequestsCreateChannel = new PostRequestsCreateChannel(channelName, channelType, privileged);
        Call<ResponseWithJsonObject> call = restApi.createChannel(postRequestsCreateChannel, serverId, categoryId, userKey);

        call.enqueue(new Callback<ResponseWithJsonObject>() {
            @Override
            public void onResponse(@NotNull Call<ResponseWithJsonObject> call, @NotNull Response<ResponseWithJsonObject> response) {
                if (response != null) {
                    // Action
                    if (responseCallbackWithObject != null) {
                        if (response.body().getStatus().equals(SUCCESS)) {
                            responseCallbackWithObject.onSuccess(response.body().getStatus(), response.body().getData());
                        } else {
                            responseCallbackWithObject.onFailed(new Throwable("Can't login! " + response.body().getMessage()));
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseWithJsonObject> call, @NotNull Throwable t) {
                Log.v("ERROR", t + "");
                if (responseCallbackWithObject != null)
                    responseCallbackWithObject.onFailed(t);
            }
        });
    }

    public void doCreateServer(String serverName, String userKey, ResponseCallbackWithObject responseCallbackWithObject) {
        PostRequestsCreateServer postRequestsCreateServer = new PostRequestsCreateServer(serverName);
        Call<ResponseWithJsonObject> call = restApi.createServer(postRequestsCreateServer, userKey);

        call.enqueue(new Callback<ResponseWithJsonObject>() {
            @Override
            public void onResponse(@NotNull Call<ResponseWithJsonObject> call, @NotNull Response<ResponseWithJsonObject> response) {
                if (response != null) {
                    // Action
                    if (responseCallbackWithObject != null) {
                        if (response.body().getStatus().equals(SUCCESS)) {
                            responseCallbackWithObject.onSuccess(response.body().getStatus(), response.body().getData());
                        } else {
                            responseCallbackWithObject.onFailed(new Throwable("Can't login! " + response.body().getMessage()));
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseWithJsonObject> call, @NotNull Throwable t) {
                Log.v("ERROR", t + "");
                if (responseCallbackWithObject != null)
                    responseCallbackWithObject.onFailed(t);
            }
        });
    }

//    public void loginTemp(Callback<JsonNode> callback) {
//        HttpRequest<?> request = Unirest.post(REST_SERVER_URL + TEMP_USER_PATH).header("Content-Type", "application/json");
//        sendRequest(request, callback);
//    }
//
//    public void postServerLeave(String serverId, String userKey, Callback<JsonNode> callback) {
//        String url = REST_SERVER_URL + SERVER_PATH + "/" + serverId + LEAVE_PATH;
//        HttpRequest<?> postRequest = Unirest.post(url).header("userKey", userKey).header("Content-Type", "application/json");
//        sendRequest(postRequest, callback);
//    }
//
//    public JsonNode postServer(String userKey, String serverName, Callback<JsonNode> callback) {
//        JSONObject jsonObj = new JSONObject().accumulate("name", serverName);
//        String body = JSONObject.valueToString(jsonObj);
//        HttpRequest<?> request = Unirest.post(REST_SERVER_URL + SERVER_PATH).header("Content-Type", "application/json").body(body).header("userKey", userKey);
//        sendRequest(request, callback);
//        return null;
//    }
//
//    public void updateChannel(String serverId, String categoryId, String channelId, String userKey, String channelName, boolean privilege, String[] Members, Callback<JsonNode> callback) {
//        JSONObject jsonObj = new JSONObject().accumulate("name", channelName).accumulate("privileged", privilege).accumulate("members", Members);
//        String body = JSONObject.valueToString(jsonObj);
//        HttpRequest<?> request = Unirest.put(REST_SERVER_URL + SERVER_PATH + "/" + serverId + SERVER_CATEGORIES_PATH + "/" + categoryId + SERVER_CHANNELS_PATH + "/" + channelId).body(body).header("userKey", userKey).header("Content-Type", "application/json");
//        sendRequest(request, callback);
//    }
//
//    public void putServer(String serverId, String serverName, String userKey, Callback<JsonNode> callback) {
//        JSONObject jsonObj = new JSONObject().accumulate("name", serverName);
//        String body = JSONObject.valueToString(jsonObj);
//        String url = REST_SERVER_URL + SERVER_PATH + "/" + serverId;
//        HttpRequest<?> postRequest = Unirest.put(url).header("userKey", userKey).body(body).header("Content-Type", "application/json");
//        sendRequest(postRequest, callback);
//    }
//
//    public void deleteServer(String serverId, String userKey, Callback<JsonNode> callback) {
//        String url = REST_SERVER_URL + SERVER_PATH + "/" + serverId;
//        HttpRequest<?> postRequest = Unirest.delete(url).header("userKey", userKey).header("Content-Type", "application/json");
//        sendRequest(postRequest, callback);
//    }
//
//    public void createChannel(String serverId, String categoryId, String userKey, String channelName, String type, boolean privileged, String[] members, Callback<JsonNode> callback) {
//        JSONObject jsonObj = new JSONObject().accumulate("name", channelName).accumulate("type", type).accumulate("privileged", privileged).accumulate("members", members);
//        String body = JSONObject.valueToString(jsonObj);
//        HttpRequest<?> request = Unirest.post(REST_SERVER_URL + SERVER_PATH + "/" + serverId + SERVER_CATEGORIES_PATH + "/" + categoryId + SERVER_CHANNELS_PATH).body(body).header("Content-Type", "application/json").header("userKey", userKey);
//        sendRequest(request, callback);
//    }
//
//    public void deleteChannel(String serverId, String categoryId, String channelId, String userKey, Callback<JsonNode> callback) {
//        String url = REST_SERVER_URL + SERVER_PATH + "/" + serverId + SERVER_CATEGORIES_PATH + "/" + categoryId + SERVER_CHANNELS_PATH + "/" + channelId;
//        HttpRequest<?> postRequest = Unirest.delete(url).header("userKey", userKey).header("Content-Type", "application/json");
//        sendRequest(postRequest, callback);
//    }
//
//    private void sendRequest(HttpRequest<?> req, Callback<JsonNode> callback) {
//        req.asJsonAsync(callback);
//    }
//
//    public void createTempLink(String type, Integer max, String serverId, String userKey, Callback<JsonNode> callback) {
//        JSONObject jsonObj = new JSONObject().accumulate("type", type);
//        if (type.equals("count")) {
//            jsonObj.accumulate("max", max);
//        }
//        String body = JSONObject.valueToString(jsonObj);
//        HttpRequest<?> request = Unirest.post(REST_SERVER_URL + SERVER_PATH + "/" + serverId + SERVER_INVITES).header("userKey", userKey).body(body).header("Content-Type", "application/json");
//        sendRequest(request, callback);
//    }
//
//    public void getInvLinks(String serverId, String userKey, Callback<JsonNode> callback) {
//        String url = REST_SERVER_URL + SERVER_PATH + "/" + serverId + SERVER_INVITES;
//        HttpRequest<?> postRequest = Unirest.get(url).header("userKey", userKey).header("Content-Type", "application/json");
//        sendRequest(postRequest, callback);
//    }
//
//    public void deleteInvLink(String serverId, String invId, String userKey, Callback<JsonNode> callback) {
//        String url = REST_SERVER_URL + SERVER_PATH + "/" + serverId + SERVER_INVITES + "/" + invId;
//        HttpRequest<?> postRequest = Unirest.delete(url).header("userKey", userKey).header("Content-Type", "application/json");
//        sendRequest(postRequest, callback);
//    }
//
//    public void joinServer(String serverId, String inviteId, String username, String password, String userKey, Callback<JsonNode> callback) {
//        JSONObject jsonObj = new JSONObject().accumulate("name", username).accumulate("password", password);
//        String body = JSONObject.valueToString(jsonObj);
//        HttpRequest<?> request = Unirest.post(REST_SERVER_URL + SERVER_PATH + "/" + serverId + SERVER_INVITES + "/" + inviteId).header("userKey", userKey).body(body).header("Content-Type", "application/json");
//        sendRequest(request, callback);
//    }
//
//    public void createCategory(String serverId, String categoryName, String userKey, Callback<JsonNode> callback) {
//        JSONObject jsonObj = new JSONObject().accumulate("name", categoryName);
//        String body = JSONObject.valueToString(jsonObj);
//        HttpRequest<?> request = Unirest.post(REST_SERVER_URL + SERVER_PATH + "/" + serverId + SERVER_CATEGORIES_PATH).body(body).header("Content-Type", "application/json").header("userKey", userKey);
//        sendRequest(request, callback);
//    }
//
//    public void updateCategory(String serverId, String categoryId, String categoryName, String userKey, Callback<JsonNode> callback) {
//        JSONObject jsonObj = new JSONObject().accumulate("name", categoryName);
//        String body = JSONObject.valueToString(jsonObj);
//        HttpRequest<?> request = Unirest.put(REST_SERVER_URL + SERVER_PATH + "/" + serverId + SERVER_CATEGORIES_PATH + "/" + categoryId).body(body).header("userKey", userKey).header("Content-Type", "application/json");
//        sendRequest(request, callback);
//    }
//
//    public void deleteCategory(String serverId, String categoryId, String userKey, Callback<JsonNode> callback) {
//        HttpRequest<?> request = Unirest.delete(REST_SERVER_URL + SERVER_PATH + "/" + serverId + SERVER_CATEGORIES_PATH + "/" + categoryId).header("userKey", userKey).header("Content-Type", "application/json");
//        sendRequest(request, callback);
//    }
//
//    public void joinVoiceChannel(String serverId, String catId, String channelId, String userKey, Callback<JsonNode> callback) {
//        HttpRequest<?> request = Unirest.post(REST_SERVER_URL + SERVER_PATH + "/" + serverId + SERVER_CATEGORIES_PATH + "/" + catId + SERVER_CHANNELS_PATH + "/" + channelId + SERVER_AUDIO_JOIN).header("userKey", userKey).header("Content-Type", "application/json");
//        sendRequest(request, callback);
//    }
//
//    public void leaveVoiceChannel(String serverId, String catId, String channelId, String userKey, Callback<JsonNode> callback) {
//        HttpRequest<?> request = Unirest.post(REST_SERVER_URL + SERVER_PATH + "/" + serverId + SERVER_CATEGORIES_PATH + "/" + catId + SERVER_CHANNELS_PATH + "/" + channelId + SERVER_AUDIO_LEAVE).header("userKey", userKey).header("Content-Type", "application/json");
//        sendRequest(request, callback);
//    }
//
//    public void updateMessage(String serverId, String catId, String channelId, String msgId, String text, String userKey, Callback<JsonNode> callback) {
//        JSONObject jsonObj = new JSONObject().accumulate("text", text);
//        String body = JSONObject.valueToString(jsonObj);
//        HttpRequest<?> request = Unirest.put(REST_SERVER_URL + SERVER_PATH + "/" + serverId
//                + SERVER_CATEGORIES_PATH + "/" + catId + SERVER_CHANNELS_PATH + "/" + channelId + SERVER_MESSAGE_PATH
//                + "/" + msgId).body(body).header("userKey", userKey).header("Content-Type", "application/json");
//        sendRequest(request, callback);
//    }
//
//    public void deleteMessage(String serverId, String catId, String channelId, String msgId, String text, String userKey, Callback<JsonNode> callback) {
//        JSONObject jsonObj = new JSONObject().accumulate("text", text);
//        String body = JSONObject.valueToString(jsonObj);
//        HttpRequest<?> request = Unirest.delete(REST_SERVER_URL + SERVER_PATH + "/" + serverId
//                + SERVER_CATEGORIES_PATH + "/" + catId + SERVER_CHANNELS_PATH + "/" + channelId + SERVER_MESSAGE_PATH
//                + "/" + msgId).body(body).header("userKey", userKey).header("Content-Type", "application/json");
//        sendRequest(request, callback);
//    }
//
//    public void resolveVanityID(String vanityURL, Callback<JsonNode> callback) {
//        HttpRequest<?> request = Unirest.get(STEAM_API_BASE_URL + STEAM_API_STEAM_USER + STEAM_API_RESOLVE_VANITY + STEAM_API_KEY + "&vanityurl=" + vanityURL);
//        sendRequest(request, callback);
//    }
//
//    public void updateDescription(String userId, String description, String userKey, Callback<JsonNode> callback) {
//        JSONObject jsonObj = new JSONObject().accumulate("text", description);
//        String body = JSONObject.valueToString(jsonObj);
//        HttpRequest<?> request = Unirest.post(REST_SERVER_URL + USERS_PATH + "/" + userId + SERVER_USER_DESCRIPTION).body(body).header("userKey", userKey).header("Content-Type", "application/json");
//        sendRequest(request, callback);
//    }
//
//    public void getCurrentGame(String steamToken, Callback<JsonNode> callback) {
//        HttpRequest<?> request = Unirest.get(STEAM_API_BASE_URL + STEAM_API_STEAM_USER + STEAM_API_PLAYER_SUMMARIES + STEAM_API_KEY + "&steamids=" + steamToken);
//        sendRequest(request, callback);
//    }
}
