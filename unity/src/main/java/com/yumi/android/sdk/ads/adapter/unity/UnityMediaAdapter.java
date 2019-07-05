package com.yumi.android.sdk.ads.adapter.unity;

import android.app.Activity;

import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds.FinishState;
import com.unity3d.ads.UnityAds.UnityAdsError;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerMediaAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.yumi.android.sdk.ads.adapter.unity.UnityUtil.generateLayerErrorCode;
import static com.yumi.android.sdk.ads.adapter.unity.UnityUtil.updateGDPRStatus;

public class UnityMediaAdapter extends YumiCustomerMediaAdapter {

    private static final String TAG = "UnityMediaAdapter";
    private IUnityAdsListener mUnityAdsListener;

    protected UnityMediaAdapter(Activity activity, YumiProviderBean provider) {
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
            public void onUnityAdsError(UnityAdsError unityAdsError, String placementId) {
                ZplayDebug.d(TAG, "onUnityAdsError: " + unityAdsError + ", placementId: " + placementId);

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
    protected void onPrepareMedia() {
        ZplayDebug.d(TAG, "unity media request new media", onoff);
        updateGDPRStatus(getContext());

        UnityAdsProxy.registerUnityAdsListener(getProvider().getKey2(), mUnityAdsListener);
    }

    @Override
    protected void onShowMedia() {
        UnityAdsProxy.show(getActivity(), getProvider().getKey2());
    }

    @Override
    protected boolean isMediaReady() {
        return UnityAdsProxy.isReady(getProvider().getKey2());
    }

    @Override
    protected void init() {
    }

    @Override
    protected void callOnActivityDestroy() {
    }

}
