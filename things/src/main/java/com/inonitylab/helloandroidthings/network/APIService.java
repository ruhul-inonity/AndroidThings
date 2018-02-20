package com.inonitylab.helloandroidthings.network;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created with love by inonity on 2/19/18.
 */

public interface APIService {

    @GET("PutCount?value=1")
    Call<String> setCountRequest(@Query("id") String sid);
}
