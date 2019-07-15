package com.yumi.android.sdk.ads.adapter.admob;

import android.app.Activity;
import android.content.res.Resources;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailabilityLight;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerBannerAdapter;
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
    protected final void onDestroy() {
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

    private AdSize calculateBannerSize() {
        switch (bannerSize) {
            case BANNER_SIZE_SMART:
                if (!isSupportGoogleService) {
                    return BANNER;
                }
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