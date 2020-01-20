package com.yumi.android.sdk.ads.adapter.atmosplay;

import android.app.Activity;

import com.atmosplayads.AtmosplayRewardVideo;
import com.atmosplayads.listener.AtmosplayAdListener;
import com.atmosplayads.listener.AtmosplayAdLoadListener;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.AdError;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerMediaAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.yumi.android.sdk.ads.adapter.atmosplay.AtmosplayAdsUtil.recodeError;
import static com.yumi.android.sdk.ads.adapter.atmosplay.AtmosplayAdsUtil.sdkVersion;
import static com.yumi.android.sdk.ads.adapter.atmosplay.AtmosplayAdsUtil.updateGDPRStatus;
import static com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode.ERROR_FAILED_TO_SHOW;


/**
 * Created by syj on 2017/10/12.
 */
public class AtmosplayMediaAdapter extends YumiCustomerMediaAdapter {
    private AtmosplayAdLoadListener loadListener;
    private AtmosplayRewardVideo mAtmosplayReward;
    private YumiProviderBean provider;
    private String TAG = "AtmosplayMediaAdapter";
    private boolean isRewarded = false;

    protected AtmosplayMediaAdapter(Activity activity, YumiProviderBean yumiProviderBean) {
        super(activity, yumiProviderBean);
        this.provider = yumiProviderBean;
    }

    @Override
    protected void onPrepareMedia() {
        updateGDPRStatus();
        ZplayDebug.d(TAG, "onPrepareMedia: " + provider.getKey2());
        mAtmosplayReward.loadAd(provider.getKey2(), loadListener);
    }

    @Override
    protected void onShowMedia() {
        AtmosplayRewardVideo.getInstance().show(provider.getKey2(), new AtmosplayAdListener() {
            @Override
            public void onUserEarnedReward() {
                ZplayDebug.d(TAG, "onUserEarnedReward: ");
                isRewarded = true;
                layerIncentived();
            }

            @Override
            public void onAdsError(int errorCode, String message) {
                ZplayDebug.d(TAG, "onAdsError: " + errorCode + ", errorMsg: " + message);
                AdError adError = new AdError(ERROR_FAILED_TO_SHOW);
                adError.setErrorMessage("Atmosplay errorCoed: " + errorCode + "errorMsg: " + message);
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
        final boolean isReady = mAtmosplayReward.isReady(provider.getKey2());
        ZplayDebug.d(TAG, "isMediaReady: " + isReady);
        return isReady;
    }

    @Override
    protected void init() {
        try {
            mAtmosplayReward = AtmosplayRewardVideo.init(getActivity(), provider.getKey1());
            loadListener = new AtmosplayAdLoadListener() {
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
        ZplayDebug.d(TAG, "onDestroy: " + mAtmosplayReward);
        if (mAtmosplayReward != null) {
            mAtmosplayReward.destroy();
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
