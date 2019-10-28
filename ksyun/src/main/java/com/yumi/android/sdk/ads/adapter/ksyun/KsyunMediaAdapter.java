package com.yumi.android.sdk.ads.adapter.ksyun;

import android.app.Activity;

import com.ksc.ad.sdk.IKsyunAdInitResultListener;
import com.ksc.ad.sdk.IKsyunAdListener;
import com.ksc.ad.sdk.IKsyunAdLoadListener;
import com.ksc.ad.sdk.IKsyunRewardVideoAdListener;
import com.ksc.ad.sdk.KsyunAdSdk;
import com.ksc.ad.sdk.KsyunAdSdkConfig;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.AdError;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerMediaAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import java.util.Map;

import static com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode.ERROR_INTERNAL;

/**
 * Created by hjl on 2018/7/30.
 */
public class KsyunMediaAdapter extends YumiCustomerMediaAdapter {

    private static final String TAG = "KsyunMediaAdapter";
    private boolean isRewarded = false;
    private boolean isInitialized = false;

    protected KsyunMediaAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }

    @Override
    public void onActivityPause() {

    }

    @Override
    public void onActivityResume() {

    }

    @Override
    protected void onPrepareMedia() {
        ZplayDebug.d(TAG, "onPrepareMedia: ");
        if (!isInitialized) {
            initKsyun(new IKsyunAdInitResultListener() {
                @Override
                public void onSuccess(Map<String, String> map) {
                    ZplayDebug.d(TAG, "onSuccess: " + map);
                    isInitialized = true;
                    loadAd();
                }

                @Override
                public void onFailure(int errorCode, String errorMsg) {
                    ZplayDebug.d(TAG, "onFailure: errorCode: " + errorCode + ", errorMsg: " + errorMsg);
                    isInitialized = false;
                    AdError adError = new AdError(ERROR_INTERNAL);
                    adError.setErrorMessage("Ksyun errorCode: " + errorCode + ", errorMsg: " + errorMsg);
                    layerPreparedFailed(adError);
                }
            });
            return;
        }
        loadAd();
    }

    @Override
    protected void onShowMedia() {
        final String slotId = getProvider().getKey2();
        final boolean isReady = KsyunAdSdk.getInstance().hasAd(slotId);
        ZplayDebug.d(TAG, "onShowMedia: " + ", isReady: " + isReady + ", slotId: " + slotId);
        if (isReady) {
            //广告存在，点击奖励视频入口后，调用showAd接口展示广告
            KsyunAdSdk.getInstance().showAd(getActivity(), slotId);
        }
    }

    @Override
    protected boolean isMediaReady() {
        boolean isReady = KsyunAdSdk.getInstance().hasAd(getProvider().getKey2());
        ZplayDebug.d(TAG, "isMediaReady: " + isReady);
        return isReady;
    }

    @Override
    protected void init() {
    }

    private void initKsyun(final IKsyunAdInitResultListener listener) {
        final String appId = getProvider().getKey1();
        final String adSlotId = getProvider().getKey2();
        ZplayDebug.d(TAG, "init: appId: " + appId + ", adSlotId: " + adSlotId);
        // 设置SDK请求环境为线上环境。SDK的init初始化方法，如果不设置config，默认则为沙盒环境
        KsyunAdSdkConfig config = new KsyunAdSdkConfig();
        //正式环境
        config.setSdkEnvironment(KsyunAdSdkConfig.RELEASE_ENV);
        // 测试环境
        // config.setSdkEnvironment(KsyunAdSdkConfig.SANDBOX_ENV);
        // 设置奖励视频展示过程中，允许出现关闭按钮
        config.setShowCloseBtnOfRewardVideo(false);
        // 设置奖励视频展示过程中，出现关闭按钮的时间点
        // config.setCloseBtnComingTimeOfRewardVideo(5);
        KsyunAdSdk.getInstance().init(getActivity(), getProvider().getKey1(), config, new IKsyunAdInitResultListener() {
            @Override
            public void onSuccess(Map<String, String> map) {
                listener.onSuccess(map);
            }

            @Override
            public void onFailure(int errorCode, String errorMsg) {
                listener.onFailure(errorCode, errorMsg);
            }
        });
    }

    private void loadAd() {
        final String adSlotId = getProvider().getKey2();
        ZplayDebug.d(TAG, "loadAd: adSlotId: " + adSlotId);

        KsyunAdSdk.getInstance().setRewardVideoAdListener(new IKsyunRewardVideoAdListener() {
            @Override
            public void onAdAwardSuccess(String adSlotId) {
                ZplayDebug.d(TAG, "onAdAwardSuccess: " + adSlotId);
                isRewarded = true;
                layerIncentived();
            }

            @Override
            public void onAdAwardFailed(String adSlotId, int errorCode, String errorMsg) {
                ZplayDebug.d(TAG, "onAdAwardFailed: " + adSlotId + ", " + errorCode + ", " + errorMsg);
            }
        });

        KsyunAdSdk.getInstance().setAdListener(new IKsyunAdListener() {
            @Override
            public void onShowSuccess(String adSlotId) {
                ZplayDebug.d(TAG, "onShowSuccess: " + adSlotId);
                isRewarded = false;
                layerExposure();
                layerStartPlaying();
            }

            @Override
            public void onShowFailed(String adSlotId, int errorCode, String errorMsg) {
                ZplayDebug.d(TAG, "onShowFailed: " + adSlotId + ", errorCode: " + errorCode + ", errorMsg: " + errorMsg);
            }

            @Override
            public void onADComplete(String adSlotId) {
                ZplayDebug.d(TAG, "onADComplete: " + adSlotId);
            }

            @Override
            public void onADClick(String adSlotId) {
                ZplayDebug.d(TAG, "onADClick: " + adSlotId);
                layerClicked();
            }

            @Override
            public void onADClose(String adSlotId) {
                ZplayDebug.d(TAG, "onADClose: " + adSlotId);
                layerClosed(isRewarded);
            }
        });

        KsyunAdSdk.getInstance().loadAd(adSlotId, new IKsyunAdLoadListener() {
            @Override
            public void onAdInfoSuccess() {
                ZplayDebug.d(TAG, "onAdInfoSuccess: ");
            }

            @Override
            public void onAdInfoFailed(int errorCode, String errorMsg) {
                ZplayDebug.d(TAG, "onAdInfoFailed: errorCode: " + errorCode + ", errorMsg: " + errorMsg);
                LayerErrorCode error;
                if (errorCode == 2001) {
                    error = LayerErrorCode.ERROR_NO_FILL;
                } else {
                    error = ERROR_INTERNAL;
                }
                AdError adError = new AdError(error);
                adError.setErrorMessage("Ksyun errorMsg: " + errorMsg);
                layerPreparedFailed(adError);
            }

            @Override
            public void onAdLoaded(String adSlotId) {
                ZplayDebug.d(TAG, "onAdLoaded: " + adSlotId);
                layerPrepared();
            }
        });
    }

    @Override
    public String getProviderVersion() {
        return "4.0.3";
    }
}
