package com.hhmt.rxjavaandretrofit.ui.activity;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import com.hhmt.rxjavaandretrofit.R;
import com.hhmt.rxjavaandretrofit.net.ApiService;
import com.hhmt.rxjavaandretrofit.net.ServiceGenerator;
import com.hhmt.rxjavaandretrofit.response.MultiObservable;
import com.hhmt.rxjavaandretrofit.response.PublishBean;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class RxLifecycleActivity extends RxAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_lifecycle);

        new ServiceGenerator().createService(ApiService.class).getPublishInfo()
                .map(new Func1<PublishBean, PublishBean>() {
                    @Override
                    public PublishBean call(PublishBean publishBean) {
                        SystemClock.sleep(5000);
                        return publishBean;
                    }
                })
                .compose(this.<PublishBean>bindToLifecycle())       //防止内存泄漏
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<PublishBean>() {
                    @Override
                    public void call(PublishBean publishBean) {
                        Log.i("yang", "title: " + publishBean.getTitle());
                    }
                });

    }
}
