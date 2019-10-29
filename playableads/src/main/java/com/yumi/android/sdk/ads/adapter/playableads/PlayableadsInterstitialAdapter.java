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
import static com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode.ERROR_FAILED_TO_SHOW;

/**
 * Created by syj on 2017/10/12.
 */
public class PlayableadsInterstitialAdapter extends YumiCustomerInterstitialAdapter {
    private PlayPreloadingListener listener;
    private PlayableInterstitial playable;
    private Activity activity;
    private YumiProviderBean provoder;
    private String TAG = "PlayableadsInterstitialAdapter";

    protected PlayableadsInterstitialAdapter(Activity activity, YumiProviderBean yumiProviderBean) {
        super(activity, yumiProviderBean);
        this.activity = activity;
        this.provoder = yumiProviderBean;
    }

    @Override
    protected void onPrepareInterstitial() {
        if (playable != null && listener != null) {
            ZplayDebug.d(TAG, "load new interstitial", onoff);
            playable.requestPlayableAds(provoder.getKey2(), listener);
        }
    }

    @Override
    protected void onShowInterstitialLayer(Activity activity) {
        playable.presentPlayableAd(provoder.getKey2(), new SimplePlayLoadingListener() {
            @Override
            public void playableAdsIncentive() {
                // 广告展示完成，回到原页面，此时可以给用户奖励了。
                ZplayDebug.d(TAG, "Playable media Video playableAdsIncentive: ", onoff);
//                layerIncentived();
            }

            @Override
            public void onAdsError(int errorCode, String message) {
                // 广告展示失败，根据错误码和错误信息定位问题
                ZplayDebug.d(TAG, "Playable media Video Show Error: " + message, onoff);
                AdError adError = new AdError(ERROR_FAILED_TO_SHOW);
                adError.setErrorMessage("Playable error: " + message);
                layerExposureFailed(adError);
            }

            @Override
            public void onVideoFinished() {
                super.onVideoFinished();
                ZplayDebug.d(TAG, "Playable Interstitial Finish: ", onoff);
            }

            @Override
            public void onVideoStart() {
                super.onVideoStart();
                ZplayDebug.d(TAG, "Playable Interstitial Start: ", onoff);
                layerExposure();
                layerStartPlaying();
            }

            @Override
            public void onLandingPageInstallBtnClicked() {
                layerClicked(-99f, -99f);
                super.onLandingPageInstallBtnClicked();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                ZplayDebug.d(TAG, "Playable Interstitial AdClosed: ", onoff);
                layerClosed();
            }
        });
    }

    @Override
    protected boolean isInterstitialLayerReady() {
        if (playable.canPresentAd(provoder.getKey2())) {
            ZplayDebug.d(TAG, "Playable Interstitial isMediaReady true", onoff);
            return true;
        } else {
            return false;
        }
    }


    @Override
    protected void init() {
        try {
            playable = PlayableInterstitial.init(getActivity(), provoder.getKey1());
            playable.setAutoload(false);
            listener = new PlayPreloadingListener() {
                @Override
                public void onLoadFinished() {
                    ZplayDebug.d(TAG, "Playable Interstitial Ready ", onoff);
                    layerPrepared();
                }

                @Override
                public void onLoadFailed(int errorCode, String s) {
                    ZplayDebug.d(TAG, "Playable Interstitial onLoadFailed errorCode：" + errorCode + "   s:" + s, onoff);

                    if (errorCode == 2004) { //ads has filled
                        layerPrepared();
                        return;
                    }

                    layerPreparedFailed(recodeError(errorCode, s));
                }
            };
        } catch (Exception e) {
            ZplayDebug.e(TAG, "Playable Interstitial init error ", e, onoff);
        }
    }

    @Override
    protected void onDestroy() {
        try {
            if (playable != null) {
                ZplayDebug.d(TAG, "Playable Interstitial onDestroy ", onoff);
            }
        } catch (Exception e) {
            ZplayDebug.e(TAG, "Playable Interstitial callOnActivityDestroy error : ", e, onoff);
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
