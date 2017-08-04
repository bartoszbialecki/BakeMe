package com.example.bakeme.service;

import com.example.bakeme.model.Recipe;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    // region CONSTANTS
    private static final String BASE_URL = "http://go.udacity.com/";
    private static final int TIMEOUT = 60;
    // endregion

    // region VARIABLES
    private static ApiClient sInstance = new ApiClient();
    private ApiService mService;
    // endregion

    // region CONSTRUCTORS
    private ApiClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        mService = retrofit.create(ApiService.class);
    }
    // endregion

    // region PUBLIC METHODS
    public static ApiClient getInstance() {
        return sInstance;
    }

    public Single<List<Recipe>> getRecipes() {
        return mService.getRecipes();
    }
    // endregion
}
