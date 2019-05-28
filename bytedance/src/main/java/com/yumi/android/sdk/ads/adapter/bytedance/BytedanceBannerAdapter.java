package com.yumi.android.sdk.ads.adapter.bytedance;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTBannerAd;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerBannerAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.AdSize;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.yumi.android.sdk.ads.adapter.bytedance.BytedanceUtil.recodeError;


public class BytedanceBannerAdapter extends YumiCustomerBannerAdapter {

    private static final String TAG = "BytedanceBannerAdapter";
    private TTAdNative mTTAdNative;
    private int bannerHeight = 50;
    private int bannerWidth = 320;
    private TTAdNative.BannerAdListener bannerAdListener;

    protected BytedanceBannerAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }

    @Override
    protected void onPrepareBannerLayer() {
        ZplayDebug.d(TAG, "Bytedance request new banner", onoff);
        calculateBannerSize();
        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId("901121895") //广告位id
                .setSupportDeepLink(true)
                .setImageAcceptedSize(bannerWidth, bannerHeight)
                .build();
        //step5:请求广告，对请求回调的广告作渲染处理
        mTTAdNative.loadBannerAd(adSlot, bannerAdListener);
    }

    @Override
    protected void init() {
        ZplayDebug.d(TAG, "bytedance banner init key1: " + getProvider().getKey1() + "key2: " + getProvider().getKey2(), onoff);

        TTAdSdk.init(getActivity(),
                new TTAdConfig.Builder()
                        .appId("5001121")
                        .useTextureView(false) //使用TextureView控件播放视频,默认为SurfaceView,当有SurfaceView冲突的场景，可以使用TextureView
                        .appName(getActivity().getPackageName())
                        .titleBarTheme(TTAdConstant.TITLE_BAR_THEME_DARK)
                        .allowShowNotify(false) //是否允许sdk展示通知栏提示
                        .allowShowPageWhenScreenLock(true) //是否在锁屏场景支持展示广告落地页
                        .debug(true) //测试阶段打开，可以通过日志排查问题，上线时去除该调用
                        .directDownloadNetworkType(TTAdConstant.NETWORK_STATE_WIFI, TTAdConstant.NETWORK_STATE_3G) //允许直接下载的网络状态集合
                        .supportMultiProcess(false) //是否支持多进程，true支持
                        .build());
        mTTAdNative = TTAdSdk.getAdManager().createAdNative(getActivity());//baseContext建议为activity
        createListener();
    }

    private void createListener() {
        bannerAdListener = new TTAdNative.BannerAdListener() {

            @Override
            public void onError(int code, String message) {
                ZplayDebug.d(TAG, "Bytedance banner Prepared Failed，message ：" + message, onoff);
                layerPreparedFailed(recodeError(code, message));

            }

            @Override
            public void onBannerAdLoad(final TTBannerAd ad) {
                if (ad == null) {
                    return;
                }
                View bannerView = ad.getBannerView();
                if (bannerView == null) {
                    return;
                }

                //设置广告互动监听回调
                ad.setBannerInteractionListener(new TTBannerAd.AdInteractionListener() {
                    @Override
                    public void onAdClicked(View view, int type) {
                        ZplayDebug.d(TAG, "Bytedance banner Clicked", onoff);
                        layerClicked(-99f, -99f);
                    }

                    @Override
                    public void onAdShow(View view, int type) {

                    }
                });
                ZplayDebug.d(TAG, "Bytedance banner Prepared", onoff);
                layerPrepared(bannerView, true);
            }
        };
    }

    @Override
    protected void callOnActivityDestroy() {

    }

    @Override
    public void onActivityPause() {

    }

    @Override
    public void onActivityResume() {

    }

    private void calculateBannerSize() {
        if (isMatchWindowWidth && calculateLayerSize != null) {
            if (calculateLayerSize[0] > 0 && calculateLayerSize[1] > 0) {
                bannerWidth = calculateLayerSize[0];
                bannerHeight = calculateLayerSize[1];
                return;
            } else {
                if (bannerSize == AdSize.BANNER_SIZE_728X90) {
                    bannerWidth = 728;
                    bannerHeight = 90;
                } else {
                    bannerWidth = 320;
                    bannerHeight = 50;
                }
                bannerWidth = dip2px(getContext(), bannerWidth);
                bannerHeight = dip2px(getContext(), bannerHeight);
            }
        } else {
            if (bannerSize == AdSize.BANNER_SIZE_728X90) {
                bannerWidth = 728;
                bannerHeight = 90;
            } else {
                bannerWidth = 320;
                bannerHeight = 50;
            }
            bannerWidth = dip2px(getContext(), bannerWidth);
            bannerHeight = dip2px(getContext(), bannerHeight);
        }
    }

    private final int dip2px(Context context, int dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return ((int) (dp * scale + 0.5f));
    }
}