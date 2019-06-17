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
import com.yumi.android.sdk.ads.utils.device.PackageInfoGetter;

import static com.yumi.android.sdk.ads.adapter.bytedance.BytedanceUtil.recodeError;
import static com.yumi.android.sdk.ads.utils.device.WindowSizeUtils.dip2px;


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
        if (bannerSize == AdSize.BANNER_SIZE_SMART) {
            ZplayDebug.d(TAG, "Bytedance banner not support smart banner:", onoff);
            layerPreparedFailed(recodeError(-999, "not support smart banner."));
            return;
        }
        calculateBannerSize();
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(getProvider().getKey2())
                .setSupportDeepLink(true)
                .setImageAcceptedSize(bannerWidth, bannerHeight)
                .build();
        mTTAdNative.loadBannerAd(adSlot, bannerAdListener);
    }

    @Override
    protected void init() {
        ZplayDebug.d(TAG, "bytedance banner init key1: " + getProvider().getKey1() + "key2: " + getProvider().getKey2(), onoff);

        TTAdSdk.init(getActivity(),
                new TTAdConfig.Builder()
                        .appId(getProvider().getKey1())
                        .useTextureView(false)
                        .appName(PackageInfoGetter.getAppName(getActivity().getPackageManager(), getActivity().getPackageName()))
                        .titleBarTheme(TTAdConstant.TITLE_BAR_THEME_DARK)
                        .allowShowNotify(false)
                        .allowShowPageWhenScreenLock(false)
                        .debug(false)
                        .directDownloadNetworkType(TTAdConstant.NETWORK_STATE_WIFI, TTAdConstant.NETWORK_STATE_3G)
                        .supportMultiProcess(false)
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
                    layerPreparedFailed(recodeError(-999, "Bytedance banner TTBannerAd is null"));
                    return;
                }
                if(getProvider().getAutoRefreshInterval() > 0) {
                    ad.setSlideIntervalTime(getProvider().getAutoRefreshInterval());
                }
                View bannerView = ad.getBannerView();
                if (bannerView == null) {
                    layerPreparedFailed(recodeError(-999, "Bytedance banner bannerView is null"));
                    return;
                }

                ad.setBannerInteractionListener(new TTBannerAd.AdInteractionListener() {
                    @Override
                    public void onAdClicked(View view, int type) {
                        ZplayDebug.d(TAG, "Bytedance banner Clicked", onoff);
                        layerClicked(-99f, -99f);
                    }

                    @Override
                    public void onAdShow(View view, int type) {
                        layerExposure();
                    }
                });
                ZplayDebug.d(TAG, "Bytedance banner Prepared", onoff);
                layerPrepared(bannerView, false);
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


}