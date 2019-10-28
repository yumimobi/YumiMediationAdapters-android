package com.yumi.android.sdk.ads.adapter.unity;

import com.unity3d.ads.UnityAds;
import com.unity3d.ads.mediation.IUnityAdsExtendedListener;

import java.lang.ref.WeakReference;

class UnityAdsListenerObserver implements IUnityAdsExtendedListener {
    private String mPlacementId;
    private UnityAds.PlacementState mBefore;
    private UnityAds.PlacementState mAfter;

    private WeakReference<IUnityAdsExtendedListener> mObserverRef = new WeakReference<>(null);

    @Override
    public void onUnityAdsReady(String placementId) {
        // 通过 onUnityAdsPlacementStateChanged 来确定广告是否就绪，而不能使用 onUnityAdsReady 原因：
        // 没有专有回调通知某广告位加载失败，因为加载失败后，unity 会在内部重新加载直到成功后会调用 onUnityAdsReady
        // 这样就有可能会导致一直在等待状态
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

        IUnityAdsExtendedListener observer = mObserverRef.get();
        if (observer != null) {
            observer.onUnityAdsFinish(placementId, finishState);
        }
    }

    @Override
    public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String placementId) {
        // Unity 不需要管理 error 状态
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
        mPlacementId = placementId;
        mBefore = placementState;
        mAfter = placementState1;

        IUnityAdsExtendedListener observer = mObserverRef.get();
        if (observer != null) {
            observer.onUnityAdsPlacementStateChanged(placementId, mBefore, mAfter);
        }
    }

    void setObserver(IUnityAdsExtendedListener listener) {
        mObserverRef = new WeakReference<>(listener);
        if (mBefore != null) {
            listener.onUnityAdsPlacementStateChanged(mPlacementId, mBefore, mAfter);
        }
    }
}
