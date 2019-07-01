package com.yumi.android.sdk.ads.adapter.unity;

import android.app.Activity;

import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds.FinishState;
import com.unity3d.ads.UnityAds.UnityAdsError;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerInterstitialAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.yumi.android.sdk.ads.adapter.unity.UnityUtil.generateLayerErrorCode;
import static com.yumi.android.sdk.ads.adapter.unity.UnityUtil.updateGDPRStatus;

public class UnityInterstitialAdapter extends YumiCustomerInterstitialAdapter {

    private static final String TAG = "UnityInterstitialAdapter";
    private IUnityAdsListener mUnityAdsListener;

    protected UnityInterstitialAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
        UnityAdsProxy.initUnitySDK(getActivity(), getProvider().getKey1());
        mUnityAdsListener = new IUnityAdsListener() {
            @Override
            public void onUnityAdsReady(String placementId) {
                ZplayDebug.d(TAG, "onUnityAdsReady: " + placementId);

                layerPrepared();
            }

            @Override
            public void onUnityAdsStart(String placementId) {
                ZplayDebug.d(TAG, "onUnityAdsStart: ");

                layerExposure();
                layerStartPlaying();
            }

            @Override
            public void onUnityAdsFinish(String placementId, FinishState finishState) {
                ZplayDebug.d(TAG, "onUnityAdsFinish: " + finishState);

                layerClosed();
            }

            @Override
            public void onUnityAdsError(UnityAdsError unityAdsError, String placementId) {
                ZplayDebug.d(TAG, "onUnityAdsError: " + unityAdsError);

                layerPreparedFailed(generateLayerErrorCode(unityAdsError, placementId));
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
    public boolean onActivityBackPressed() {
        return false;
    }

    @Override
    protected void onPrepareInterstitial() {
        ZplayDebug.d(TAG, "unity Interstitial request new", onoff);
        updateGDPRStatus(getContext());

        UnityAdsProxy.registerUnityAdsListener(getProvider().getKey2(), mUnityAdsListener);
    }

    @Override
    protected void onShowInterstitialLayer(Activity activity) {
        UnityAdsProxy.show(getActivity(), getProvider().getKey2());
    }

    @Override
    protected boolean isInterstitialLayerReady() {
        return UnityAdsProxy.isReady(getProvider().getKey2());
    }

    @Override
    protected void init() {
    }

    @Override
    protected void callOnActivityDestroy() {
        UnityAdsProxy.unregisterUnityAdsListener(getProvider().getKey2());
    }
}
