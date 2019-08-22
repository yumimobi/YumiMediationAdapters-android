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
        ZplayDebug.d(TAG, "AppLovin request new media", onoff);
        updateGDPRStatus(getContext());
        isFirstClick = false;
    }

    @Override
    protected void onShowMedia() {
        if (mediaAd != null) {
            ZplayDebug.i(TAG, "AppLovin Media onShowMedia : " + getProvider().getKey2(), onoff);
            mediaAd.show(getActivity(), adRewardListener, adVideoPlaybackListener, adDisplayListener, adClickListener);
        }
    }

    @Override
    protected boolean isMediaReady() {
        if (mediaAd != null) {
            boolean isReady = mediaAd.isAdReadyToDisplay();
            ZplayDebug.i(TAG, "AppLovin Media isMediaReady : " + isReady, onoff);
            return isReady;
        }
        return false;
    }

    @Override
    protected void init() {
        ZplayDebug.i(TAG, "AppLovin Media init sdkKey : " + getProvider().getKey1() + "  ZoneId : " + getProvider().getKey2(), onoff);
        appLovinSDK = ApplovinExtraHolder.getAppLovinSDK(getActivity(), getProvider().getKey1());
        String zoneId = getProvider().getKey2();
        if (appLovinSDK != null && zoneId != null && !"".equals(zoneId)) {
            createMediaListener();
            mediaAd = AppLovinIncentivizedInterstitial.create(zoneId, appLovinSDK);
            preloadAd();
        }
    }

    private void preloadAd() {
        if (mediaAd != null) {
            ZplayDebug.i(TAG, "AppLovin Media preloadAd : " + getProvider().getKey2(), onoff);
            mediaAd.preload(adLoadListener);
        }
    }

    private void createMediaListener() {
        adRewardListener = new AppLovinAdRewardListener() {
            @Override
            public void userRewardVerified(AppLovinAd appLovinAd, Map<String, String> map) {
                ZplayDebug.i(TAG, "AppLovin Media userRewardVerified ", onoff);
                userRewardVerified = true;
                isRewarded = true;
            }

            @Override
            public void userOverQuota(AppLovinAd appLovinAd, Map<String, String> map) {
                ZplayDebug.i(TAG, "AppLovin Media userOverQuota ", onoff);
            }

            @Override
            public void userRewardRejected(AppLovinAd appLovinAd, Map<String, String> map) {
                ZplayDebug.i(TAG, "AppLovin Media userRewardRejected ", onoff);
            }

            @Override
            public void validationRequestFailed(AppLovinAd appLovinAd, int i) {
                ZplayDebug.i(TAG, "AppLovin Media validationRequestFailed ", onoff);
            }

            @Override
            public void userDeclinedToViewAd(AppLovinAd appLovinAd) {
                ZplayDebug.i(TAG, "AppLovin Media userDeclinedToViewAd ", onoff);
            }
        };

        adVideoPlaybackListener = new AppLovinAdVideoPlaybackListener() {
            @Override
            public void videoPlaybackBegan(AppLovinAd appLovinAd) {
                ZplayDebug.i(TAG, "AppLovin Media videoPlaybackBegan ", onoff);
            }

            @Override
            public void videoPlaybackEnded(AppLovinAd appLovinAd, double v, boolean b) {
                ZplayDebug.i(TAG, "AppLovin Media videoPlaybackEnded ", onoff);
            }
        };

        adClickListener = new AppLovinAdClickListener() {
            @Override
            public void adClicked(AppLovinAd appLovinAd) {
                ZplayDebug.i(TAG, "AppLovin Media adClicked ", onoff);
                if (!isFirstClick) {
                    ZplayDebug.d(TAG, "clicked" + appLovinAd.getAdIdNumber(), onoff);
                    layerClicked();
                    isFirstClick = true;
                }
            }
        };

        adDisplayListener = new AppLovinAdDisplayListener() {
            @Override
            public void adDisplayed(AppLovinAd appLovinAd) {
                ZplayDebug.i(TAG, "AppLovin Media adDisplayed ", onoff);
                isRewarded = false;
                layerExposure();
                layerStartPlaying();
            }

            @Override
            public void adHidden(AppLovinAd appLovinAd) {
                ZplayDebug.i(TAG, "AppLovin Media adHidden, userRewardVerified = " + userRewardVerified, onoff);
                if (userRewardVerified) {
                    userRewardVerified = false;
                    layerIncentived();
                }
                layerClosed(isRewarded);
                preloadAd();
            }
        };

        adLoadListener = new AppLovinAdLoadListener() {
            @Override
            public void adReceived(AppLovinAd appLovinAd) {
                ZplayDebug.i(TAG, "AppLovin Media adReceived ZoneID : " + getProvider().getKey2(), onoff);
                layerPrepared();
            }

            @Override
            public void failedToReceiveAd(int errorCode) {
                ZplayDebug.i(TAG, "AppLovin Media failedToReceiveAd ZoneID : " + getProvider().getKey2() + "  ||  errorCode:" + errorCode, onoff);

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
