package com.hhmt.rxjavaandretrofit.response;

/**
 * author    : yangshaopeng
 * email     : ysp@btomorrow.cn
 * date      : 2017/10/12  13:07
 * desc      : <p> </p>
 * package   : com.hhmt.rxretrofitdemo.response
 * project   : RxRetrofitDemo
 */

public class MultiObservable {

    public PublishBean publishBeanObservable1;
    public PublishBean publishBeanObservable2;

    public MultiObservable(PublishBean publishBeanObservable1, PublishBean publishBeanObservable2) {
        this.publishBeanObservable1 = publishBeanObservable1;
        this.publishBeanObservable2 = publishBeanObservable2;
    }

}
