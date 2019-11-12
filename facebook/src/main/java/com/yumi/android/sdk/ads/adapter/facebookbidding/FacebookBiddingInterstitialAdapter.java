package com.yumi.android.sdk.ads.adapter.facebookbidding;

import android.app.Activity;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.BidderTokenProvider;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerInterstitialAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.yumi.android.sdk.ads.adapter.facebook.FacebookUtil.initSDK;
import static com.yumi.android.sdk.ads.adapter.facebook.FacebookUtil.recodeError;
import static com.yumi.android.sdk.ads.adapter.facebook.FacebookUtil.sdkVersion;

public class FacebookBiddingInterstitialAdapter extends
        YumiCustomerInterstitialAdapter {

    private static final String TAG = "FacebookBiddingInterstitialAdapter";
    private InterstitialAd interstitial;
    private InterstitialAdListener interstitialListener;

    protected FacebookBiddingInterstitialAdapter(Activity activity,
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
            ZplayDebug.d(TAG, "load new interstitial" + getProvider().getErrCode());
            if (getProvider().getErrCode() != 200) {
                layerPreparedFailed(recodeError(null, getProvider().getErrMessage()), getProvider().getErrMessage());
                return;
            }
            if (interstitial == null) {
                interstitial = new InterstitialAd(getActivity(), getProvider().getKey1());
                interstitial.setAdListener(interstitialListener);
            }
            interstitial.loadAdFromBid(getProvider().getPayload());
        } catch (Exception e) {
            ZplayDebug.e(TAG, "facebook bidding interstitial onPrepareInterstitial error", e);
        }
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
        ZplayDebug.i(TAG, "init placementID : " + getProvider().getKey1() + ",payload:" + getProvider().getPayload());
        initSDK(getContext());
        createListener();
    }

    private void createListener() {
        if (interstitialListener == null) {
            interstitialListener = new InterstitialAdListener() {

                @Override
                public void onError(Ad arg0, AdError arg1) {
                    ZplayDebug.d(TAG, "onError errorMsg: " + arg1.getErrorMessage());

                    layerPreparedFailed(recodeError(arg1), arg1.getErrorMessage());
                }

                @Override
                public void onAdLoaded(Ad arg0) {
                    ZplayDebug.d(TAG, "onAdLoaded");
                    layerPrepared();
                }

                @Override
                public void onAdClicked(Ad arg0) {
                    ZplayDebug.d(TAG, "onAdClicked");
                    layerClicked(-99f, -99f);
                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }

                @Override
                public void onInterstitialDisplayed(Ad arg0) {
                    ZplayDebug.d(TAG, "onInterstitialDisplayed");
                    layerExposure();
                    layerStartPlaying();
                }

                @Override
                public void onInterstitialDismissed(Ad arg0) {
                    ZplayDebug.d(TAG, "onInterstitialDismissed");
                    layerClosed();
                }
            };
        }
    }

    public String getBidderToken() {
        return BidderTokenProvider.getBidderToken(getActivity());
    }

    @Override
    public String getProviderVersion() {
        return sdkVersion();
    }
}