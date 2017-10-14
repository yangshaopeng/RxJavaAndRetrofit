package com.hhmt.rxjavaandretrofit;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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
import com.hhmt.rxjavaandretrofit.ui.banner.ConvenientBanner;
import com.hhmt.rxjavaandretrofit.ui.banner.holder.CBViewHolderCreator;
import com.hhmt.rxjavaandretrofit.ui.banner.holder.Holder;
import com.hhmt.rxjavaandretrofit.ui.banner.listener.OnItemClickListener;

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
        convenientBanner = (ConvenientBanner) findViewById(R.id.convenientBanner);

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

        /*----------------------------------------Banner----------------------------------------*/
        new ServiceGenerator().createService(KingTvService.class, KingTvService.BASE_URL)
                .getAppStartInfo()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<AppStart>() {
                    @Override
                    public void call(AppStart appStart) {
                        Log.i("yang", "banner success");
                        initBanner(appStart.getBanners());
                    }
                });
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
