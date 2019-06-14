package com.yumi.android.sdk.ads.adapter.admob;

import android.app.Activity;
import android.content.res.Resources;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailabilityLight;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.AdError;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerBannerAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.google.android.gms.ads.AdSize.BANNER;
import static com.google.android.gms.ads.AdSize.LEADERBOARD;
import static com.google.android.gms.ads.AdSize.SMART_BANNER;
import static com.yumi.android.sdk.ads.adapter.admob.AdMobUtil.getAdRequest;
import static com.yumi.android.sdk.ads.adapter.admob.AdMobUtil.recodeError;

/**
 * Created by Administrator on 2017/3/23.
 */

public class AdmobBannerAdapter extends YumiCustomerBannerAdapter {

    private static final String TAG = "AdmobBannerAdapter";
    private AdView adView;
    private AdListener adListener;
    private float cx = -99f;
    private float cy = -99f;
    private boolean isSupportGoogleService;
    private AdSize mAdSize;

    protected AdmobBannerAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
        GoogleApiAvailabilityLight googleApiAvailability = GoogleApiAvailabilityLight.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(getContext());
        isSupportGoogleService = resultCode == ConnectionResult.SUCCESS;
    }

    @Override
    public void onActivityPause() {
        if (adView != null) {
            adView.pause();
        }
    }

    @Override
    public void onActivityResume() {
        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    protected final void callOnActivityDestroy() {
        if (adView != null) {
            adView.destroy();
        }
    }

    @Override
    protected void onPrepareBannerLayer() {
        ZplayDebug.d(TAG, "admob request new banner", onoff);
        mAdSize = calculateBannerSize();
        adView = new AdView(getActivity());
        adView.setAdSize(mAdSize);
        adView.setAdUnitId(getProvider().getKey1());
        adView.setAdListener(adListener);
        adView.loadAd(getAdRequest(getContext()));
    }

    @Override
    protected void init() {
        ZplayDebug.i(TAG, "unitId : " + getProvider().getKey1(), onoff);
        createAdListener();
    }

    private void createAdListener() {
        adListener = new AdListener() {
            @Override
            public void onAdClosed() {
                ZplayDebug.d(TAG, "admob banner closed", onoff);
                layerClosed();
                super.onAdClosed();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdLeftApplication() {
                ZplayDebug.d(TAG, "admob banner clicked", onoff);
                layerClicked(cx, cy);
                super.onAdLeftApplication();
            }

            @Override
            public void onAdLoaded() {
                if(isInterruptSmartBanner()){
                    ZplayDebug.d(TAG, "admob smart banner bigger than ad container");
                    layerPreparedFailed(new AdError(LayerErrorCode.ERROR_NO_FILL, "Admob SMART_BANNER compatible error."));
                    return;
                }
                ZplayDebug.d(TAG, "admob banner preapred", onoff);
                layerPrepared(adView, true);
                super.onAdLoaded();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                ZplayDebug.d(TAG, "admob banner failed " + errorCode, onoff);
                layerPreparedFailed(recodeError(errorCode));
                super.onAdFailedToLoad(errorCode);
            }
        };
    }

    private boolean isInterruptSmartBanner(){
        // AdMob SMART_BANNER 有个 bug，在 > 720dp 的设置上返回 90dp 高度的 banner，如果容器小于此值
        // 则广告不显示，与 AdMob 沟通说过几周会发布修复此 bug 的版本（date: 20190614）
        return !isSupportGoogleService && getHeightDp() >= 720 && mAdSize == SMART_BANNER;
    }

    private AdSize calculateBannerSize() {
        switch (bannerSize) {
            case BANNER_SIZE_SMART:
                return SMART_BANNER;
            case BANNER_SIZE_728X90:
                return LEADERBOARD;
            default:
                return BANNER;
        }
    }

    private int getHeightDp() {
        int px = Resources.getSystem().getDisplayMetrics().heightPixels;
        float density = Resources.getSystem().getDisplayMetrics().density;
        return (int) ((px / density) + 0.5);
    }
}