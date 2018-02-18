package com.inonitylab.helloandroidthings.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created with love by inonity on 2/19/18.
 */

public interface APIService {

    @GET("PutCount?value={id}")
    Call<Void> setCountRequest(@Path("id") String id);
}
