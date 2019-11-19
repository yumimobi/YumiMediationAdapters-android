package com.yumi.android.sdk.ads.adapter.facebook;

import android.app.Activity;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerBannerAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.facebook.ads.AdError.NO_FILL;
import static com.yumi.android.sdk.ads.adapter.facebook.FacebookUtil.initSDK;
import static com.yumi.android.sdk.ads.adapter.facebook.FacebookUtil.recodeError;
import static com.yumi.android.sdk.ads.adapter.facebook.FacebookUtil.sdkVersion;
import static com.yumi.android.sdk.ads.publish.enumbean.AdSize.BANNER_SIZE_SMART;

public class FacebookBannerAdapter extends YumiCustomerBannerAdapter {

    private static final String TAG = "FacebookBannerAdapter";
    private AdView banner;
    private AdListener bannerListener;

    protected FacebookBannerAdapter(Activity activity, YumiProviderBean provider) {
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
        if (banner != null) {
            banner.destroy();
        }
    }

    @Override
    protected void onPrepareBannerLayer() {
        try {
            ZplayDebug.d(TAG, "load new banner");
            if (!AudienceNetworkAds.isInitialized(getContext())) {
                initSDK(getContext(), new AudienceNetworkAds.InitListener() {
                    @Override
                    public void onInitialized(AudienceNetworkAds.InitResult initResult) {
                        if (initResult.isSuccess()) {
                            loadAd();
                        } else {
                            layerPreparedFailed(recodeError(AdError.INTERNAL_ERROR, "facebook init errorMsg: " + initResult.getMessage()));
                        }
                    }
                });
                return;
            }

            loadAd();
        } catch (Exception e) {
            ZplayDebug.e(TAG, "facebook interstitial onPrepareBanner error", e);
        }
    }

    private void loadAd() {
        if (bannerSize == BANNER_SIZE_SMART) {
            ZplayDebug.d(TAG, "not support smart banner");
            layerPreparedFailed(FacebookUtil.recodeError(NO_FILL, "not support smart banner."));
            return;
        }
        banner = new AdView(getContext(), getProvider().getKey1(), calculateBannerSize());
        banner.setAdListener(bannerListener);
        banner.loadAd();
    }

    @Override
    protected void init() {
        ZplayDebug.i(TAG, "init placementID : " + getProvider().getKey1());
        createBannerListener();
    }

    private void createBannerListener() {
        if (bannerListener == null) {
            bannerListener = new AdListener() {

                @Override
                public void onError(Ad arg0, AdError arg1) {
                    ZplayDebug.d(TAG, "onError ErrorCode: " + arg1.getErrorMessage());
                    layerPreparedFailed(FacebookUtil.recodeError(arg1));
                }

                @Override
                public void onAdLoaded(Ad arg0) {
                    ZplayDebug.d(TAG, "onAdLoaded");
                    layerPrepared(banner, true);
                }

                @Override
                public void onAdClicked(Ad arg0) {
                    ZplayDebug.d(TAG, "onAdClicked");
                    layerClicked(-99f, -99f);
                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }
            };
        }
    }


    private AdSize calculateBannerSize() {
        if (bannerSize == com.yumi.android.sdk.ads.publish.enumbean.AdSize.BANNER_SIZE_320X50) {
            if (isMatchWindowWidth) {
                return AdSize.BANNER_HEIGHT_50;
            }
            return AdSize.BANNER_320_50;
        }
        if (bannerSize == com.yumi.android.sdk.ads.publish.enumbean.AdSize.BANNER_SIZE_728X90) {
            return AdSize.BANNER_HEIGHT_90;
        }
        return AdSize.BANNER_320_50;
    }

    @Override
    public String getProviderVersion() {
        return sdkVersion();
    }
}