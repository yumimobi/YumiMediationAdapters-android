package com.yumi.android.sdk.ads.adapter.applovin;


import android.app.Activity;

import com.applovin.adview.AppLovinInterstitialAd;
import com.applovin.adview.AppLovinInterstitialAdDialog;
import com.applovin.sdk.AppLovinAd;
import com.applovin.sdk.AppLovinAdClickListener;
import com.applovin.sdk.AppLovinAdDisplayListener;
import com.applovin.sdk.AppLovinAdLoadListener;
import com.applovin.sdk.AppLovinAdVideoPlaybackListener;
import com.applovin.sdk.AppLovinSdk;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerInterstitialAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.yumi.android.sdk.ads.adapter.applovin.ApplovinUtil.recodeError;
import static com.yumi.android.sdk.ads.adapter.applovin.ApplovinUtil.sdkVersion;
import static com.yumi.android.sdk.ads.adapter.applovin.ApplovinUtil.updateGDPRStatus;

public class ApplovinInterstitialAdapter extends YumiCustomerInterstitialAdapter {

    private static final String TAG = "ApplovinInterstitialAdapter";
    private AppLovinSdk appLovinSDK;
    private AppLovinInterstitialAdDialog interstitialAd;
    private AppLovinAd currentAd = null;

    private AppLovinAdClickListener adClickListener;
    private AppLovinAdDisplayListener adDisplayListener;
    private AppLovinAdVideoPlaybackListener adVideoPlaybackListener;

    private boolean isFirstClick = false;
    private boolean isPrepared = false;

    protected ApplovinInterstitialAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
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
    protected void onPrepareInterstitial() {
        ZplayDebug.d(TAG, "load new interstitial ");
        updateGDPRStatus(getContext());
        isFirstClick = false;
        String zoneId = getProvider().getKey2();
        if (appLovinSDK != null && zoneId != null && !"".equals(zoneId)) {
            if (isPrepared && currentAd != null) {
                layerPrepared();
            }
            ZplayDebug.d(TAG, "loadNextAdForZoneId ZoneId : " + zoneId);
            appLovinSDK.getAdService().loadNextAdForZoneId(zoneId, new AppLovinAdLoadListener() {
                @Override
                public void adReceived(AppLovinAd ad) {
                    ZplayDebug.i(TAG, "adReceived :" + getProvider().getKey2());
                    currentAd = ad;
                    if (!isPrepared) {
                        layerPrepared();
                        isPrepared = true;
                    }
                }

                @Override
                public void failedToReceiveAd(int errorCode) {
                    ZplayDebug.i(TAG, "failedToReceiveAd ZoneID : " + getProvider().getKey2() + "  || errorCode : " + errorCode);
                    layerPreparedFailed(recodeError(errorCode));
                }
            });
        }
    }

    @Override
    protected void onShowInterstitialLayer(Activity activity) {
        ZplayDebug.d(TAG, "onShowInterstitialLayer ");
        if (interstitialAd != null) {
            if (currentAd != null) {
                interstitialAd.showAndRender(currentAd);
                ZplayDebug.i(TAG, "Show ZoneId : " + getProvider().getKey2());
            } else {
                ZplayDebug.i(TAG, "Show The currentAd  is null");
            }
        }
        isPrepared = false;
    }

    @Override
    protected boolean isInterstitialLayerReady() {
        if (currentAd != null) {
            ZplayDebug.i(TAG, "isInterstitialLayerReady true");
            return true;
        }
        return false;
    }

    @Override
    protected void init() {
        ZplayDebug.i(TAG, "init  sdkKey : " + getProvider().getKey1() + "  ZoneId : " + getProvider().getKey2());
        appLovinSDK = ApplovinExtraHolder.getAppLovinSDK(getActivity(), getProvider().getKey1());
        if (appLovinSDK != null) {
            createAppLovinListener();
            interstitialAd = AppLovinInterstitialAd.create(appLovinSDK, getActivity());
            interstitialAd.setAdClickListener(adClickListener);
            interstitialAd.setAdDisplayListener(adDisplayListener);
            interstitialAd.setAdVideoPlaybackListener(adVideoPlaybackListener);
        }
    }

    private void createAppLovinListener() {
        adClickListener = new AppLovinAdClickListener() {
            @Override
            public void adClicked(AppLovinAd appLovinAd) {
                if (!isFirstClick) {
                    ZplayDebug.d(TAG, "adClicked");
                    layerClicked(-99f, -99f);
                    isFirstClick = true;
                }
            }
        };
        adDisplayListener = new AppLovinAdDisplayListener() {
            @Override
            public void adDisplayed(AppLovinAd appLovinAd) {
                ZplayDebug.d(TAG, "adDisplayed");
                layerExposure();
                layerStartPlaying();
            }

            @Override
            public void adHidden(AppLovinAd appLovinAd) {
                ZplayDebug.d(TAG, "adHidden");

                //need reset currentAd = null，if not isInterstitialLayerReady() method will always return true
                currentAd = null;
                layerClosed();

            }
        };
        adVideoPlaybackListener = new AppLovinAdVideoPlaybackListener() {
            @Override
            public void videoPlaybackBegan(AppLovinAd appLovinAd) {
                ZplayDebug.d(TAG, "videoPlaybackBegan");
            }

            @Override
            public void videoPlaybackEnded(AppLovinAd appLovinAd, double v, boolean b) {
                ZplayDebug.d(TAG, "videoPlaybackEnded");
            }
        };
    }

    @Override
    protected void onDestroy() {
        ApplovinExtraHolder.destroyHolder();
        appLovinSDK = null;
        interstitialAd = null;
        currentAd = null;
    }

    @Override
    public String getProviderVersion() {
        return sdkVersion();
    }
}
