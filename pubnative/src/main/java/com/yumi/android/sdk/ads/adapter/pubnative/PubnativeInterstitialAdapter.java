package com.yumi.android.sdk.ads.adapter.pubnative;

import android.app.Activity;

import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerInterstitialAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import net.pubnative.lite.sdk.interstitial.PNInterstitialAd;

import static com.yumi.android.sdk.ads.adapter.pubnative.PubNativeUtil.initPubNativeSDK;
import static com.yumi.android.sdk.ads.adapter.pubnative.PubNativeUtil.recodeError;
import static com.yumi.android.sdk.ads.adapter.pubnative.PubNativeUtil.sdkVersion;
import static com.yumi.android.sdk.ads.adapter.pubnative.PubNativeUtil.updateGDPRStatus;

public class PubnativeInterstitialAdapter extends YumiCustomerInterstitialAdapter {
    private String TAG = "PubnativeInterstitialAdapter";
    private PNInterstitialAd mInterstitial;
    private PNInterstitialAd.Listener mInterstitialListener;

    protected PubnativeInterstitialAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }

    @Override
    protected void onPrepareInterstitial() {
        ZplayDebug.i(TAG, "pubnative request new interstitial key2:" + getProvider().getKey2(), onoff);
        mInterstitial = new PNInterstitialAd(getActivity(), getProvider().getKey2(), mInterstitialListener);

        mInterstitial.load();
    }

    @Override
    protected void onShowInterstitialLayer(Activity activity) {
        if (mInterstitial != null) {
            mInterstitial.show();
        }
    }

    @Override
    protected boolean isInterstitialLayerReady() {
        if (mInterstitial != null) {
            return mInterstitial.isReady();
        }
        return false;
    }

    @Override
    protected void init() {
        ZplayDebug.i(TAG, "pubnative interstitial init key1:" + getProvider().getKey1(), onoff);
        initPubNativeSDK(getProvider().getKey1(), getActivity());
        updateGDPRStatus();
        createInterstitialListener();
    }

    private void createInterstitialListener() {
        mInterstitialListener = new PNInterstitialAd.Listener() {

            @Override
            public void onInterstitialLoaded() {
                ZplayDebug.i(TAG, "pubnative interstitial loaded", onoff);
                layerPrepared();
            }

            @Override
            public void onInterstitialLoadFailed(Throwable throwable) {
                ZplayDebug.i(TAG, "pubnative interstitial loadFailed" + throwable, onoff);
                layerPreparedFailed(recodeError(throwable.toString()));
            }

            @Override
            public void onInterstitialImpression() {
                ZplayDebug.i(TAG, "pubnative interstitial impression", onoff);
                layerStartPlaying();
                layerExposure();
            }

            @Override
            public void onInterstitialDismissed() {
                ZplayDebug.i(TAG, "pubnative interstitial dismissed", onoff);
                layerClosed();
            }

            @Override
            public void onInterstitialClick() {
                ZplayDebug.i(TAG, "pubnative interstitial click", onoff);
                layerClicked(-999f, -999f);
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
    public boolean onActivityBackPressed() {
        return false;
    }

    @Override
    protected void onDestroy() {
        if (mInterstitial != null) {
            mInterstitial.destroy();
        }
    }

    @Override
    public String getProviderVersion() {
        return sdkVersion();
    }
}
