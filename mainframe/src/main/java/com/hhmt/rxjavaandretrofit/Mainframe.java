package com.hhmt.rxjavaandretrofit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.hhmt.rxjavaandretrofit.net.ApiService;
import com.hhmt.rxjavaandretrofit.net.ServiceGenerator;
import com.hhmt.rxjavaandretrofit.response.MultiObservable;
import com.hhmt.rxjavaandretrofit.response.PublishBean;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class Mainframe extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainframe);

        Observable<PublishBean> publishBeanObservable1 = new ServiceGenerator().createService(ApiService.class).getPublishInfo()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());

        Observable<PublishBean> publishBeanObservable2 = new ServiceGenerator().createService(ApiService.class).getPublishInfo()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());

        Observable<MultiObservable> combine = Observable.zip(publishBeanObservable1, publishBeanObservable2, new Func2<PublishBean, PublishBean, MultiObservable>() {
            @Override
            public MultiObservable call(PublishBean publishBean, PublishBean publishBean2) {
                return new MultiObservable(publishBean, publishBean2);
            }
        });

        combine.subscribe(new Action1<MultiObservable>() {
            @Override
            public void call(MultiObservable multiObservable) {
                Log.i("yang", multiObservable.publishBeanObservable1.getTitle());
                Log.i("yang", multiObservable.publishBeanObservable2.getTitle());
            }
        });
    }
}
