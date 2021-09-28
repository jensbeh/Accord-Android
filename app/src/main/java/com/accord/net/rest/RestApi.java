package com.accord.net.rest;

import static com.accord.util.Constants.LOGIN_PATH;
import static com.accord.util.Constants.LOGOUT_PATH;
import static com.accord.util.Constants.SERVER_PATH;
import static com.accord.util.Constants.USERS_PATH;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

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
    Call<GetRequests> getUsers(@Header("userKey") String userKey);

    @GET(SERVER_PATH)
    Call<GetRequests> getServer(@Header("userKey") String userKey);

//    @GET(SERVER_PATH + "/{categoryId}")
//    Call<GetRequests> getServerCategories(@Path("categoryId") @Header("userKey") String userKey);
}