package com.yumi.android.sdk.ads.adapter.pubnative;

import android.app.Activity;

import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerBannerAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.PNLite;
import net.pubnative.lite.sdk.views.PNAdView;
import net.pubnative.lite.sdk.views.PNBannerAdView;

import static com.yumi.android.sdk.ads.adapter.pubnative.PubNativeUtil.initPubNativeSDK;
import static com.yumi.android.sdk.ads.adapter.pubnative.PubNativeUtil.recodeError;
import static com.yumi.android.sdk.ads.adapter.pubnative.PubNativeUtil.sdkVersion;
import static com.yumi.android.sdk.ads.adapter.pubnative.PubNativeUtil.updateGDPRStatus;
import static com.yumi.android.sdk.ads.publish.enumbean.AdSize.BANNER_SIZE_SMART;

public class PubnativeBannerAdapter extends YumiCustomerBannerAdapter {
    private String TAG = "PubnativeBannerAdapter";
    private PNBannerAdView mBanner;

    protected PubnativeBannerAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }

    @Override
    protected void onPrepareBannerLayer() {
        if (bannerSize == BANNER_SIZE_SMART) {
            ZplayDebug.d(TAG, "onPrepareBannerLayer: not support smart banner");
            layerPreparedFailed(recodeError("not support smart banner"));
            return;
        }

        final String appToken = getProvider().getKey1();
        final boolean isInitialized = PNLite.isInitialized();
        ZplayDebug.d(TAG, "load new banner appToken: " + appToken + ", isInitialized: " + isInitialized);
        if (!isInitialized) {
            initPubNativeSDK(getProvider().getKey1(), getActivity(), new HyBid.InitialisationListener() {
                @Override
                public void onInitialisationFinished(boolean b) {
                    ZplayDebug.d(TAG, "onInitialisationFinished: " + b);
                    if (b) {
                        loadAd();
                    } else {
                        layerPreparedFailed(recodeError("initialisation failed."));
                    }
                }
            });
            return;
        }
        loadAd();
    }

    private void loadAd() {
        final String zoneId = getProvider().getKey2();
        ZplayDebug.d(TAG, "loadAd: " + zoneId);
        mBanner = new PNBannerAdView(getContext());
        mBanner.load(zoneId, new PNAdView.Listener() {
            @Override
            public void onAdLoaded() {
                ZplayDebug.d(TAG, "onAdLoaded: ");
                layerPrepared(mBanner, true);
            }

            @Override
            public void onAdLoadFailed(Throwable throwable) {
                ZplayDebug.d(TAG, "onAdLoadFailed: " + throwable);
                layerPreparedFailed(recodeError(throwable.toString()));
            }

            @Override
            public void onAdImpression() {
                ZplayDebug.d(TAG, "onAdImpression: ");
                ZplayDebug.i(TAG, "pubnative banner onAdImpression", onoff);
            }

            @Override
            public void onAdClick() {
                ZplayDebug.d(TAG, "onAdClick: ");
                layerClicked(-999f, -999f);
            }
        });
    }

    @Override
    protected void init() {
        ZplayDebug.d(TAG, "init: ");
        updateGDPRStatus();
    }


    @Override
    public void onActivityPause() {

    }

    @Override
    public void onActivityResume() {

    }

    @Override
    protected void onDestroy() {
        if (mBanner != null) {
            mBanner.destroy();
        }
    }

    @Override
    public String getProviderVersion() {
        return sdkVersion();
    }
}
