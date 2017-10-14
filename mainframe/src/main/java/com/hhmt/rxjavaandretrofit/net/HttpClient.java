package com.hhmt.rxjavaandretrofit.net;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * author    : yangshaopeng
 * email     : ysp@btomorrow.cn
 * date      : 2017/10/13  20:40
 * desc      : <p> </p>
 * package   : com.hhmt.rxjavaandretrofit.net
 * project   : RxJavaAndRetrofit
 */

public class HttpClient extends OkHttpClient {

    private static OkHttpClient instance;
    public static final int CONNECT_TIME = 10 * 1000;
    public static final int READ_TIME = 10 * 1000;
    public static final int WRITE_TIME = 10 * 1000;

    public static OkHttpClient getInstance() {
        if (instance == null) {
            instance = new OkHttpClient.Builder()
                    .connectTimeout(CONNECT_TIME, TimeUnit.SECONDS)
                    .readTimeout(READ_TIME, TimeUnit.SECONDS)
                    .writeTimeout(WRITE_TIME, TimeUnit.SECONDS)
                    //.addInterceptor()
                    .build();
        }
        return instance;
    }

}
