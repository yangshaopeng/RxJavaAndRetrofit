package com.hhmt.rxjavaandretrofit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.hhmt.rxjavaandretrofit.net.ApiService;
import com.hhmt.rxjavaandretrofit.net.ServiceGenerator;
import com.hhmt.rxjavaandretrofit.response.MultiObservable;
import com.hhmt.rxjavaandretrofit.response.PublishBean;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class Mainframe extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainframe);

        /*----------------------------------------zip----------------------------------------*/
        /**
         * 使用场景：一个页面多个请求时，将所有请求都完成后再更新页面。
         * http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2016/0325/4080.html
         */
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
        /*----------------------------------------flatmap----------------------------------------*/
        /**
         * http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2015/0309/2571.html
         * 使用场景：对一个集合的元素进行处理，虽然也可以用for循环，但这种更清晰易懂。
         */
        new ServiceGenerator().createService(ApiService.class)
                .getPublishInfo()
                .map(new Func1<PublishBean, List<PublishBean.SubjectsBean>>() {
                    @Override
                    public List<PublishBean.SubjectsBean> call(PublishBean publishBean) {
                        return publishBean.getSubjects();
                    }
                })
                .flatMap(new Func1<List<PublishBean.SubjectsBean>, Observable<PublishBean.SubjectsBean>>() {
                    @Override
                    public Observable<PublishBean.SubjectsBean> call(List<PublishBean.SubjectsBean> subjectsBeen) {
                        return Observable.from(subjectsBeen);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<PublishBean.SubjectsBean>() {
                    @Override
                    public void call(PublishBean.SubjectsBean subjectsBean) {
                        Log.i("flatmap: ", subjectsBean.getTitle());
                    }
                });

    }
}
