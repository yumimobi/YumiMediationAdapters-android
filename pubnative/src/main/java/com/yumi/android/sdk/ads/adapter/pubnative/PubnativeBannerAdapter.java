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
    private PNAdView.Listener mBannerListener;

    protected PubnativeBannerAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }

    @Override
    protected void onPrepareBannerLayer() {
        ZplayDebug.i(TAG, "pubnative request new banner key2:" + getProvider().getKey2(), onoff);
        if (bannerSize == BANNER_SIZE_SMART) {
            ZplayDebug.i(TAG, "pubnative not support smart banner", onoff);
            layerPreparedFailed(recodeError("not support smart banner"));
            return;
        }
        mBanner = new PNBannerAdView(getContext());

        mBanner.load(getProvider().getKey2(), mBannerListener);
    }

    @Override
    protected void init() {
        final boolean isInitialized = PNLite.isInitialized();
        ZplayDebug.d(TAG, "onPrepareInterstitial: " + isInitialized + ", appToken: " + getProvider().getKey1());

        if (!isInitialized) {
            initPubNativeSDK(getProvider().getKey1(), getActivity(), new HyBid.InitialisationListener() {
                @Override
                public void onInitialisationFinished(boolean b) {
                    ZplayDebug.d(TAG, "onInitialisationFinished: " + b);
                }
            });
        }

        updateGDPRStatus();
        createBannerListener();
    }

    private void createBannerListener() {
        mBannerListener = new PNAdView.Listener() {
            @Override
            public void onAdLoaded() {
                ZplayDebug.i(TAG, "pubnative banner onAdLoaded", onoff);
                layerPrepared(mBanner, true);
            }

            @Override
            public void onAdLoadFailed(Throwable throwable) {
                ZplayDebug.i(TAG, "pubnative banner onAdLoadFailed", onoff);
                layerPreparedFailed(recodeError(throwable.toString()));
            }

            @Override
            public void onAdImpression() {
                ZplayDebug.i(TAG, "pubnative banner onAdImpression", onoff);
            }

            @Override
            public void onAdClick() {
                ZplayDebug.i(TAG, "pubnative banner onAdClick", onoff);
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
