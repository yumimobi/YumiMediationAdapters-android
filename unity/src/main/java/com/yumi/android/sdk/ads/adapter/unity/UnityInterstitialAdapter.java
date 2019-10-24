package com.yumi.android.sdk.ads.adapter.unity;

import android.app.Activity;
import android.text.TextUtils;

import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAds.FinishState;
import com.unity3d.ads.UnityAds.UnityAdsError;
import com.unity3d.ads.mediation.IUnityAdsExtendedListener;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerInterstitialAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.yumi.android.sdk.ads.adapter.unity.UnityUtil.generateLayerErrorCode;
import static com.yumi.android.sdk.ads.adapter.unity.UnityUtil.sdkVersion;
import static com.yumi.android.sdk.ads.adapter.unity.UnityUtil.updateGDPRStatus;

public class UnityInterstitialAdapter extends YumiCustomerInterstitialAdapter {

    private static final String TAG = "UnityInterstitialAdapter";
    private IUnityAdsExtendedListener mUnityAdsListener;

    protected UnityInterstitialAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
        UnityAdsProxy.initUnitySDK(getActivity(), getProvider().getKey1());
        mUnityAdsListener = new IUnityAdsExtendedListener() {
            @Override
            public void onUnityAdsClick(String placementId) {
                ZplayDebug.d(TAG, "onUnityAdsClick: " + placementId);

                layerClicked(-99, -99);
            }

            @Override
            public void onUnityAdsPlacementStateChanged(String placementId, UnityAds.PlacementState placementState, UnityAds.PlacementState placementState1) {
                ZplayDebug.d(TAG, "onUnityAdsPlacementStateChanged: " + placementId + ", placementState: " + placementState + ", placementState1: " + placementState1);
            }

            @Override
            public void onUnityAdsReady(String placementId) {
                ZplayDebug.d(TAG, "onUnityAdsReady: " + placementId);
                try {
                    if (TextUtils.equals(placementId, getProvider().getKey2())) {
                        layerPrepared();
                    }
                } catch (Exception e) {
                    ZplayDebug.d(TAG, "onUnityAdsReady: error: " + e);
                }
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
        if (UnityAdsProxy.isReady(getProvider().getKey2())) {
            layerPrepared();
        }
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
    public String getProviderVersion() {
        return sdkVersion();
    }
}
