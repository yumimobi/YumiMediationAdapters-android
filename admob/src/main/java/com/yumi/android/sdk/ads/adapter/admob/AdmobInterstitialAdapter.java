package com.yumi.android.sdk.ads.adapter.admob;

import android.app.Activity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerInterstitialAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.yumi.android.sdk.ads.adapter.admob.AdMobUtil.getAdRequest;
import static com.yumi.android.sdk.ads.adapter.admob.AdMobUtil.recodeError;

public class AdmobInterstitialAdapter extends YumiCustomerInterstitialAdapter {

    private static final String TAG = "AdmobInterstitialAdapter";
    private InterstitialAd mInterstitialAd;
    private AdListener mAdListener;
    private boolean isReady;

    protected AdmobInterstitialAdapter(Activity activity,
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
    public boolean onActivityBackPressed() {
        return false;
    }

    @Override
    protected void onPrepareInterstitial() {
        ZplayDebug.d(TAG, "admob request new interstitial", onoff);
        if (mInterstitialAd == null) {
            mInterstitialAd = new InterstitialAd(getActivity());
            mInterstitialAd.setAdUnitId(getProvider().getKey1());
            mInterstitialAd.setAdListener(mAdListener);
        }
        AdRequest req = getAdRequest(getContext());
        isReady = false;
        mInterstitialAd.loadAd(req);
    }

    @Override
    protected void onShowInterstitialLayer(Activity activity) {
        mInterstitialAd.show();
    }

    @Override
    protected boolean isInterstitialLayerReady() {
        try {
            return mInterstitialAd != null && mInterstitialAd.isLoaded();
        } catch (Exception e) {
            ZplayDebug.e(TAG, "admob interstitial isInterstitialLayerReady error : ", e, onoff);
            return isReady;
        }
    }

    @Override
    protected void init() {
        ZplayDebug.i(TAG, "unitId : " + getProvider().getKey1(), onoff);
        createAdListener();
    }

    private void createAdListener() {
        mAdListener = new AdListener() {
            @Override
            public void onAdClosed() {
                ZplayDebug.d(TAG, "admob interstitial closed", onoff);
                layerClosed();
                isReady = false;
                super.onAdClosed();
            }

            @Override
            public void onAdOpened() {
                ZplayDebug.d(TAG, "admob interstitial shown", onoff);
                layerExposure();
                layerStartPlaying();
                isReady = false;
                super.onAdOpened();
            }

            @Override
            public void onAdLeftApplication() {
                ZplayDebug.d(TAG, "admob interstitial clicked", onoff);
                layerClicked(-99f, -99f);
                isReady = false;
                super.onAdLeftApplication();
            }

            @Override
            public void onAdLoaded() {
                ZplayDebug.d(TAG, "admob interstitial prepared", onoff);
                layerPrepared();
                isReady = true;
                super.onAdLoaded();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                ZplayDebug.d(TAG, "admob interstitial failed " + errorCode, onoff);
                layerPreparedFailed(recodeError(errorCode));
                isReady = false;
                super.onAdFailedToLoad(errorCode);
            }
        };
    }

}
