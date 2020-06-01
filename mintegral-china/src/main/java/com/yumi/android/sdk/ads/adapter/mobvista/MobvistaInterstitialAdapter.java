package com.yumi.android.sdk.ads.adapter.mobvista;

import android.app.Activity;
import android.text.TextUtils;

import com.mintegral.msdk.MIntegralConstans;
import com.mintegral.msdk.interstitialvideo.out.InterstitialVideoListener;
import com.mintegral.msdk.interstitialvideo.out.MTGInterstitialVideoHandler;
import com.mintegral.msdk.out.InterstitialListener;
import com.mintegral.msdk.out.MTGInterstitialHandler;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.AdError;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerInterstitialAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import java.util.HashMap;

import static com.yumi.android.sdk.ads.adapter.mobvista.Util.sdkVersion;
import static com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode.ERROR_FAILED_TO_SHOW;

public class MobvistaInterstitialAdapter extends YumiCustomerInterstitialAdapter {
    private static final String TAG = "MobvistaInterstitialAdapter";
    private static final String INTERSTITIAL_IMAGE = "2";
    private MTGInterstitialHandler mInterstitialHandler;
    private MTGInterstitialVideoHandler mInterstitialVideoHandler;
    private boolean isReady = false;

    protected MobvistaInterstitialAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }

    @Override
    protected void onPrepareInterstitial() {
        final boolean isInterstitialImage = isInterstitialImage();
        ZplayDebug.d(TAG, "load new ineterstitial isInterstitialImage: " + isInterstitialImage);

        if (isInterstitialImage) {
            initInterstitialHandler();
            if (mInterstitialHandler != null) {
                isReady = false;
                mInterstitialHandler.preload();
            }
        } else {
            initInterstitialVideoHandler();
            if (mInterstitialVideoHandler != null) {
                mInterstitialVideoHandler.load();
            }
        }
    }

    @Override
    protected void onShowInterstitialLayer(Activity activity) {
        if (isInterstitialImage()) {
            if (mInterstitialHandler != null) {
                mInterstitialHandler.show();
            }
        } else {
            if (mInterstitialVideoHandler != null) {
                mInterstitialVideoHandler.show();
            }
        }
    }

    private boolean isInterstitialImage() {
        return TextUtils.equals(INTERSTITIAL_IMAGE, getProvider().getExtraData("inventory").trim());
    }

    @Override
    protected boolean isInterstitialLayerReady() {
        if (isInterstitialImage()) {
            return isReady;
        } else {
            if (mInterstitialVideoHandler != null) {
                return mInterstitialVideoHandler.isReady();
            }
        }
        return false;
    }

    @Override
    protected void init() {
        try {
            final String appId = getProvider().getKey1();
            final String appKey = getProvider().getKey2();
            ZplayDebug.d(TAG, "init: appId: " + appId + ", appKey: " + appKey);
            Util.initSDK(getContext(), appId, appKey);
        } catch (Exception e) {
            ZplayDebug.e(TAG, "init: exception", e);
        }
    }

    private void initInterstitialHandler() {
        if (mInterstitialHandler != null) {
            return;
        }

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(MIntegralConstans.PROPERTIES_UNIT_ID, getProvider().getKey3());
        mInterstitialHandler = new MTGInterstitialHandler(getContext(), hashMap);
        mInterstitialHandler.setInterstitialListener(new InterstitialListener() {

            @Override
            public void onInterstitialLoadSuccess() {
                ZplayDebug.d(TAG, "onInterstitialLoadSuccess: ");
                isReady = true;
                layerPrepared();
            }

            @Override
            public void onInterstitialLoadFail(String errorMsg) {
                ZplayDebug.i(TAG, "onInterstitialLoadFail: " + errorMsg);
                isReady = false;
                AdError error = new AdError(LayerErrorCode.ERROR_NO_FILL);
                error.setErrorMessage("minteral errorMsg: " + errorMsg);
                layerPreparedFailed(error);
            }

            @Override
            public void onInterstitialShowSuccess() {
                ZplayDebug.d(TAG, "onInterstitialShowSuccess: ");
                isReady = false;
                layerStartPlaying();
                layerExposure();
            }

            @Override
            public void onInterstitialShowFail(String errorMsg) {
                ZplayDebug.i(TAG, "onInterstitialShowFail: " + errorMsg);
                AdError adError = new AdError(ERROR_FAILED_TO_SHOW);
                adError.setErrorMessage("Mobvista errorMsg: " + errorMsg);
                layerExposureFailed(adError);
            }

            @Override
            public void onInterstitialClosed() {
                ZplayDebug.d(TAG, "onInterstitialClosed: ");
                layerClosed();
            }

            @Override
            public void onInterstitialAdClick() {
                ZplayDebug.d(TAG, "onInterstitialAdClick: ");
                layerClicked(-999f, -999f);
            }
        });
    }

    private void initInterstitialVideoHandler() {
        if (mInterstitialVideoHandler != null) {
            return;
        }

        mInterstitialVideoHandler = new MTGInterstitialVideoHandler(getContext(), getProvider().getKey4(), getProvider().getKey3());
        mInterstitialVideoHandler.setInterstitialVideoListener(new InterstitialVideoListener() {

            @Override
            public void onLoadSuccess(String s, String s1) {
                ZplayDebug.d(TAG, "onLoadSuccess: " + s);
            }

            @Override
            public void onVideoLoadSuccess(String s, String s1) {
                ZplayDebug.d(TAG, "onVideoLoadSuccess: " + s);
                layerPrepared();
            }

            @Override
            public void onVideoLoadFail(String errorMsg) {
                ZplayDebug.d(TAG, "onVideoLoadFail: " + errorMsg);
                AdError error = new AdError(LayerErrorCode.ERROR_NO_FILL);
                error.setErrorMessage("minteral errorMsg: " + errorMsg);
                layerPreparedFailed(error);
            }

            @Override
            public void onAdShow() {
                ZplayDebug.d(TAG, "onAdShow: ");
                layerStartPlaying();
                layerExposure();
            }

            @Override
            public void onAdClose(boolean b) {
                ZplayDebug.d(TAG, "onAdClose: ");
                layerClosed();
            }

            @Override
            public void onShowFail(String errorMsg) {
                ZplayDebug.d(TAG, "onShowFail: " + errorMsg);
                AdError adError = new AdError(ERROR_FAILED_TO_SHOW);
                adError.setErrorMessage("Mobvista errorMsg: " + errorMsg);
                layerExposureFailed(adError);
            }

            @Override
            public void onVideoAdClicked(String s, String s1) {
                ZplayDebug.d(TAG, "onVideoAdClicked: " + s);
                layerClicked(-999f, -999f);
            }

            @Override
            public void onVideoComplete(String s, String s1) {
                ZplayDebug.d(TAG, "onVideoComplete: " + s);
            }

            @Override
            public void onAdCloseWithIVReward(boolean b, int i) {
                ZplayDebug.d(TAG, "onAdCloseWithIVReward: " + b);
            }

            @Override
            public void onEndcardShow(String s, String s1) {
                ZplayDebug.d(TAG, "onEndcardShow: " + s);
            }

        });
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
    public String getProviderVersion() {
        return sdkVersion();
    }
}
