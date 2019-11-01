package com.yumi.android.sdk.ads.adapter.unity;

import android.app.Activity;

import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAds.FinishState;
import com.unity3d.ads.UnityAds.UnityAdsError;
import com.unity3d.ads.mediation.IUnityAdsExtendedListener;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerInterstitialAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.unity3d.ads.UnityAds.PlacementState.DISABLED;
import static com.unity3d.ads.UnityAds.PlacementState.NO_FILL;
import static com.unity3d.ads.UnityAds.UnityAdsError.INTERNAL_ERROR;
import static com.yumi.android.sdk.ads.adapter.unity.UnityUtil.generateLayerErrorCode;
import static com.yumi.android.sdk.ads.adapter.unity.UnityUtil.sdkVersion;
import static com.yumi.android.sdk.ads.adapter.unity.UnityUtil.updateGDPRStatus;

public class UnityInterstitialAdapter extends YumiCustomerInterstitialAdapter {

    private static final String TAG = "UnityInterstitialAdapter";
    private IUnityAdsExtendedListener mUnityAdsListener;
    // Unity 为自轮询平台，只需要监听第一次回调，以后直接判断 isReady 属性
    private boolean hasHitReadyCallback;

    protected UnityInterstitialAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
        ZplayDebug.d(TAG, "UnityInterstitialAdapter: " + activity + ", gameId: " + getProvider().getKey1());
        UnityAdsProxy.initUnitySDK(getActivity(), getProvider().getKey1());
        mUnityAdsListener = new IUnityAdsExtendedListener() {
            @Override
            public void onUnityAdsClick(String placementId) {
                ZplayDebug.d(TAG, "onUnityAdsClick: " + placementId);

                layerClicked(-99, -99);
            }

            @Override
            public void onUnityAdsPlacementStateChanged(String placementId, UnityAds.PlacementState state1, UnityAds.PlacementState state2) {
                ZplayDebug.d(TAG, "onUnityAdsPlacementStateChanged: " + placementId + ", state1: " + state1 + ", state2: " + state2);
                if (hasHitReadyCallback) {
                    return;
                }

                try {
                    final String targetPlacementId = getProvider().getKey2();
                    ZplayDebug.d(TAG, "onUnityAdsReady: {" + placementId + " should equals " + targetPlacementId + "}");

                    if (state1 == DISABLED) {
                        hasHitReadyCallback = true;
                        layerPreparedFailed(generateLayerErrorCode(INTERNAL_ERROR, "placement state is " + DISABLED));
                        return;
                    }

                    switch (state2) {
                        case READY:
                            hasHitReadyCallback = true;
                            layerPrepared();
                            break;
                        case NO_FILL:
                            hasHitReadyCallback = true;
                            layerPreparedFailed(generateLayerErrorCode(NO_FILL, "placement state is " + state2));
                            break;
                        case DISABLED:
                            hasHitReadyCallback = true;
                            layerPreparedFailed(generateLayerErrorCode(DISABLED, "placement state is " + state2));
                            break;
                        default:
                            ZplayDebug.d(TAG, "onUnityAdsPlacementStateChanged: ignore this state.");
                    }
                } catch (Exception e) {
                    ZplayDebug.d(TAG, "onUnityAdsReady: error: " + e);
                }
            }

            @Override
            public void onUnityAdsReady(String placementId) {
                ZplayDebug.d(TAG, "onUnityAdsReady: " + placementId);
                if (!hasHitReadyCallback) {
                    return;
                }
                // unity 准备完成后要上报准备完成, 起统计作用
                reportPrepared();
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
            public void onUnityAdsError(UnityAdsError unityAdsError, String errorMsg) {
                ZplayDebug.d(TAG, "onUnityAdsError: " + unityAdsError + ", errorMsg: " + errorMsg);

                layerPreparedFailed(generateLayerErrorCode(unityAdsError, errorMsg));
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
        final String placementId = getProvider().getKey2();
        final boolean isReady = UnityAds.isReady(placementId);
        ZplayDebug.d(TAG, "load new interstitial isReady: " + isReady + ", placementId: " + placementId + ", state: " + UnityAds.getPlacementState(placementId));
        updateGDPRStatus(getContext());
        if (isReady) {
            hasHitReadyCallback = true;
            layerPrepared();
        }
        UnityAdsProxy.registerUnityAdsListener(placementId, mUnityAdsListener);
    }

    @Override
    protected void onShowInterstitialLayer(Activity activity) {
        UnityAdsProxy.show(getActivity(), getProvider().getKey2());
    }

    @Override
    protected boolean isInterstitialLayerReady() {
        return UnityAds.isReady(getProvider().getKey2());
    }

    @Override
    protected void init() {
    }

    @Override
    public String getProviderVersion() {
        return sdkVersion();
    }
}
