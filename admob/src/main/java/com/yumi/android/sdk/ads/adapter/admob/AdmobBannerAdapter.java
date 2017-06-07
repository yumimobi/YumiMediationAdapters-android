package com.yumi.android.sdk.ads.adapter.admob;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerBannerAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

/**
 * Created by Administrator on 2017/3/23.
 */

public class AdmobBannerAdapter extends YumiCustomerBannerAdapter {

    private static final String TAG = "AdmobBannerAdapter";
    private AdView adView;
    private AdListener adListener;
    private float cx = -99f;
    private float cy = -99f;
    private Activity mActivity;

    protected AdmobBannerAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
        mActivity=activity;
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
        adView = new AdView(getActivity());
        adView.setAdSize(calculateBannerSize());
        adView.setAdUnitId(getProvider().getKey1());
        adView.setAdListener(adListener);
        AdRequest req = new AdRequest.Builder().build();
        adView.loadAd(req);
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
                layerPreparedFailed(decodeErrorCode(errorCode));
                super.onAdFailedToLoad(errorCode);
            }
        };
    }

    private LayerErrorCode decodeErrorCode(int errorCode) {
        switch (errorCode) {
            case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                return LayerErrorCode.ERROR_INTERNAL;
            case AdRequest.ERROR_CODE_INVALID_REQUEST:
                return LayerErrorCode.ERROR_INVALID;
            case AdRequest.ERROR_CODE_NO_FILL:
                return LayerErrorCode.ERROR_NO_FILL;
            case AdRequest.ERROR_CODE_NETWORK_ERROR:
                return LayerErrorCode.ERROR_NETWORK_ERROR;
            default:
                return LayerErrorCode.ERROR_INTERNAL;
        }
    }

    private AdSize calculateBannerSize() {
        if(isMatchWindowWidth && isPortrait(mActivity))
        {
            return AdSize.SMART_BANNER;
        }
        if (bannerSize == com.yumi.android.sdk.ads.publish.enumbean.AdSize.BANNER_SIZE_320X50) {
            return AdSize.BANNER;
        }
        if (bannerSize == com.yumi.android.sdk.ads.publish.enumbean.AdSize.BANNER_SIZE_728X90) {
            return AdSize.LEADERBOARD;
        }
        return AdSize.BANNER;
    }


    private static final boolean isPortrait(Context context){
        try {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            if (dm.widthPixels <= dm.heightPixels) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

}