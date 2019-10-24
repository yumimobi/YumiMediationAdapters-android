package com.yumi.android.sdk.ads.adapter.unity;

import android.app.Activity;

import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.mediation.IUnityAdsExtendedListener;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import java.util.HashMap;
import java.util.Map;

final class UnityAdsProxy {
    private static final String TAG = "UnityAdsProxy";

    private UnityAdsProxy() {
    }

    private static Map<String, UnityAdsListenerObserver> sListeners = new HashMap<>(3);

    private static IUnityAdsListener unityAdsListener = new IUnityAdsExtendedListener() {
        @Override
        public void onUnityAdsClick(String s) {
            ZplayDebug.d(TAG, "onUnityAdsClick: " + s);
            if (!sListeners.containsKey(s)) {
                sListeners.put(s, new UnityAdsListenerObserver());
            }
            sListeners.get(s).onUnityAdsClick(s);
        }

        @Override
        public void onUnityAdsPlacementStateChanged(String s, UnityAds.PlacementState placementState, UnityAds.PlacementState placementState1) {
            ZplayDebug.d(TAG, "onUnityAdsPlacementStateChanged: " + s + ", state1: " + placementState + ", state2: " + placementState1);
            if (!sListeners.containsKey(s)) {
                sListeners.put(s, new UnityAdsListenerObserver());
            }
            sListeners.get(s).onUnityAdsPlacementStateChanged(s, placementState, placementState1);
        }

        @Override
        public void onUnityAdsReady(String s) {
            ZplayDebug.d(TAG, "onUnityAdsReady: " + s);
            if (!sListeners.containsKey(s)) {
                sListeners.put(s, new UnityAdsListenerObserver());
            }
            sListeners.get(s).onUnityAdsReady(s);
        }

        @Override
        public void onUnityAdsStart(String s) {
            ZplayDebug.d(TAG, "onUnityAdsStart: " + s);
            if (!sListeners.containsKey(s)) {
                sListeners.put(s, new UnityAdsListenerObserver());
            }
            sListeners.get(s).onUnityAdsStart(s);
        }

        @Override
        public void onUnityAdsFinish(String s, UnityAds.FinishState finishState) {
            ZplayDebug.d(TAG, "onUnityAdsFinish: " + s + ", finishState: " + finishState);
            if (!sListeners.containsKey(s)) {
                sListeners.put(s, new UnityAdsListenerObserver());
            }
            sListeners.get(s).onUnityAdsFinish(s, finishState);
        }

        @Override
        public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String s) {
            ZplayDebug.d(TAG, "onUnityAdsError: " + unityAdsError + ", " + s);
            if (!sListeners.containsKey(s)) {
                sListeners.put(s, new UnityAdsListenerObserver());
            }
            sListeners.get(s).onUnityAdsError(unityAdsError, s);
        }
    };

    static void initUnitySDK(Activity activity, String gameId) {
        if (!UnityAds.isInitialized()) {
            UnityAds.initialize(activity, gameId, unityAdsListener);
        }
    }

    static void registerUnityAdsListener(String placementId, final IUnityAdsExtendedListener listener) {
        if (!sListeners.containsKey(placementId)) {
            UnityAdsListenerObserver myListener = new UnityAdsListenerObserver();
            sListeners.put(placementId, myListener);
        }
        sListeners.get(placementId).setObserver(listener);
    }

    static void unregisterUnityAdsListener(String placementId) {
        sListeners.remove(placementId);
    }

    static boolean isReady(String placementId) {
        return UnityAds.isReady(placementId);
    }

    public static void show(Activity activity, String placementId) {
        UnityAds.show(activity, placementId);
    }
}
