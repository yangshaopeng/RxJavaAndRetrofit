package com.hhmt.rxjavaandretrofit.net;


import com.hhmt.rxjavaandretrofit.response.PublishBean;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * author    : yangshaopeng
 * email     : ysp@btomorrow.cn
 * date      : 2017/10/12  10:23
 * desc      : <p> </p>
 * package   : com.hhmt.rxretrofitdemo.rest
 * project   : RxRetrofitDemo
 */

public interface ApiService {

    @GET("movie/in_theaters?apikey=0b2bdeda43b5688921839c8ecb20399b&city=%E5%8C%97%E4%BA%AC&start=0&count=100&client=somemessage&udid=dddddddddddddddddddddd")
    Observable<PublishBean> getPublishInfo();

}
