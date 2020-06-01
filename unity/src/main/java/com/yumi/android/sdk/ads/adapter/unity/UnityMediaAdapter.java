package com.yumi.android.sdk.ads.adapter.unity;

import android.app.Activity;

import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAds.FinishState;
import com.unity3d.ads.UnityAds.UnityAdsError;
import com.unity3d.ads.mediation.IUnityAdsExtendedListener;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerMediaAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.unity3d.ads.UnityAds.PlacementState.DISABLED;
import static com.unity3d.ads.UnityAds.PlacementState.NO_FILL;
import static com.unity3d.ads.UnityAds.UnityAdsError.INTERNAL_ERROR;
import static com.yumi.android.sdk.ads.adapter.unity.UnityAdsProxy.initUnitySDK;
import static com.yumi.android.sdk.ads.adapter.unity.UnityUtil.generateLayerErrorCode;
import static com.yumi.android.sdk.ads.adapter.unity.UnityUtil.sdkVersion;
import static com.yumi.android.sdk.ads.adapter.unity.UnityUtil.updateGDPRStatus;

public class UnityMediaAdapter extends YumiCustomerMediaAdapter {

    private static final String TAG = "UnityMediaAdapter";
    private IUnityAdsExtendedListener mUnityAdsListener;
    // Unity 为自轮询平台，只需要监听第一次回调，以后直接判断 isReady 属性
    private boolean hasHitReadyCallback;

    protected UnityMediaAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
        ZplayDebug.d(TAG, "UnityMediaAdapter: " + getActivity() + ", gameId: " + getProvider().getKey1());
        initUnitySDK(getActivity(), getProvider().getKey1());
        mUnityAdsListener = new IUnityAdsExtendedListener() {
            @Override
            public void onUnityAdsClick(String placementId) {
                ZplayDebug.d(TAG, "onUnityAdsClick: " + placementId);

                layerClicked();
            }

            @Override
            public void onUnityAdsPlacementStateChanged(String placementId, UnityAds.PlacementState state1, UnityAds.PlacementState state2) {
                ZplayDebug.d(TAG, "onUnityAdsPlacementStateChanged: " + placementId + ", state1: " + state1 + ", state2: " + state2);
                if (hasHitReadyCallback) {
                    return;
                }

                try {
                    final String targetPlacementId = getProvider().getKey2();
                    ZplayDebug.d(TAG, "onUnityAdsPlacementStateChanged: {" + placementId + " should equals " + targetPlacementId + "}");

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
                layerPrepared();
            }

            @Override
            public void onUnityAdsStart(String placementId) {
                ZplayDebug.d(TAG, "onUnityAdsStart: " + placementId);

                layerExposure();
                layerStartPlaying();
            }

            @Override
            public void onUnityAdsFinish(String placementId, FinishState finishState) {
                ZplayDebug.d(TAG, "onUnityAdsFinish: " + finishState + ", " + placementId);

                boolean isRewarded = false;
                if (finishState == FinishState.COMPLETED) {
                    isRewarded = true;
                    layerIncentived();
                }
                layerClosed(isRewarded);
            }

            @Override
            public void onUnityAdsError(UnityAdsError unityAdsError, String errorMsg) {
                ZplayDebug.d(TAG, "onUnityAdsError: " + unityAdsError + ", errorMsg: " + errorMsg);

                layerPreparedFailed(generateLayerErrorCode(unityAdsError, errorMsg));
            }
        };

        UnityAdsProxy.registerUnityAdsListener(getProvider().getKey2(), mUnityAdsListener);
    }

    @Override
    public void onActivityPause() {
    }

    @Override
    public void onActivityResume() {
    }

    @Override
    protected void onPrepareMedia() {
        final String placementId = getProvider().getKey2();
        final boolean isReady = UnityAds.isReady(placementId);
        ZplayDebug.d(TAG, "load new media isReady: " + isReady + ", placementId: " + placementId + ", state: " + UnityAds.getPlacementState(placementId));
        updateGDPRStatus(getContext());

        if (isReady) {
            hasHitReadyCallback = true;
            layerPrepared();
        }

    }

    @Override
    protected void onShowMedia() {
        UnityAdsProxy.show(getActivity(), getProvider().getKey2());
    }

    @Override
    protected boolean isMediaReady() {
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
