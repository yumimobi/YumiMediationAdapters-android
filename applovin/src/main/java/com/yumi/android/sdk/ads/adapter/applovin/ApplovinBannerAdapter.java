package com.yumi.android.sdk.ads.adapter.applovin;

import android.app.Activity;

import com.applovin.adview.AppLovinAdView;
import com.applovin.adview.AppLovinAdViewDisplayErrorCode;
import com.applovin.adview.AppLovinAdViewEventListener;
import com.applovin.sdk.AppLovinAd;
import com.applovin.sdk.AppLovinAdClickListener;
import com.applovin.sdk.AppLovinAdDisplayListener;
import com.applovin.sdk.AppLovinAdLoadListener;
import com.applovin.sdk.AppLovinAdSize;
import com.applovin.sdk.AppLovinErrorCodes;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkSettings;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerBannerAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

/**
 * Created by hjl on 2018/9/12.
 */
public class ApplovinBannerAdapter extends YumiCustomerBannerAdapter {

    private static final String TAG = "ApplovinBannerAdapter";
    private AppLovinSdk appLovinSdk;
    private AppLovinAdView adView;
    private AppLovinAdLoadListener adLovinAdLoadListener;
    private AppLovinAdClickListener adClickListener;
    private AppLovinAdDisplayListener adDisplayListener;
    private AppLovinAdViewEventListener adViewEventListener;

    protected ApplovinBannerAdapter(Activity activity, YumiProviderBean yumiProviderBean) {
        super(activity, yumiProviderBean);
    }

    @Override
    protected void onPrepareBannerLayer() {
        try {
            ZplayDebug.d(TAG, "AppLovin banner onPrepareBannerLayer", onoff);
            if (appLovinSdk == null || adView == null) {
                init();
            }
            appLovinSdk.getAdService().loadNextAdForZoneId(getProvider().getKey2(), adLovinAdLoadListener);
        } catch (Exception e) {
            ZplayDebug.e(TAG, "AppLovin banner onPrepareBannerLayer error", e, onoff);
        }
    }

    @Override
    protected void init() {
        ZplayDebug.d(TAG, "AppLovin banner init key1: " + getProvider().getKey1() + ", key2:" + getProvider().getKey2(), onoff);
        try {
            if (appLovinSdk == null || adView == null) {
                appLovinSdk = AppLovinSdk.getInstance(getProvider().getKey1(), new AppLovinSdkSettings(), getContext());
                adView = new AppLovinAdView(appLovinSdk, AppLovinAdSize.BANNER, getActivity());
            }
            createAppLovinListener();
            adView.setAdClickListener(adClickListener);
            adView.setAdDisplayListener(adDisplayListener);
            adView.setAdViewEventListener(adViewEventListener);
        } catch (Exception e) {
            ZplayDebug.e(TAG, "AppLovin banner init error", e, onoff);
        }
    }

    private void createAppLovinListener() {
        adLovinAdLoadListener = new AppLovinAdLoadListener() {
            @Override
            public void adReceived(AppLovinAd appLovinAd) {
                ZplayDebug.d(TAG, "AppLovin banner adReceived", onoff);
                adView.renderAd(appLovinAd);
                layerPrepared(adView, false);
            }

            @Override
            public void failedToReceiveAd(final int errorCode) {
                ZplayDebug.d(TAG, "AppLovin banner failedToReceiveAd  errorCode:" + errorCode, onoff);
                if (errorCode == AppLovinErrorCodes.NO_FILL) {
                    layerPreparedFailed(LayerErrorCode.ERROR_NO_FILL);
                } else {
                    layerPreparedFailed(LayerErrorCode.ERROR_INTERNAL);
                }
            }
        };

        adClickListener = new AppLovinAdClickListener() {
            @Override
            public void adClicked(AppLovinAd appLovinAd) {
                ZplayDebug.d(TAG, "AppLovin banner adClicked", onoff);
                layerClicked(-99f, -99f);
            }
        };
        adDisplayListener = new AppLovinAdDisplayListener() {
            @Override
            public void adDisplayed(AppLovinAd appLovinAd) {
                ZplayDebug.d(TAG, "AppLovin banner adDisplayed", onoff);
                layerExposure();
            }

            @Override
            public void adHidden(AppLovinAd appLovinAd) {
                ZplayDebug.d(TAG, "AppLovin banner adHidden", onoff);
                layerClosed();
            }
        };
        adViewEventListener = new AppLovinAdViewEventListener() {
            @Override
            public void adOpenedFullscreen(AppLovinAd appLovinAd, AppLovinAdView appLovinAdView) {
                ZplayDebug.d(TAG, "AppLovin banner adOpenedFullscreen", onoff);

            }

            @Override
            public void adClosedFullscreen(AppLovinAd appLovinAd, AppLovinAdView appLovinAdView) {
                ZplayDebug.d(TAG, "AppLovin banner adClosedFullscreen", onoff);

            }

            @Override
            public void adLeftApplication(AppLovinAd appLovinAd, AppLovinAdView appLovinAdView) {
                ZplayDebug.d(TAG, "AppLovin banner adLeftApplication", onoff);

            }

            @Override
            public void adFailedToDisplay(AppLovinAd appLovinAd, AppLovinAdView appLovinAdView, AppLovinAdViewDisplayErrorCode appLovinAdViewDisplayErrorCode) {
                ZplayDebug.d(TAG, "AppLovin banner adFailedToDisplay", onoff);

            }
        };
    }

    @Override
    protected void callOnActivityDestroy() {
        try {
            if (adView != null) {
                adView.destroy();
            }
        } catch (Exception e) {
            ZplayDebug.e(TAG, "AppLovin banner callOnActivityDestroy error ", e, onoff);
        }
    }

    @Override
    public void onActivityPause() {
    }

    @Override
    public void onActivityResume() {
    }
}
