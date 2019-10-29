package com.yumi.android.sdk.ads.adapter.adcolony;

import android.app.Activity;

import com.adcolony.sdk.AdColony;
import com.adcolony.sdk.AdColonyAdOptions;
import com.adcolony.sdk.AdColonyAppOptions;
import com.adcolony.sdk.AdColonyInterstitial;
import com.adcolony.sdk.AdColonyInterstitialListener;
import com.adcolony.sdk.AdColonyReward;
import com.adcolony.sdk.AdColonyRewardListener;
import com.adcolony.sdk.AdColonyZone;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.YumiSettings;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerMediaAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.YumiGDPRStatus;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.yumi.android.sdk.ads.adapter.adcolony.AdcolonyUtil.sdkVersion;

public class AdcolonyMediaAdapter extends YumiCustomerMediaAdapter {

    private static final String TAG = "AdcolonyMediaAdapter";
    private static final String CLIENT_OPTIONS = "version:1.0,store:google";
    private AdColonyInterstitial ad;
    private AdColonyInterstitialListener listener;
    private AdColonyAdOptions ad_options;

    protected AdcolonyMediaAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }

    private void createListeners() {

        listener = new AdColonyInterstitialListener() {
            @Override
            public void onRequestFilled(AdColonyInterstitial ad) {
                ZplayDebug.d(TAG, "onRequestFilled: " + ad);
                AdcolonyMediaAdapter.this.ad = ad;
                layerPrepared();
            }

            @Override
            public void onRequestNotFilled(AdColonyZone zone) {
                ZplayDebug.d(TAG, "onRequestNotFilled: " + zone);
                layerPreparedFailed(AdcolonyUtil.recodeError());

            }

            @Override
            public void onOpened(AdColonyInterstitial ad) {
                ZplayDebug.d(TAG, "onOpened: " + ad);
                ZplayDebug.d(TAG, "onOpened", onoff);
                layerExposure();
                layerStartPlaying();
            }

            @Override
            public void onExpiring(AdColonyInterstitial ad) {
                ZplayDebug.d(TAG, "onExpiring: " + ad);
                ZplayDebug.d(TAG, "onExpiring", onoff);
            }
        };
    }

    @Override
    public void onActivityPause() {
    }

    @Override
    public void onActivityResume() {

    }

    @Override
    protected final void onDestroy() {
    }

    @Override
    protected void onPrepareMedia() {
        final String zoneId = getProvider().getKey2();
        ZplayDebug.d(TAG, "onPrepareMedia: " + zoneId);
        AdColony.requestInterstitial(zoneId, listener, ad_options);
    }

    @Override
    protected void onShowMedia() {
        if (ad != null && !ad.isExpired()) {
            ad.show();
        }
    }

    @Override
    protected boolean isMediaReady() {
        return ad != null && !ad.isExpired();
    }

    @Override
    protected void init() {
        final String appId = getProvider().getKey1();
        final String zoneId = getProvider().getKey2();
        ZplayDebug.d(TAG, "init: " + appId + ", zoneId: " + zoneId);
        createListeners();
        final AdColonyAppOptions appOptions = new AdColonyAppOptions().setUserID(CLIENT_OPTIONS);

        if (YumiSettings.getGDPRStatus() != YumiGDPRStatus.UNKNOWN) {
            // https://github.com/AdColony/AdColony-Android-SDK-3/wiki/GDPR#code-example
            appOptions
                    .setGDPRConsentString(YumiSettings.getGDPRStatus().getGDPRValue())
                    .setGDPRRequired(true);
        }
        AdColony.configure(getActivity(), appOptions, appId, zoneId);
        ad_options =
                new AdColonyAdOptions()
                        .enableConfirmationDialog(false)
                        .enableResultsDialog(false);
        AdColony.setRewardListener(new AdColonyRewardListener() {

            @Override
            public void onReward(AdColonyReward arg0) {
                ZplayDebug.d(TAG, "onReward: " + arg0);
                layerIncentived();
                layerClosed(true);
            }
        });
    }

    @Override
    public String getProviderVersion() {
        return sdkVersion();
    }
}
