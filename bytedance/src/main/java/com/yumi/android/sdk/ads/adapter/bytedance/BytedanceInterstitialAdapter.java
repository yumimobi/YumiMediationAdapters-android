package com.yumi.android.sdk.ads.adapter.bytedance;

import android.app.Activity;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTInteractionAd;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerInterstitialAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.yumi.android.sdk.ads.adapter.bytedance.BytedanceUtil.getAppName;
import static com.yumi.android.sdk.ads.adapter.bytedance.BytedanceUtil.recodeError;
import static com.yumi.android.sdk.ads.adapter.bytedance.BytedanceUtil.sdkVersion;
import static com.yumi.android.sdk.ads.utils.device.WindowSizeUtils.dip2px;

public class BytedanceInterstitialAdapter extends YumiCustomerInterstitialAdapter {

    private static final String TAG = "BytedanceInterstitialAdapter";
    private TTAdNative mTTAdNative;
    private TTInteractionAd.AdInteractionListener interactionListener;
    private TTInteractionAd mTTInteractionAd;
    private TTAdNative.InteractionAdListener loadListener;
    private boolean isReady = false;

    protected BytedanceInterstitialAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }


    @Override
    protected void onPrepareInterstitial() {
        ZplayDebug.d(TAG, "load new interstitial");
        if (mTTAdNative != null && loadListener != null) {
            isReady = false;
            AdSlot adSlot = new AdSlot.Builder()
                    .setCodeId(getProvider().getKey2())
                    .setSupportDeepLink(true)
                    .setImageAcceptedSize(dip2px(600), dip2px(600))
                    .build();

            mTTAdNative.loadInteractionAd(adSlot, loadListener);
        }
    }

    @Override
    protected void onShowInterstitialLayer(Activity activity) {
        if (mTTInteractionAd != null && isReady) {
            mTTInteractionAd.showInteractionAd(getActivity());
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

        loadListener = new TTAdNative.InteractionAdListener() {
            @Override
            public void onError(int code, String message) {
                ZplayDebug.d(TAG, "onError：" + message);
                layerPreparedFailed(recodeError(code, message));
                isReady = false;
            }

            @Override
            public void onInteractionAdLoad(TTInteractionAd ttInteractionAd) {
                ZplayDebug.d(TAG, "onInteractionAdLoad");
                mTTInteractionAd = ttInteractionAd;
                setAdInteractionListener(mTTInteractionAd);
                isReady = true;
                layerPrepared();
            }
        };

    }

    private void setAdInteractionListener(TTInteractionAd ttInteractionAd) {
        ttInteractionAd.setAdInteractionListener(new TTInteractionAd.AdInteractionListener() {
            @Override
            public void onAdClicked() {
                ZplayDebug.d(TAG, "onAdClicked");
                isReady = false;
                layerClicked(-99, -99);
            }

            @Override
            public void onAdShow() {
                ZplayDebug.d(TAG, "onAdShow");
                isReady = false;
                layerExposure();
                layerStartPlaying();
            }

            @Override
            public void onAdDismiss() {
                ZplayDebug.d(TAG, "onAdDismiss");
                layerClosed();
            }
        });
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
