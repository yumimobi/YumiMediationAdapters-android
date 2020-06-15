package com.yumi.android.sdk.ads.adapter.bytedance;

import android.app.Activity;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerInterstitialAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.device.PhoneInfoGetter;

import static com.yumi.android.sdk.ads.adapter.bytedance.BytedanceUtil.getAppName;
import static com.yumi.android.sdk.ads.adapter.bytedance.BytedanceUtil.recodeError;
import static com.yumi.android.sdk.ads.adapter.bytedance.BytedanceUtil.sdkVersion;

public class BytedanceInterstitialAdapter extends YumiCustomerInterstitialAdapter {

    private static final String TAG = "BytedanceInterstitialAdapter";
    private TTAdNative mTTAdNative;
    private TTFullScreenVideoAd mTTFullScreenVideoAd;
    private TTAdNative.FullScreenVideoAdListener loadListener;
    private boolean isReady = false;

    protected BytedanceInterstitialAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }


    @Override
    protected void onPrepareInterstitial() {
        ZplayDebug.d(TAG, "load new interstitial");
        if (mTTAdNative != null && loadListener != null) {
            isReady = false;
            //设置广告参数
            AdSlot adSlot = new AdSlot.Builder()
                    .setCodeId(getProvider().getKey2())
                    .setSupportDeepLink(true)
                    //个性化模板广告需要设置期望个性化模板广告的大小,单位dp,全屏视频场景，只要设置的值大于0即可
                    .setExpressViewAcceptedSize(0, 0)
                    .setImageAcceptedSize(1080, 1920)
                    .setOrientation(getOrientation())
                    .build();

            mTTAdNative.loadFullScreenVideoAd(adSlot, loadListener);
        }
    }

    @Override
    protected void onShowInterstitialLayer(Activity activity) {
        if (mTTFullScreenVideoAd != null && isReady) {
            mTTFullScreenVideoAd.showFullScreenVideoAd(getActivity());
        }
    }

    @Override
    protected boolean isInterstitialLayerReady() {
        return isReady;
    }

    @Override
    protected void init() {
        ZplayDebug.d(TAG, "init key1: " + getProvider().getKey1() + "key2: " + getProvider().getKey2());

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
        createrListener();
    }

    private void createrListener() {

        loadListener = new TTAdNative.FullScreenVideoAdListener() {
            @Override
            public void onError(int code, String message) {
                ZplayDebug.d(TAG, "onError：" + message);
                layerPreparedFailed(recodeError(code, message));
                isReady = false;
            }

            @Override
            public void onFullScreenVideoAdLoad(TTFullScreenVideoAd ttFullScreenVideoAd) {
                ZplayDebug.d(TAG, "onFullScreenVideoAdLoad");
                mTTFullScreenVideoAd = ttFullScreenVideoAd;
                setAdInteractionListener(mTTFullScreenVideoAd);
                isReady = true;
                layerPrepared();
            }

            @Override
            public void onFullScreenVideoCached() {
                ZplayDebug.d(TAG, "onFullScreenVideoAdLoad");
                isReady = true;
            }

        };

    }

    private void setAdInteractionListener(TTFullScreenVideoAd mTTFullScreenVideoAd) {
        mTTFullScreenVideoAd.setFullScreenVideoAdInteractionListener(new TTFullScreenVideoAd.FullScreenVideoAdInteractionListener() {

            @Override
            public void onAdShow() {
                ZplayDebug.d(TAG, "onAdShow");
                isReady = false;
                layerExposure();
                layerStartPlaying();
            }

            @Override
            public void onAdVideoBarClick() {
                ZplayDebug.d(TAG, "onAdClicked");
                isReady = false;
                layerClicked(-99, -99);
            }

            @Override
            public void onAdClose() {
                ZplayDebug.d(TAG, "onAdDismiss");
                layerClosed();
            }

            @Override
            public void onVideoComplete() {

            }

            @Override
            public void onSkippedVideo() {

            }

        });
    }

    private int getOrientation() {
        if (PhoneInfoGetter.getScreenMode(getContext()) == 0) {
            return TTAdConstant.HORIZONTAL;
        } else {
            return TTAdConstant.VERTICAL;
        }
    }

    @Override
    public void onActivityPause() {

    }

    @Override
    public void onActivityResume() {

    }

    @Override
    public boolean onActivityBackPressed() {
        return false;
    }

    @Override
    public String getProviderVersion() {
        return sdkVersion();
    }
}
