package com.hhmt.rxjavaandretrofit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hhmt.rxjavaandretrofit.net.ApiService;
import com.hhmt.rxjavaandretrofit.net.KingTvService;
import com.hhmt.rxjavaandretrofit.net.ServiceGenerator;
import com.hhmt.rxjavaandretrofit.response.MultiObservable;
import com.hhmt.rxjavaandretrofit.response.PublishBean;
import com.hhmt.rxjavaandretrofit.response.kingtv.AppStart;
import com.hhmt.rxjavaandretrofit.response.kingtv.Banner;
import com.hhmt.rxjavaandretrofit.ui.activity.RxLifecycleActivity;
import com.hhmt.rxjavaandretrofit.ui.banner.ConvenientBanner;
import com.hhmt.rxjavaandretrofit.ui.banner.holder.CBViewHolderCreator;
import com.hhmt.rxjavaandretrofit.ui.banner.holder.Holder;
import com.hhmt.rxjavaandretrofit.ui.banner.listener.OnItemClickListener;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class Mainframe extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainframe);
        convenientBanner = (ConvenientBanner) findViewById(R.id.convenientBanner);

        /*----------------------------------------zip----------------------------------------*/
        /**
         * 使用场景：一个页面多个请求时，将所有请求都完成后再更新页面。
         * http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2016/0325/4080.html
         */
        Observable<PublishBean> publishBeanObservable1 = new ServiceGenerator().createService(ApiService.class)
                .getPublishInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        Observable<PublishBean> publishBeanObservable2 = new ServiceGenerator().createService(ApiService.class).getPublishInfo()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());

        /**
         * 合并两次请求。
         */
        Observable.zip(publishBeanObservable1, publishBeanObservable2, new BiFunction<PublishBean, PublishBean, MultiObservable>() {
            @Override
            public MultiObservable apply(PublishBean publishBean, PublishBean publishBean2) throws Exception {
                return new MultiObservable(publishBean, publishBean2);
            }
        });

        Observable<MultiObservable> combine = Observable.zip(publishBeanObservable1, publishBeanObservable2, new BiFunction<PublishBean, PublishBean, MultiObservable>() {
            @Override
            public MultiObservable apply(PublishBean publishBean, PublishBean publishBean2) throws Exception {
                return new MultiObservable(publishBean, publishBean2);
            }
        });

        combine.subscribe(new Consumer<MultiObservable>() {
            @Override
            public void accept(MultiObservable multiObservable) throws Exception {
                Log.i("yang", multiObservable.publishBeanObservable1.getTitle());
                Log.i("yang", multiObservable.publishBeanObservable2.getTitle());
            }
        });
        /*----------------------------------------flatmap && compose----------------------------------------*//*
       /**
         * http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2015/0309/2571.html
         * 使用场景：对一个集合的元素进行处理，虽然也可以用for循环，但这种更清晰易懂。
         *
         * compose:子线程和主线程监听放到一块。
         *
         */

        new ServiceGenerator().createService(ApiService.class)
                .getPublishInfo()
                .map(new Function<PublishBean, List<PublishBean.SubjectsBean>>() {
                    @Override
                    public List<PublishBean.SubjectsBean> apply(PublishBean publishBean) throws Exception {
                        return publishBean.getSubjects();
                    }
                })
                .flatMap(new Function<List<PublishBean.SubjectsBean>, Observable<PublishBean.SubjectsBean>>() {
                    @Override
                    public Observable<PublishBean.SubjectsBean> apply(List<PublishBean.SubjectsBean> subjectsBeen) throws Exception {
                        return null;
                    }
                })
                .compose(this.<PublishBean.SubjectsBean>ioMainListener())
                .subscribe(new Consumer<PublishBean.SubjectsBean>() {
                    @Override
                    public void accept(PublishBean.SubjectsBean subjectsBean) throws Exception {
                        Log.i("flatmap compose: ", subjectsBean.getTitle());
                    }
                });

        /*----------------------------------------RxRecyclerLife----------------------------------------*/
        ((Button) findViewById(R.id.rxlifecycle)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Mainframe.this, RxLifecycleActivity.class));
            }
        });

        /*----------------------------------------Banner----------------------------------------*/
        new ServiceGenerator().createService(KingTvService.class, KingTvService.BASE_URL)
                .getAppStartInfo()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<AppStart>() {
                    @Override
                    public void accept(AppStart appStart) throws Exception {
                        Log.i("yang", "banner success ：" + appStart.toString());
                        initBanner(appStart.getBanners());
                    }
                });
    }

    public <T> ObservableTransformer<T, T> ioMainListener() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        if (convenientBanner != null && !convenientBanner.isTurning()) {
            convenientBanner.startTurning(4000);
        }
    }

    private ConvenientBanner<Banner> convenientBanner;
    private List<Banner> listBanner;

    private void initBanner() {
        convenientBanner.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                clickBannerItem(listBanner.get(position));
            }
        });
    }

    private void clickBannerItem(Banner banner) {
        if (banner != null) {
            Log.i("yang", "clickBannerItem: " + banner.getTitle());
            if (banner.isRoom()) {//如果是房间类型就点击进入房间
                //startRoom(banner.getLink_object());
            } else {//广告类型
                //startWeb(banner.getTitle(),banner.getLink());
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (convenientBanner != null) {
            convenientBanner.stopTurning();
        }

    }


    private void initBanner(List<Banner> banners) {
        listBanner = banners;
        initBanner();
        convenientBanner.setPages(new CBViewHolderCreator() {
            @Override
            public Holder<Banner> createHolder() {
                return new ImageHolder();
            }
        }, listBanner)
                .setPageIndicator(new int[]{R.mipmap.ic_dot_normal, R.mipmap.ic_dot_pressed})
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL);
    }

    public class ImageHolder implements Holder<Banner> {

        private ImageView iv;

        @Override
        public View createView(Context context) {
            iv = new ImageView(context);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return iv;
        }

        @Override
        public void UpdateUI(Context context, int position, Banner data) {
            Glide.with(context).load(data.getThumb()).centerCrop().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(iv);
        }
    }

}
