package com.yumi.android.sdk.ads.adapter.pubnative;

import android.app.Activity;

import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerInterstitialAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.PNLite;
import net.pubnative.lite.sdk.interstitial.PNInterstitialAd;

import static com.yumi.android.sdk.ads.adapter.pubnative.PubNativeUtil.initPubNativeSDK;
import static com.yumi.android.sdk.ads.adapter.pubnative.PubNativeUtil.recodeError;
import static com.yumi.android.sdk.ads.adapter.pubnative.PubNativeUtil.sdkVersion;
import static com.yumi.android.sdk.ads.adapter.pubnative.PubNativeUtil.updateGDPRStatus;

public class PubnativeInterstitialAdapter extends YumiCustomerInterstitialAdapter {
    private String TAG = "PubnativeInterstitialAdapter";
    private PNInterstitialAd mInterstitial;

    // 测试发现，关闭广告后，再次判断 mInterstitial.isReady() 方法，返回值仍为 true，
    // 但是调用 show() 方法没有任何作用。
    private boolean isReady;

    protected PubnativeInterstitialAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }

    @Override
    protected void onPrepareInterstitial() {
        final boolean isInitialized = PNLite.isInitialized();
        ZplayDebug.d(TAG, "onPrepareInterstitial: " + isInitialized + ", appToken: " + getProvider().getKey1());

        if (!isInitialized) {
            initPubNativeSDK(getProvider().getKey1(), getActivity(), new HyBid.InitialisationListener() {
                @Override
                public void onInitialisationFinished(boolean b) {
                    ZplayDebug.d(TAG, "onInitialisationFinished: " + b);
                    if (b) {
                        loadAd();
                    } else {
                        layerPreparedFailed(recodeError("onInitialisationFinished: false"));
                    }
                }
            });
            return;
        }

        loadAd();
    }

    private void loadAd() {
        ZplayDebug.d(TAG, "loadAd: zoneId: " + getProvider().getKey2());
        updateGDPRStatus();
        mInterstitial = new PNInterstitialAd(getActivity(), getProvider().getKey2(), new PNInterstitialAd.Listener() {

            @Override
            public void onInterstitialLoaded() {
                ZplayDebug.d(TAG, "onInterstitialLoaded: ");
                isReady = true;
                layerPrepared();
            }

            @Override
            public void onInterstitialLoadFailed(Throwable throwable) {
                ZplayDebug.d(TAG, "onInterstitialLoadFailed: " + throwable);
                layerPreparedFailed(recodeError(throwable.toString()));
            }

            @Override
            public void onInterstitialImpression() {
                ZplayDebug.d(TAG, "onInterstitialImpression: ");
                layerStartPlaying();
                layerExposure();
            }

            @Override
            public void onInterstitialDismissed() {
                ZplayDebug.d(TAG, "onInterstitialDismissed: ");
                isReady = false;
                layerClosed();
            }

            @Override
            public void onInterstitialClick() {
                ZplayDebug.d(TAG, "onInterstitialClick: ");
                layerClicked(-999f, -999f);
            }
        });
        mInterstitial.load();
    }

    @Override
    protected void onShowInterstitialLayer(Activity activity) {
        ZplayDebug.d(TAG, "onShowInterstitialLayer: " + mInterstitial);
        if (mInterstitial != null) {
            mInterstitial.show();
        }
    }

    @Override
    protected boolean isInterstitialLayerReady() {
        if (mInterstitial != null) {
            return isReady && mInterstitial.isReady();
        }
        return false;
    }

    @Override
    protected void init() {
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
