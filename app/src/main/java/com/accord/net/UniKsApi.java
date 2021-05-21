package com.accord.net;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

import static com.accord.util.Constants.*;

public interface UniKsApi {
    @POST(LOGIN_PATH)
    Call<PostRequests> login(@Body PostRequests postRequests);

    @GET(USERS_PATH)
    Call<GetRequests> getUsers(@Header("userKey") String userKey);
}
