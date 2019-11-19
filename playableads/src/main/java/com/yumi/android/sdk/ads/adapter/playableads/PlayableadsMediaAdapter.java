package com.yumi.android.sdk.ads.adapter.playableads;

import android.app.Activity;

import com.playableads.PlayPreloadingListener;
import com.playableads.PlayableAds;
import com.playableads.SimplePlayLoadingListener;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.AdError;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerMediaAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.yumi.android.sdk.ads.adapter.playableads.PlayableAdsUtil.recodeError;
import static com.yumi.android.sdk.ads.adapter.playableads.PlayableAdsUtil.sdkVersion;
import static com.yumi.android.sdk.ads.adapter.playableads.PlayableAdsUtil.updateGDPRStatus;
import static com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode.ERROR_FAILED_TO_SHOW;


/**
 * Created by syj on 2017/10/12.
 */
public class PlayableadsMediaAdapter extends YumiCustomerMediaAdapter {
    private PlayPreloadingListener listener;
    private PlayableAds playable;
    private YumiProviderBean provider;
    private String TAG = "PlayableadsMediaAdapter";
    private boolean isRewarded = false;

    protected PlayableadsMediaAdapter(Activity activity, YumiProviderBean yumiProviderBean) {
        super(activity, yumiProviderBean);
        this.provider = yumiProviderBean;
    }

    @Override
    protected void onPrepareMedia() {
        updateGDPRStatus();
        ZplayDebug.d(TAG, "onPrepareMedia: " + provider.getKey2());
        playable.requestPlayableAds(provider.getKey2(), listener);
    }

    @Override
    protected void onShowMedia() {
        PlayableAds.getInstance().presentPlayableAD(provider.getKey2(), new SimplePlayLoadingListener() {
            @Override
            public void playableAdsIncentive() {
                ZplayDebug.d(TAG, "playableAdsIncentive: ");
                isRewarded = true;
                layerIncentived();
            }

            @Override
            public void onAdsError(int errorCode, String message) {
                ZplayDebug.d(TAG, "onAdsError: " + errorCode + ", errorMsg: " + message);
                AdError adError = new AdError(ERROR_FAILED_TO_SHOW);
                adError.setErrorMessage("Playable errorCoed: " + errorCode + "errorMsg: " + message);
                layerExposureFailed(adError);
            }

            @Override
            public void onVideoFinished() {
                ZplayDebug.d(TAG, "onVideoFinished: ");
            }

            @Override
            public void onVideoStart() {
                ZplayDebug.d(TAG, "onVideoStart: ");
                isRewarded = false;
                layerExposure();
                layerStartPlaying();
            }

            @Override
            public void onLandingPageInstallBtnClicked() {
                ZplayDebug.d(TAG, "onLandingPageInstallBtnClicked: ");
                layerClicked();
            }

            @Override
            public void onAdClosed() {
                ZplayDebug.d(TAG, "onAdClosed: ");
                layerClosed(isRewarded);
            }
        });

    }

    @Override
    protected boolean isMediaReady() {
        final boolean isReady = playable.canPresentAd(provider.getKey2());
        ZplayDebug.d(TAG, "isMediaReady: " + isReady);
        return isReady;
    }

    @Override
    protected void init() {
        try {
            playable = PlayableAds.init(getActivity(), provider.getKey1());
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
    public String getProviderVersion() {
        return sdkVersion();
    }
}
