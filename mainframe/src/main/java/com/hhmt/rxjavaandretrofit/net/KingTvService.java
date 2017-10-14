package com.hhmt.rxjavaandretrofit.net;

import com.hhmt.rxjavaandretrofit.response.kingtv.AppStart;

import retrofit2.http.GET;
import rx.Observable;

/**
 * author    : yangshaopeng
 * email     : ysp@btomorrow.cn
 * date      : 2017/10/14  16:47
 * desc      : <p> </p>
 * package   : com.hhmt.rxjavaandretrofit.net
 * project   : RxJavaAndRetrofit
 */

public interface KingTvService {

    public static final String BASE_URL = "http://www.quanmin.tv/";

    @GET("json/page/app-data/info.json?v=3.0.1&os=1&ver=4")
    Observable<AppStart> getAppStartInfo();
}
