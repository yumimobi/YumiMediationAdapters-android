package com.yumi.android.sdk.ads.mraid;

import android.app.Activity;
import android.widget.FrameLayout;

import com.yumi.android.sdk.ads.self.ads.b.BannerAD;

/**
 * Created by mzk10 on 2017/10/31.
 */

public class BannerMraidCreater {

    private final static String[] supportedNativeFeatures = {
            //MRAIDNativeFeature.CALENDAR,
            //MRAIDNativeFeature.INLINE_VIDEO,
            "sms",//MRAIDNativeFeature.SMS,
            //MRAIDNativeFeature.STORE_PICTURE,
            "tel"//MRAIDNativeFeature.TEL,
    };

    private MRAIDView mraidView;
    private Activity activity;
    private String adm;
    private FrameLayout.LayoutParams params_web;
    private int banner_width;
    private int banner_height;
    private FrameLayout bannerBox;
    private BannerAD bannerAD;

    public BannerMraidCreater(Activity activity, String adm, FrameLayout.LayoutParams params_web, int banner_width, int banner_height, FrameLayout bannerBox, BannerAD bannerAD) {
        this.activity = activity;
        this.adm = adm;
        this.params_web = params_web;
        this.banner_width = banner_width;
        this.banner_height = banner_height;
        this.bannerBox = bannerBox;
        this.bannerAD = bannerAD;
    }

    public void createBannerMraid() {
        MyBannerMraidiListener myBannerMraidiListener = new MyBannerMraidiListener(params_web,
                banner_width,
                banner_height,
                bannerBox,
                bannerAD);
        MyMRAIDNativeFeatureListener myMRAIDNativeFeatureListener = new MyMRAIDNativeFeatureListener(activity);
        mraidView = new MRAIDView(activity,
                null,
                adm,
                supportedNativeFeatures,
                myBannerMraidiListener,
                myMRAIDNativeFeatureListener);

        /*try {
            String classname_MyBannerMraidiListener = "com.yumi.android.sdk.ads.mraid.MyBannerMraidiListener";
            Class<?> class_MRAIDViewListener = Class.forName(classname_MyBannerMraidiListener);
            Constructor<?> constructor_MRAIDViewListener = class_MRAIDViewListener.getConstructor(
                    FrameLayout.LayoutParams.class,
                    int.class,
                    int.class,
                    FrameLayout.class,
                    BannerAD.class
            );
            Object obj_MyBannerMraidiListener = constructor_MRAIDViewListener.newInstance(
                    params_web,
                    banner_width,
                    banner_height,
                    bannerBox,
                    bannerAD);

            String classname_MRAIDNativeFeatureListener = "com.yumi.android.sdk.ads.mraid.MRAIDNativeFeatureListener";
            Class<?> class_MRAIDNativeFeatureListener = Class.forName(classname_MRAIDNativeFeatureListener);
            Constructor<?> constructor_MRAIDNativeFeatureListener = class_MRAIDNativeFeatureListener.getConstructor(Context.class);
            Object obj_MyMRAIDNativeFeatureListener = constructor_MRAIDNativeFeatureListener.newInstance(activity);

            String classname_MRAIDView = "com.yumi.android.sdk.ads.mraid.MRAIDView";
            Class<?> class_MRAIDView = Class.forName(classname_MRAIDView);
            Constructor<?> constructor_MRAIDView = class_MRAIDView.getConstructor(
                    Context.class,
                    String.class,
                    String.class,
                    String[].class,
                    Class.forName("com.yumi.android.sdk.ads.mraid.MRAIDViewListener"),
                    Class.forName("com.yumi.android.sdk.ads.mraid.MRAIDNativeFeatureListener")
            );
            obj_MRAIDView = constructor_MRAIDView.newInstance(
                    activity,
                    null,
                    adm,
                    supportedNativeFeatures,
                    obj_MyBannerMraidiListener,
                    obj_MyMRAIDNativeFeatureListener
            );
            return true;
        } catch (Exception e) {
            ZplayDebug.e_s(TAG, e.getMessage(), e, onoff);
            return false;
        }*/
    }

    public void destroyBannerMraid() {
        if (mraidView != null)
        {
            mraidView.destroy();
        }
        /*if (obj_MRAIDView != null) {
            try {
                String classname_MRAIDView = "com.yumi.android.sdk.ads.mraid.MRAIDView";
                Class<?> class_MRAIDView = Class.forName(classname_MRAIDView);
                Method destroy = class_MRAIDView.getMethod("destroy");
                destroy.invoke(obj_MRAIDView);
            } catch (Exception e) {
                ZplayDebug.e_s(TAG, e.getMessage(), e, onoff);
            }
        }*/
    }

}
