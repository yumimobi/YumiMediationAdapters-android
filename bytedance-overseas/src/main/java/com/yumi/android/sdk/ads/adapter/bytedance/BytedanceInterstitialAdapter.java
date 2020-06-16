package com.yumi.android.sdk.ads.adapter.bytedance;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerInterstitialAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.yumi.android.sdk.ads.adapter.bytedance.BytedanceUtil.getAppName;
import static com.yumi.android.sdk.ads.adapter.bytedance.BytedanceUtil.initSDK;
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

        initSDK(getActivity(), getProvider().getKey1(), getAppName(getActivity().getPackageManager(), getActivity().getPackageName()));
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
                ZplayDebug.d(TAG, "onFullScreenVideoCached");
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
                isReady = false;
                layerClosed();
            }

            @Override
            public void onVideoComplete() {
                ZplayDebug.d(TAG, "onVideoComplete");
                isReady = false;
            }

            @Override
            public void onSkippedVideo() {
                ZplayDebug.d(TAG, "onSkippedVideo");
                isReady = false;
            }

        });
    }

    private int getOrientation() {
        if (getScreenMode(getContext()) == 0) {
            return TTAdConstant.HORIZONTAL;
        } else {
            return TTAdConstant.VERTICAL;
        }
    }

    /**
     * 获取横竖屏的标示，约定1：竖屏 0：横屏
     */
    public static int getScreenMode(Context context) {
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return 0;
        } else {
            return 1;
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
