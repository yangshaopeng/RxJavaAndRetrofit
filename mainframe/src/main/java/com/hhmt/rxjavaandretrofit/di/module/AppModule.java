package com.hhmt.rxjavaandretrofit.di.module;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * author    : yangshaopeng
 * email     : ysp@btomorrow.cn
 * date      : 2017/09/13  20:31
 * desc      : <p> </p>
 * package   : com.hhmt.rxjavaandretrofit.di.module
 * project   : RxJavaAndRetrofit
 */

@Module
public class AppModule {

    private Context context;
    private String baseUrl;

    public AppModule(Context context, String baseurl) {
        this.context = context;
        this.baseUrl = baseurl;
    }

    @Provides
    @Singleton
    public Context provideContext() {
        return context;
    }


}
