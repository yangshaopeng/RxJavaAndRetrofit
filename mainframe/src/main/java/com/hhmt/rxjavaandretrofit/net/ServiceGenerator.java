package com.hhmt.rxjavaandretrofit.net;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * author    : yangshaopeng
 * email     : ysp@btomorrow.cn
 * date      : 2017/10/12  10:26
 * desc      : <p> </p>
 * package   : com.hhmt.rxretrofitdemo.net
 * project   : RxRetrofitDemo
 */

public class ServiceGenerator {

    public String BASE_URL = "https://api.douban.com/v344123424/";

    private Retrofit.Builder retrofitBuilder;

    public <S> S createService(Class<S> serviceClass, String baseUrl) {

        // TODO: 2017/10/12 0012 要设置client。
        if (this.retrofitBuilder == null) {
            this.retrofitBuilder = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(HttpClient.getInstance())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create());
        } else {
            this.retrofitBuilder.baseUrl(baseUrl);
        }

        Retrofit retrofit = retrofitBuilder.build();
        return retrofit.create(serviceClass);
    }

    public <S> S createService(Class<S> serviceClass) {
        return createService(serviceClass, BASE_URL);
    }

}
