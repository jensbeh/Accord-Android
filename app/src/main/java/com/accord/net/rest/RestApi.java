package com.accord.net.rest;

import static com.accord.util.Constants.CATEGORIES_PATH;
import static com.accord.util.Constants.CHANNELS_PATH;
import static com.accord.util.Constants.LOGIN_PATH;
import static com.accord.util.Constants.LOGOUT_PATH;
import static com.accord.util.Constants.MESSAGES_PATH;
import static com.accord.util.Constants.SERVER_PATH;
import static com.accord.util.Constants.USERS_PATH;

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
    @POST(LOGIN_PATH)
    Call<PostRequests> login(@Body PostRequests postRequests);

    @POST(LOGOUT_PATH)
    Call<PostRequests> logout(@Header("userKey") String userKey);

    //////////////
    // GET
    //////////////
    @GET(USERS_PATH)
    Call<GetRequestsWithList> getUsers(@Header("userKey") String userKey);

    @GET(SERVER_PATH)
    Call<GetRequestsWithList> getServer(@Header("userKey") String userKey);

    @GET(SERVER_PATH + "/{serverId}")
    Call<GetRequestsWithObject> getServerUsers(@Path("serverId") String serverId, @Header("userKey") String userKey);

    @GET(SERVER_PATH + "/{serverId}" + CATEGORIES_PATH)
    Call<GetRequestsWithList> getCategories(@Path("serverId") String serverId, @Header("userKey") String userKey);

    @GET(SERVER_PATH + "/{serverId}" + CATEGORIES_PATH + "/{categoryId}" + CHANNELS_PATH)
    Call<GetRequestsWithList> getChannels(@Path("serverId") String serverId, @Path("categoryId") String categoryId, @Header("userKey") String userKey);

    @GET(SERVER_PATH + "/{serverId}" + CATEGORIES_PATH + "/{categoryId}" + CHANNELS_PATH + "/{channelId}" + MESSAGES_PATH)
    Call<GetRequestsWithList> getMessages(@Path("serverId") String serverId, @Path("categoryId") String categoryId, @Path("channelId") String channelId, @Query("timestamp") long timestamp, @Header("userKey") String userKey);
}