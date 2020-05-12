package com.project.mapping.util;

import com.project.mapping.constant.ApiConstant;
import com.project.mapping.constant.ApiService;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager {
    private Retrofit mRetrofit;
    private ApiService mApiService;

    /**
     * RetrofitManager
     */
    private RetrofitManager() {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.connectTimeout(15, TimeUnit.SECONDS);
        mRetrofit = new Retrofit.Builder()
                .client(httpClientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(ApiConstant.BASE_URL)
                .build();
        mApiService = mRetrofit.create(ApiService.class);
    }


    /**
     * Instance
     *
     * @return
     */
    public static RetrofitManager getInstance() {
        return Inner.retrofitManager;
    }

    private static class Inner {
        private static final RetrofitManager retrofitManager = new RetrofitManager();
    }

    /**
     * getService
     *
     * @return ApiService
     */
    public ApiService getService() {
        return mApiService;
    }

}

