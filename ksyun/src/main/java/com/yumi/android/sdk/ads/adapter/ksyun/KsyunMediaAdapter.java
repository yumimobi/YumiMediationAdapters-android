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

/**
 * Created by hjl on 2018/7/30.
 */
public class KsyunMediaAdapter extends YumiCustomerMediaAdapter {

    private static final String TAG = "KsyunMediaAdapter";
    private IKsyunAdListener adListener;
    private IKsyunRewardVideoAdListener rewardVideoAdListener;
    private boolean isCompletePlaying = false;

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
        ZplayDebug.d(TAG, "Ksyun request new media", onoff);
        loadAd();
    }

    @Override
    protected void onShowMedia() {
        ZplayDebug.i(TAG, "Ksyun Media onShowMedia : " + getProvider().getKey2(), onoff);
        if (KsyunAdSdk.getInstance().hasAd(getProvider().getKey2())) {
            //广告存在，点击奖励视频入口后，调用showAd接口展示广告
            KsyunAdSdk.getInstance().showAd(getActivity(), getProvider().getKey2());
        } else {
            loadAd();
        }
    }

    @Override
    protected boolean isMediaReady() {
        boolean isReady = KsyunAdSdk.getInstance().hasAd(getProvider().getKey2());
        ZplayDebug.i(TAG, "Ksyun Media isMediaReady : " + isReady, onoff);
        return isReady;
    }

    @Override
    protected void init() {
        ZplayDebug.i(TAG, "Ksyun Media init Key1 : " + getProvider().getKey1() + "  Key2 : " + getProvider().getKey2(), onoff);
        createMediaListener();
        //设置SDK请求环境为线上环境。SDK的init初始化方法，如果不设置config，默认则为沙盒环境
        KsyunAdSdkConfig config = new KsyunAdSdkConfig();
        config.setSdkEnvironment(KsyunAdSdkConfig.RELEASE_ENV); //正式环境
//        config.setSdkEnvironment(KsyunAdSdkConfig.SANDBOX_ENV);  //测试环境
        //设置奖励视频展示过程中，允许出现关闭按钮
        config.setShowCloseBtnOfRewardVideo(false);
        //设置奖励视频展示过程中，出现关闭按钮的时间点
//        config.setCloseBtnComingTimeOfRewardVideo(5);
        KsyunAdSdk.getInstance().init(getActivity(), getProvider().getKey1(), config, new IKsyunAdInitResultListener() {
            @Override
            public void onSuccess(Map<String, String> map) {
                ZplayDebug.i(TAG, "Ksyun SDK init onSuccess : ", onoff);
                //SDK初始化成功，设置事件监听
                KsyunAdSdk.getInstance().setAdListener(adListener);
                KsyunAdSdk.getInstance().setRewardVideoAdListener(rewardVideoAdListener);
                loadAd();
            }

            @Override
            public void onFailure(int errCode, String errMsg) {
                ZplayDebug.i(TAG, "Ksyun SDK init onFailure errCode: " + errCode + "  errMsg:" + errMsg, onoff);
            }
        });
    }

    private void loadAd() {
        ZplayDebug.i(TAG, "Ksyun Media loadAd " + getProvider().getKey2(), onoff);
        KsyunAdSdk.getInstance().loadAd(getProvider().getKey2(), new IKsyunAdLoadListener() {
            @Override
            public void onAdInfoSuccess() {
                ZplayDebug.i(TAG, "Ksyun Media onAdInfoSuccess", onoff);
            }

            @Override
            public void onAdInfoFailed(int erroCode, String erroMsg) {
                ZplayDebug.i(TAG, "Ksyun Media onAdInfoFailed  erroCode: " + erroCode + "   erroMsg: " + erroMsg, onoff);
                LayerErrorCode error;
                if (erroCode == 2001) {
                    error = LayerErrorCode.ERROR_NO_FILL;
                } else {
                    error = LayerErrorCode.ERROR_INTERNAL;
                }
                AdError adError = new AdError(error);
                adError.setErrorMessage("Ksyun errorMsg: " + erroMsg);
                layerPreparedFailed(adError);
            }

            @Override
            public void onAdLoaded(String adSlotId) {
                ZplayDebug.i(TAG, "Ksyun Media onAdLoaded  adSlotId: " + adSlotId, onoff);
                layerPrepared();
            }
        });
    }

    private void createMediaListener() {
        adListener = new IKsyunAdListener() {
            @Override
            public void onShowSuccess(String adSlotId) {
                ZplayDebug.i(TAG, "Ksyun Media onShowSuccess  adSlotId: " + adSlotId, onoff);
                isCompletePlaying = false;
                layerExposure();
                layerStartPlaying();
            }

            @Override
            public void onShowFailed(String adSlotId, int erroCode, String erroMsg) {
                //播放失败,预加载下一个奖励视频
                ZplayDebug.i(TAG, "Ksyun Media onShowFailed  adSlotId: " + adSlotId + "  erroCode : " + erroCode + "   erroMsg : " + erroMsg, onoff);
            }

            @Override
            public void onADComplete(String adSlotId) {
                //播放成功，预加载下一个奖励视频
                ZplayDebug.i(TAG, "Ksyun Media onADComplete  adSlotId: " + adSlotId, onoff);
                isCompletePlaying = true;
            }

            @Override
            public void onADClick(String adSlotId) {
                ZplayDebug.i(TAG, "Ksyun Media onADClick  adSlotId: " + adSlotId, onoff);
                layerClicked();
            }

            @Override
            public void onADClose(String adSlotId) {
                ZplayDebug.i(TAG, "Ksyun Media onADClose  adSlotId: " + adSlotId, onoff);
                layerClosed(isCompletePlaying);
            }
        };

        rewardVideoAdListener = new IKsyunRewardVideoAdListener() {
            @Override
            public void onAdAwardSuccess(String s) {
                ZplayDebug.i(TAG, "Ksyun Media onAdAwardSuccess  s: " + s, onoff);
                layerIncentived();
            }

            @Override
            public void onAdAwardFailed(String s, int i, String s1) {
                ZplayDebug.i(TAG, "Ksyun Media onAdAwardFailed  s: " + s + "  i : " + i + "   s1 : " + s1, onoff);

            }
        };
    }


    @Override
    protected void callOnActivityDestroy() {
    }

}
