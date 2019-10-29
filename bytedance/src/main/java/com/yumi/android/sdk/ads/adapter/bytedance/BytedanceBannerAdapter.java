package com.yumi.android.sdk.ads.adapter.bytedance;

import android.app.Activity;
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

import static com.yumi.android.sdk.ads.adapter.bytedance.BytedanceUtil.getAppName;
import static com.yumi.android.sdk.ads.adapter.bytedance.BytedanceUtil.recodeError;
import static com.yumi.android.sdk.ads.adapter.bytedance.BytedanceUtil.sdkVersion;
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
        ZplayDebug.d(TAG, "load new banner");
        if (bannerSize == AdSize.BANNER_SIZE_SMART) {
            ZplayDebug.d(TAG, "not support smart banner:");
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
        ZplayDebug.d(TAG, "init key1: " + getProvider().getKey1() + " ,key2: " + getProvider().getKey2());

        TTAdSdk.init(getActivity(),
                new TTAdConfig.Builder()
                        .appId(getProvider().getKey1())
                        .useTextureView(false)
                        .appName(getAppName(getActivity().getPackageManager(), getActivity().getPackageName()))
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
                ZplayDebug.d(TAG, "onError：" + message);
                layerPreparedFailed(recodeError(code, message));

            }

            @Override
            public void onBannerAdLoad(final TTBannerAd ad) {
                if (ad == null) {
                    layerPreparedFailed(recodeError(-999, "TTBannerAd is null"));
                    return;
                }
                if (getProvider().getAutoRefreshInterval() > 0) {
                    ad.setSlideIntervalTime(getProvider().getAutoRefreshInterval());
                }
                View bannerView = ad.getBannerView();
                if (bannerView == null) {
                    layerPreparedFailed(recodeError(-999, "bannerView is null"));
                    return;
                }

                ad.setBannerInteractionListener(new TTBannerAd.AdInteractionListener() {
                    @Override
                    public void onAdClicked(View view, int type) {
                        ZplayDebug.d(TAG, "onAdClicked");
                        layerClicked(-99f, -99f);
                    }

                    @Override
                    public void onAdShow(View view, int type) {
                        ZplayDebug.d(TAG, "onAdShow");
                    }
                });
                ZplayDebug.d(TAG, "Prepared");
                layerPrepared(bannerView, true);
            }
        };
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
                bannerWidth = dip2px(bannerWidth);
                bannerHeight = dip2px(bannerHeight);
            }
        } else {
            if (bannerSize == AdSize.BANNER_SIZE_728X90) {
                bannerWidth = 728;
                bannerHeight = 90;
            } else {
                bannerWidth = 320;
                bannerHeight = 50;
            }
            bannerWidth = dip2px(bannerWidth);
            bannerHeight = dip2px(bannerHeight);
        }
    }

    @Override
    public String getProviderVersion() {
        return sdkVersion();
    }
}