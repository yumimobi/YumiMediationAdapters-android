package com.yumi.android.sdk.ads.adapter.unity;

import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;

import java.lang.ref.WeakReference;

import static com.yumi.android.sdk.ads.adapter.unity.UnityAdsListenerObserver.UnityAdStatus.ERROR;
import static com.yumi.android.sdk.ads.adapter.unity.UnityAdsListenerObserver.UnityAdStatus.IDLE;
import static com.yumi.android.sdk.ads.adapter.unity.UnityAdsListenerObserver.UnityAdStatus.READY;

class UnityAdsListenerObserver implements IUnityAdsListener {
    private UnityAdStatus mUnityAdStatus = IDLE;
    private String mPlacementId;
    private UnityAds.UnityAdsError mAdsError;

    private WeakReference<IUnityAdsListener> mObserverRef = new WeakReference<>(null);

    @Override
    public void onUnityAdsReady(String placementId) {
        mPlacementId = placementId;
        mUnityAdStatus = READY;

        IUnityAdsListener observer = mObserverRef.get();
        if (observer != null) {
            observer.onUnityAdsReady(placementId);
        }
    }

    @Override
    public void onUnityAdsStart(String placementId) {
        mPlacementId = placementId;

        IUnityAdsListener observer = mObserverRef.get();
        if (observer != null) {
            observer.onUnityAdsStart(placementId);
        }
    }

    @Override
    public void onUnityAdsFinish(String placementId, UnityAds.FinishState finishState) {
        mPlacementId = placementId;
        mUnityAdStatus = IDLE;

        IUnityAdsListener observer = mObserverRef.get();
        if (observer != null) {
            observer.onUnityAdsFinish(placementId, finishState);
        }
    }

    @Override
    public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String placementId) {
        mAdsError = unityAdsError;
        mPlacementId = placementId;
        mUnityAdStatus = ERROR;

        IUnityAdsListener observer = mObserverRef.get();
        if (observer != null) {
            observer.onUnityAdsError(unityAdsError, placementId);
        }
    }

    void setObserver(IUnityAdsListener listener) {
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
