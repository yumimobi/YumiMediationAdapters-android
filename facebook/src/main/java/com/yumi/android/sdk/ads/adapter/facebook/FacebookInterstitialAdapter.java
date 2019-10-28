package com.yumi.android.sdk.ads.adapter.facebook;

import android.app.Activity;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerInterstitialAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.yumi.android.sdk.ads.adapter.facebook.FacebookUtil.initSDK;
import static com.yumi.android.sdk.ads.adapter.facebook.FacebookUtil.recodeError;
import static com.yumi.android.sdk.ads.adapter.facebook.FacebookUtil.sdkVersion;

public class FacebookInterstitialAdapter extends
        YumiCustomerInterstitialAdapter {

    private static final String TAG = "FacebookInstertititalAdapter";
    private InterstitialAd interstitial;
    private InterstitialAdListener interstitialListener;

    protected FacebookInterstitialAdapter(Activity activity,
                                          YumiProviderBean provider) {
        super(activity, provider);
    }

    @Override
    public void onActivityPause() {
    }

    @Override
    public void onActivityResume() {
    }

    @Override
    protected final void onDestroy() {
        if (interstitial != null) {
            interstitial.destroy();
        }
    }

    @Override
    public boolean onActivityBackPressed() {
        return false;
    }

    @Override
    protected void onPrepareInterstitial() {
        try {
            ZplayDebug.d(TAG, "facebook request new interstitial", onoff);
            if (!AudienceNetworkAds.isInitialized(getContext())) {
                initSDK(getContext(), new AudienceNetworkAds.InitListener() {
                    @Override
                    public void onInitialized(AudienceNetworkAds.InitResult initResult) {
                        if (initResult.isSuccess()) {
                            loadAd();
                        }else{
                            layerPreparedFailed(recodeError(AdError.INTERNAL_ERROR,"facebook init errorMsg: " + initResult.getMessage()));
                        }
                    }
                });
                return;
            }

            loadAd();
        } catch (Exception e) {
            ZplayDebug.e(TAG, "facebook interstitial onPrepareInterstitial error", e, onoff);
        }
    }

    private void loadAd() {
        if (interstitial == null) {
            interstitial = new InterstitialAd(getActivity(), getProvider().getKey1());
            interstitial.setAdListener(interstitialListener);
        }
        interstitial.loadAd();
    }

    @Override
    protected void onShowInterstitialLayer(Activity activity) {
        interstitial.show();
    }

    @Override
    protected boolean isInterstitialLayerReady() {
        if (interstitial != null && interstitial.isAdLoaded()) {
            return true;
        }
        return false;
    }

    @Override
    protected void init() {
        ZplayDebug.i(TAG, "placementID : " + getProvider().getKey1(), onoff);

        createListener();
    }

    private void createListener() {
        if (interstitialListener == null) {
            interstitialListener = new InterstitialAdListener() {

                @Override
                public void onError(Ad arg0, AdError arg1) {
                    ZplayDebug.d(TAG, "facebook interstitial failed " + arg1.getErrorMessage(), onoff);

                    layerPreparedFailed(recodeError(arg1));
                }

                @Override
                public void onAdLoaded(Ad arg0) {
                    ZplayDebug.d(TAG, "facebook interstitial prepared", onoff);
                    layerPrepared();
                }

                @Override
                public void onAdClicked(Ad arg0) {
                    ZplayDebug.d(TAG, "facebook interstitial clicked", onoff);
                    layerClicked(-99f, -99f);
                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }

                @Override
                public void onInterstitialDisplayed(Ad arg0) {
                    ZplayDebug.d(TAG, "facebook interstitial shown", onoff);
                    layerExposure();
                    layerStartPlaying();
                }

                @Override
                public void onInterstitialDismissed(Ad arg0) {
                    ZplayDebug.d(TAG, "facebook interstitial closed", onoff);
                    layerClosed();
                }
            };
        }
    }

    @Override
    public String getProviderVersion() {
        return sdkVersion();
    }
}