package com.yumi.android.sdk.ads.adapter.unity;

import com.unity3d.ads.UnityAds;
import com.unity3d.ads.mediation.IUnityAdsExtendedListener;

import java.lang.ref.WeakReference;

import static com.yumi.android.sdk.ads.adapter.unity.UnityAdsListenerObserver.UnityAdStatus.ERROR;
import static com.yumi.android.sdk.ads.adapter.unity.UnityAdsListenerObserver.UnityAdStatus.IDLE;
import static com.yumi.android.sdk.ads.adapter.unity.UnityAdsListenerObserver.UnityAdStatus.READY;

class UnityAdsListenerObserver implements IUnityAdsExtendedListener {
    private UnityAdStatus mUnityAdStatus = IDLE;
    private String mPlacementId;
    private UnityAds.UnityAdsError mAdsError;

    private WeakReference<IUnityAdsExtendedListener> mObserverRef = new WeakReference<>(null);

    @Override
    public void onUnityAdsReady(String placementId) {
        mPlacementId = placementId;
        mUnityAdStatus = READY;

        IUnityAdsExtendedListener observer = mObserverRef.get();
        if (observer != null) {
            observer.onUnityAdsReady(placementId);
        }
    }

    @Override
    public void onUnityAdsStart(String placementId) {
        mPlacementId = placementId;

        IUnityAdsExtendedListener observer = mObserverRef.get();
        if (observer != null) {
            observer.onUnityAdsStart(placementId);
        }
    }

    @Override
    public void onUnityAdsFinish(String placementId, UnityAds.FinishState finishState) {
        mPlacementId = placementId;
        mUnityAdStatus = IDLE;

        IUnityAdsExtendedListener observer = mObserverRef.get();
        if (observer != null) {
            observer.onUnityAdsFinish(placementId, finishState);
        }
    }

    @Override
    public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String placementId) {
        mAdsError = unityAdsError;
        mPlacementId = placementId;
        mUnityAdStatus = ERROR;

        IUnityAdsExtendedListener observer = mObserverRef.get();
        if (observer != null) {
            observer.onUnityAdsError(unityAdsError, placementId);
        }
    }


    @Override
    public void onUnityAdsClick(String placementId) {
        mPlacementId = placementId;

        IUnityAdsExtendedListener observer = mObserverRef.get();
        if (observer != null) {
            observer.onUnityAdsClick(placementId);
        }
    }

    @Override
    public void onUnityAdsPlacementStateChanged(String placementId, UnityAds.PlacementState placementState, UnityAds.PlacementState placementState1) {

    }

    void setObserver(IUnityAdsExtendedListener listener) {
        mObserverRef = new WeakReference<>(listener);
        switch (mUnityAdStatus) {
            case READY:
                listener.onUnityAdsReady(mPlacementId);
                break;
            case ERROR:
                listener.onUnityAdsError(mAdsError, mPlacementId);
                break;
            default:
                break;
        }
    }

    enum UnityAdStatus {
        READY, ERROR, IDLE
    }
}
