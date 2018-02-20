package com.inonitylab.helloandroidthings.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created with love by inonity on 2/19/18.
 */

public class ApiClient {

    public static Retrofit retrofit = null;
    public static final String BASE_URL = "http://192.168.0.118:31416/Home/";

    public Retrofit getClient() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);


        OkHttpClient.Builder client = new OkHttpClient.Builder();

        client.addInterceptor(chain -> {

            Request request = chain.request().newBuilder()
                    //.addHeader("Authorization", "Bearer" + token)
                    .build();
            return chain.proceed(request);
        }).build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        client.addNetworkInterceptor(logging);

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client.build())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;

    }

}
