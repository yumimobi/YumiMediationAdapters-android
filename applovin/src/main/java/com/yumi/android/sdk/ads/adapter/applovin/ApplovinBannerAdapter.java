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
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkSettings;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerBannerAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.AdSize;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.yumi.android.sdk.ads.adapter.applovin.ApplovinUtil.recodeError;
import static com.yumi.android.sdk.ads.adapter.applovin.ApplovinUtil.sdkVersion;
import static com.yumi.android.sdk.ads.adapter.applovin.ApplovinUtil.updateGDPRStatus;

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
            updateGDPRStatus(getContext());
            if (bannerSize == AdSize.BANNER_SIZE_SMART) {
                ZplayDebug.d(TAG, "not support smart banner:");
                layerPreparedFailed(recodeError(-1, "not support smart banner."));
                return;
            }
            ZplayDebug.d(TAG, "load new banner");
            if (appLovinSdk == null || adView == null) {
                init();
            }
            appLovinSdk.getAdService().loadNextAdForZoneId(getProvider().getKey2(), adLovinAdLoadListener);
        } catch (Exception e) {
            ZplayDebug.e(TAG, "onPrepareBannerLayer error", e);
        }
    }

    @Override
    protected void init() {
        ZplayDebug.d(TAG, "init key1: " + getProvider().getKey1() + ", key2:" + getProvider().getKey2());
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
            ZplayDebug.e(TAG, "init error", e);
        }
    }

    private void createAppLovinListener() {
        adLovinAdLoadListener = new AppLovinAdLoadListener() {
            @Override
            public void adReceived(AppLovinAd appLovinAd) {
                ZplayDebug.d(TAG, "adReceived");
                adView.renderAd(appLovinAd);
                layerPrepared(adView, true);
            }

            @Override
            public void failedToReceiveAd(final int errorCode) {
                ZplayDebug.d(TAG, "failedToReceiveAd  errorCode:" + errorCode);

                layerPreparedFailed(recodeError(errorCode));
            }
        };

        adClickListener = new AppLovinAdClickListener() {
            @Override
            public void adClicked(AppLovinAd appLovinAd) {
                ZplayDebug.d(TAG, "adClicked");
                layerClicked(-99f, -99f);
            }
        };
        adDisplayListener = new AppLovinAdDisplayListener() {
            @Override
            public void adDisplayed(AppLovinAd appLovinAd) {
                ZplayDebug.d(TAG, "adDisplayed");
            }

            @Override
            public void adHidden(AppLovinAd appLovinAd) {
                ZplayDebug.d(TAG, "adHidden");
                layerClosed();
            }
        };
        adViewEventListener = new AppLovinAdViewEventListener() {
            @Override
            public void adOpenedFullscreen(AppLovinAd appLovinAd, AppLovinAdView appLovinAdView) {
                ZplayDebug.d(TAG, "adOpenedFullscreen");

            }

            @Override
            public void adClosedFullscreen(AppLovinAd appLovinAd, AppLovinAdView appLovinAdView) {
                ZplayDebug.d(TAG, "adClosedFullscreen");

            }

            @Override
            public void adLeftApplication(AppLovinAd appLovinAd, AppLovinAdView appLovinAdView) {
                ZplayDebug.d(TAG, "adLeftApplication");

            }

            @Override
            public void adFailedToDisplay(AppLovinAd appLovinAd, AppLovinAdView appLovinAdView, AppLovinAdViewDisplayErrorCode appLovinAdViewDisplayErrorCode) {
                ZplayDebug.d(TAG, "adFailedToDisplay");

            }
        };
    }

    @Override
    protected void onDestroy() {
        try {
            if (adView != null) {
                adView.destroy();
            }
        } catch (Exception e) {
            ZplayDebug.e(TAG, "callOnActivityDestroy error ", e);
        }
    }

    @Override
    public void onActivityPause() {
    }

    @Override
    public void onActivityResume() {
    }

    @Override
    public String getProviderVersion() {
        return sdkVersion();
    }
}
