package com.yumi.android.sdk.ads.adapter.ironsource;

import android.app.Activity;

import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.InterstitialListener;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerInterstitialAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

/**
 * Created by hjl on 2018/8/10.
 */
public class IronsourceInterstitialAdapter extends YumiCustomerInterstitialAdapter {

    private static final String TAG = "IronsourceInterstitialAdapter";
    private InterstitialListener adListener;

    protected IronsourceInterstitialAdapter(Activity activity, YumiProviderBean yumiProviderBean) {
        super(activity, yumiProviderBean);
    }

    @Override
    protected void onPrepareInterstitial() {
        ZplayDebug.i(TAG, "IronSource Interstitial onPrepareInterstitial", onoff);
        IronSource.loadInterstitial();
    }

    @Override
    protected void onShowInterstitialLayer(Activity activity) {
        ZplayDebug.i(TAG, "IronSource Interstitial onShowInterstitialLayer", onoff);
        IronSource.showInterstitial();
    }

    @Override
    protected boolean isInterstitialLayerReady() {
        boolean isReady = IronSource.isInterstitialReady();
        ZplayDebug.i(TAG, "IronSource Interstitial isInterstitialLayerReady isReady : "+isReady, onoff);
        return isReady;
    }

    @Override
    protected void init() {
        ZplayDebug.i(TAG, "IronSource Interstitial init Key1 : " + getProvider().getKey1() + "  Key2 : " + getProvider().getKey2(), onoff);
        createMediaListener();
        IronSource.setInterstitialListener(adListener);
        IronSource.init(getActivity(), getProvider().getKey1(), IronSource.AD_UNIT.INTERSTITIAL);
    }


    private void createMediaListener() {
        adListener =new InterstitialListener() {
            /**
             Invoked when Interstitial Ad is ready to be shown after load function was called.
             */
            @Override
            public void onInterstitialAdReady() {
                ZplayDebug.i(TAG, "IronSource Interstitial onInterstitialAdReady", onoff);
                layerPrepared();
            }
            /**
             invoked when there is no Interstitial Ad available after calling load function.
             */
            @Override
            public void onInterstitialAdLoadFailed(IronSourceError error) {
                ZplayDebug.e(TAG, "IronSource Interstitial onInterstitialAdReady getErrorCode : " + error.getErrorCode() + "   || getErrorMessage : " + error.getErrorMessage(), onoff);
                if (error.getErrorCode() == IronSourceError.ERROR_BN_LOAD_NO_FILL) {
                    layerPreparedFailed(LayerErrorCode.ERROR_NO_FILL);
                } else {
                    layerPreparedFailed(LayerErrorCode.ERROR_INTERNAL);
                }
            }
            /**
             Invoked when the Interstitial Ad Unit is opened
             */
            @Override
            public void onInterstitialAdOpened() {
                ZplayDebug.i(TAG, "IronSource Interstitial onInterstitialAdOpened", onoff);
            }
            /*
             * Invoked when the ad is closed and the user is about to return to the application.
             */
            @Override
            public void onInterstitialAdClosed() {
                ZplayDebug.i(TAG, "IronSource Interstitial onInterstitialAdClosed", onoff);
                layerClosed();
            }
            /*
             * Invoked when the ad was opened and shown successfully.
             */
            @Override
            public void onInterstitialAdShowSucceeded() {
                ZplayDebug.i(TAG, "IronSource Interstitial onInterstitialAdShowSucceeded", onoff);
                layerExposure();
            }
            /**
             * Invoked when Interstitial ad failed to show.
             // @param error - An object which represents the reason of showInterstitial failure.
             */
            @Override
            public void onInterstitialAdShowFailed(IronSourceError error) {
                ZplayDebug.e(TAG, "IronSource Interstitial onInterstitialAdShowFailed getErrorCode : " + error.getErrorCode() + "   || getErrorMessage : " + error.getErrorMessage(), onoff);
            }
            /*
             * Invoked when the end user clicked on the interstitial ad.
             */
            @Override
            public void onInterstitialAdClicked() {
                ZplayDebug.i(TAG, "IronSource Interstitial onInterstitialAdClicked", onoff);
                layerClicked(-99f,-99f);
            }
        };
    }

    @Override
    protected void callOnActivityDestroy() {
    }

    @Override
    public void onActivityPause() {
        IronSource.onPause(getActivity());
    }

    @Override
    public void onActivityResume() {
        IronSource.onResume(getActivity());
    }

    @Override
    public boolean onActivityBackPressed() {
        return false;
    }
}
