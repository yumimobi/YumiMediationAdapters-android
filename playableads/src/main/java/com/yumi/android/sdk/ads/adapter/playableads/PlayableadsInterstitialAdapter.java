package com.yumi.android.sdk.ads.adapter.playableads;

import android.app.Activity;

import com.playableads.PlayPreloadingListener;
import com.playableads.PlayableInterstitial;
import com.playableads.SimplePlayLoadingListener;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.AdError;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerInterstitialAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.yumi.android.sdk.ads.adapter.playableads.PlayableAdsUtil.recodeError;
import static com.yumi.android.sdk.ads.adapter.playableads.PlayableAdsUtil.sdkVersion;
import static com.yumi.android.sdk.ads.adapter.playableads.PlayableAdsUtil.updateGDPRStatus;
import static com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode.ERROR_FAILED_TO_SHOW;

/**
 * Created by syj on 2017/10/12.
 */
public class PlayableadsInterstitialAdapter extends YumiCustomerInterstitialAdapter {
    private PlayPreloadingListener listener;
    private PlayableInterstitial playable;
    private YumiProviderBean provider;
    private String TAG = "PlayableadsInterstitialAdapter";

    protected PlayableadsInterstitialAdapter(Activity activity, YumiProviderBean yumiProviderBean) {
        super(activity, yumiProviderBean);
        this.provider = yumiProviderBean;
    }

    @Override
    protected void onPrepareInterstitial() {
        updateGDPRStatus();
        ZplayDebug.d(TAG, "onPrepareInterstitial: " + provider.getKey2());
        playable.requestPlayableAds(provider.getKey2(), listener);
    }

    @Override
    protected void onShowInterstitialLayer(Activity activity) {
        playable.presentPlayableAd(provider.getKey2(), new SimplePlayLoadingListener() {
            @Override
            public void playableAdsIncentive() {
                // 广告展示完成，回到原页面，此时可以给用户奖励了。
                ZplayDebug.d(TAG, "playableAdsIncentive: ");
            }

            @Override
            public void onAdsError(int errorCode, String message) {
                // 广告展示失败，根据错误码和错误信息定位问题
                ZplayDebug.d(TAG, "onAdsError: " + errorCode + ", errorMsg: " + message);
                AdError adError = new AdError(ERROR_FAILED_TO_SHOW);
                adError.setErrorMessage("Playable error: " + message);
                layerExposureFailed(adError);
            }

            @Override
            public void onVideoFinished() {
                ZplayDebug.d(TAG, "onVideoFinished: ");
            }

            @Override
            public void onVideoStart() {
                ZplayDebug.d(TAG, "onVideoStart: ");
                layerExposure();
                layerStartPlaying();
            }

            @Override
            public void onLandingPageInstallBtnClicked() {
                ZplayDebug.d(TAG, "onLandingPageInstallBtnClicked: ");
                layerClicked(-99f, -99f);
            }

            @Override
            public void onAdClosed() {
                ZplayDebug.d(TAG, "onAdClosed: ");
                layerClosed();
            }
        });
    }

    @Override
    protected boolean isInterstitialLayerReady() {
        final boolean isReady = playable.canPresentAd(provider.getKey2());
        ZplayDebug.d(TAG, "isInterstitialLayerReady: " + isReady);
        return isReady;
    }


    @Override
    protected void init() {
        try {
            playable = PlayableInterstitial.init(getActivity(), provider.getKey1());
            playable.setAutoload(false);
            listener = new PlayPreloadingListener() {
                @Override
                public void onLoadFinished() {
                    ZplayDebug.d(TAG, "onLoadFinished: ");
                    layerPrepared();
                }

                @Override
                public void onLoadFailed(int errorCode, String s) {
                    ZplayDebug.d(TAG, "onLoadFailed: " + errorCode + ", errorMsg: " + s);

                    if (errorCode == 2004) { //ads has filled
                        layerPrepared();
                        return;
                    }

                    layerPreparedFailed(recodeError(errorCode, s));
                }
            };
        } catch (Exception e) {
            ZplayDebug.d(TAG, "init: error: " + e);
        }
    }

    @Override
    protected void onDestroy() {
        ZplayDebug.d(TAG, "onDestroy: " + playable);
        if (playable != null) {
            playable.destroy();
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
