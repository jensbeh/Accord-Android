package com.accord.net.rest;

import static com.accord.util.Constants.CATEGORIES_PATH;
import static com.accord.util.Constants.CHANNELS_PATH;
import static com.accord.util.Constants.LOGIN_PATH;
import static com.accord.util.Constants.LOGOUT_PATH;
import static com.accord.util.Constants.MESSAGES_PATH;
import static com.accord.util.Constants.SERVER_PATH;
import static com.accord.util.Constants.USERS_PATH;

import com.accord.net.rest.requests.PostRequestsCreateChannel;
import com.accord.net.rest.requests.PostRequestsCreateServer;
import com.accord.net.rest.requests.PostRequestsNamePassword;
import com.accord.net.rest.responses.ResponseWithJsonList;
import com.accord.net.rest.responses.ResponseWithJsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RestApi {
    //////////////
    // POST
    //////////////
    @POST(USERS_PATH)
    Call<ResponseWithJsonObject> signIn(@Body PostRequestsNamePassword postRequestsNamePassword);

    @POST(LOGIN_PATH)
    Call<ResponseWithJsonObject> login(@Body PostRequestsNamePassword postRequestsNamePassword);

    @POST(LOGOUT_PATH)
    Call<ResponseWithJsonObject> logout(@Header("userKey") String userKey);

    @POST(SERVER_PATH)
    Call<ResponseWithJsonObject> createServer(@Body PostRequestsCreateServer postRequestsCreateServer, @Header("userKey") String userKey);

    @POST(SERVER_PATH + "/{serverId}" + CATEGORIES_PATH + "/{categoryId}" + CHANNELS_PATH)
    Call<ResponseWithJsonObject> createChannel(@Body PostRequestsCreateChannel postRequestsCreateChannel, @Path("serverId") String serverId, @Path("categoryId") String categoryId, @Header("userKey") String userKey);
    //////////////
    // GET
    //////////////
    @GET(USERS_PATH)
    Call<ResponseWithJsonList> getUsers(@Header("userKey") String userKey);

    @GET(SERVER_PATH)
    Call<ResponseWithJsonList> getServer(@Header("userKey") String userKey);

    @GET(SERVER_PATH + "/{serverId}")
    Call<ResponseWithJsonObject> getServerUsers(@Path("serverId") String serverId, @Header("userKey") String userKey);

    @GET(SERVER_PATH + "/{serverId}" + CATEGORIES_PATH)
    Call<ResponseWithJsonList> getCategories(@Path("serverId") String serverId, @Header("userKey") String userKey);

    @GET(SERVER_PATH + "/{serverId}" + CATEGORIES_PATH + "/{categoryId}" + CHANNELS_PATH)
    Call<ResponseWithJsonList> getChannels(@Path("serverId") String serverId, @Path("categoryId") String categoryId, @Header("userKey") String userKey);

    @GET(SERVER_PATH + "/{serverId}" + CATEGORIES_PATH + "/{categoryId}" + CHANNELS_PATH + "/{channelId}" + MESSAGES_PATH)
    Call<ResponseWithJsonList> getMessages(@Path("serverId") String serverId, @Path("categoryId") String categoryId, @Path("channelId") String channelId, @Query("timestamp") long timestamp, @Header("userKey") String userKey);
}