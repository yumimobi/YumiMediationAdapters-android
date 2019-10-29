package com.yumi.android.sdk.ads.adapter.applovin;

import android.app.Activity;

import com.applovin.adview.AppLovinIncentivizedInterstitial;
import com.applovin.sdk.AppLovinAd;
import com.applovin.sdk.AppLovinAdClickListener;
import com.applovin.sdk.AppLovinAdDisplayListener;
import com.applovin.sdk.AppLovinAdLoadListener;
import com.applovin.sdk.AppLovinAdRewardListener;
import com.applovin.sdk.AppLovinAdVideoPlaybackListener;
import com.applovin.sdk.AppLovinSdk;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerMediaAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import java.util.Map;

import static com.yumi.android.sdk.ads.adapter.applovin.ApplovinUtil.recodeError;
import static com.yumi.android.sdk.ads.adapter.applovin.ApplovinUtil.sdkVersion;
import static com.yumi.android.sdk.ads.adapter.applovin.ApplovinUtil.updateGDPRStatus;

public class ApplovinMediaAdapter extends YumiCustomerMediaAdapter {

    private static final String TAG = "ApplovinMediaAdapter";
    private AppLovinSdk appLovinSDK;
    private AppLovinIncentivizedInterstitial mediaAd;
    private AppLovinAdLoadListener adLoadListener;

    private boolean userRewardVerified;
    private AppLovinAdRewardListener adRewardListener;
    private AppLovinAdVideoPlaybackListener adVideoPlaybackListener;
    private AppLovinAdDisplayListener adDisplayListener;
    private AppLovinAdClickListener adClickListener;
    private boolean isRewarded = false;

    private boolean isFirstClick = false;

    protected ApplovinMediaAdapter(Activity activity, YumiProviderBean provider) {
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
        ZplayDebug.d(TAG, "load new media");
        updateGDPRStatus(getContext());
        isFirstClick = false;
        if (mediaAd != null) {
            ZplayDebug.i(TAG, "loadAd : " + getProvider().getKey2());
            mediaAd.preload(adLoadListener);
        }
    }

    @Override
    protected void onShowMedia() {
        if (mediaAd != null) {
            ZplayDebug.i(TAG, "onShowMedia : " + getProvider().getKey2());
            mediaAd.show(getActivity(), adRewardListener, adVideoPlaybackListener, adDisplayListener, adClickListener);
        }
    }

    @Override
    protected boolean isMediaReady() {
        if (mediaAd != null) {
            boolean isReady = mediaAd.isAdReadyToDisplay();
            ZplayDebug.i(TAG, "isMediaReady : " + isReady);
            return isReady;
        }
        return false;
    }

    @Override
    protected void init() {
        ZplayDebug.i(TAG, "init sdkKey : " + getProvider().getKey1() + "  ZoneId : " + getProvider().getKey2());
        appLovinSDK = ApplovinExtraHolder.getAppLovinSDK(getActivity(), getProvider().getKey1());
        createMediaListener();
        String zoneId = getProvider().getKey2();
        if (appLovinSDK != null && zoneId != null && !"".equals(zoneId)) {
            mediaAd = AppLovinIncentivizedInterstitial.create(zoneId, appLovinSDK);
        }
    }

    private void createMediaListener() {
        adRewardListener = new AppLovinAdRewardListener() {
            @Override
            public void userRewardVerified(AppLovinAd appLovinAd, Map<String, String> map) {
                ZplayDebug.i(TAG, "userRewardVerified ");
                userRewardVerified = true;
                isRewarded = true;
            }

            @Override
            public void userOverQuota(AppLovinAd appLovinAd, Map<String, String> map) {
                ZplayDebug.i(TAG, "userOverQuota ");
            }

            @Override
            public void userRewardRejected(AppLovinAd appLovinAd, Map<String, String> map) {
                ZplayDebug.i(TAG, "userRewardRejected ");
            }

            @Override
            public void validationRequestFailed(AppLovinAd appLovinAd, int i) {
                ZplayDebug.i(TAG, "validationRequestFailed ");
            }

            @Override
            public void userDeclinedToViewAd(AppLovinAd appLovinAd) {
                ZplayDebug.i(TAG, "userDeclinedToViewAd ");
            }
        };

        adVideoPlaybackListener = new AppLovinAdVideoPlaybackListener() {
            @Override
            public void videoPlaybackBegan(AppLovinAd appLovinAd) {
                ZplayDebug.i(TAG, "videoPlaybackBegan ");
            }

            @Override
            public void videoPlaybackEnded(AppLovinAd appLovinAd, double v, boolean b) {
                ZplayDebug.i(TAG, "videoPlaybackEnded ");
            }
        };

        adClickListener = new AppLovinAdClickListener() {
            @Override
            public void adClicked(AppLovinAd appLovinAd) {
                ZplayDebug.i(TAG, "adClicked ");
                if (!isFirstClick) {
                    ZplayDebug.d(TAG, "clicked" + appLovinAd.getAdIdNumber());
                    layerClicked();
                    isFirstClick = true;
                }
            }
        };

        adDisplayListener = new AppLovinAdDisplayListener() {
            @Override
            public void adDisplayed(AppLovinAd appLovinAd) {
                ZplayDebug.i(TAG, "Exposure ");
                isRewarded = false;
                layerExposure();
                layerStartPlaying();
            }

            @Override
            public void adHidden(AppLovinAd appLovinAd) {
                ZplayDebug.i(TAG, "adHidden, userRewardVerified = " + userRewardVerified);
                if (userRewardVerified) {
                    userRewardVerified = false;
                    layerIncentived();
                }
                layerClosed(isRewarded);
            }
        };

        adLoadListener = new AppLovinAdLoadListener() {
            @Override
            public void adReceived(AppLovinAd appLovinAd) {
                ZplayDebug.i(TAG, "prepared ZoneID : " + getProvider().getKey2());
                layerPrepared();
            }

            @Override
            public void failedToReceiveAd(int errorCode) {
                ZplayDebug.i(TAG, "load failed ZoneID : " + getProvider().getKey2() + "  ||  errorCode:" + errorCode);

                layerPreparedFailed(recodeError(errorCode));
            }
        };
    }

    @Override
    protected void onDestroy() {
        ApplovinExtraHolder.destroyHolder();
        appLovinSDK = null;
        mediaAd = null;
    }

    @Override
    public String getProviderVersion() {
        return sdkVersion();
    }
}
