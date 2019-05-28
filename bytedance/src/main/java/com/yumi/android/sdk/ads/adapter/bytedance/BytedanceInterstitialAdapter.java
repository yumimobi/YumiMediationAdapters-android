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

import static com.yumi.android.sdk.ads.adapter.bytedance.BytedanceUtil.recodeError;

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
        ZplayDebug.d(TAG, "Bytedance request new interstitial", onoff);
        if (mTTAdNative != null && loadListener != null) {
            isReady = false;
            AdSlot adSlot = new AdSlot.Builder()
                    .setCodeId("901121725")
                    .setSupportDeepLink(true)
                    .setImageAcceptedSize(600, 900) //根据广告平台选择的尺寸，传入同比例尺寸
                    .build();
            //step5:请求广告，调用插屏广告异步请求接口
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
        ZplayDebug.d(TAG, "bytedance interstitial init key1: " + getProvider().getKey1() + "key2: " + getProvider().getKey2(), onoff);

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
        createrListener();
    }

    private void createrListener() {

        loadListener = new TTAdNative.InteractionAdListener() {
            @Override
            public void onError(int code, String message) {
                ZplayDebug.d(TAG, "Bytedance interstitial Prepared Failed，message ：" + message, onoff);
                layerPreparedFailed(recodeError(code, message));
                isReady = false;
            }

            @Override
            public void onInteractionAdLoad(TTInteractionAd ttInteractionAd) {
                ZplayDebug.d(TAG, "Bytedance interstitial Prepared", onoff);
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
                ZplayDebug.d(TAG, "Bytedance interstitial Clicked", onoff);
                isReady = false;
                layerClicked(-99, -99);
            }

            @Override
            public void onAdShow() {
                ZplayDebug.d(TAG, "Bytedance interstitial Show", onoff);
                isReady = false;
                layerExposure();
                layerStartPlaying();
            }

            @Override
            public void onAdDismiss() {
                ZplayDebug.d(TAG, "Bytedance interstitial close", onoff);
                layerClosed();
            }
        });
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

    @Override
    public boolean onActivityBackPressed() {
        return false;
    }

}
