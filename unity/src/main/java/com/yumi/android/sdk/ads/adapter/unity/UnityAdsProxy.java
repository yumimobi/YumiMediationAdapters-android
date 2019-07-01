package com.yumi.android.sdk.ads.adapter.unity;

import android.app.Activity;

import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;

import java.util.HashMap;
import java.util.Map;

final class UnityAdsProxy {
    private UnityAdsProxy() {
    }

    private static Map<String, UnityAdsListenerObserver> sListeners = new HashMap<>(3);

    private static IUnityAdsListener unityAdsListener = new IUnityAdsListener() {
        @Override
        public void onUnityAdsReady(String s) {
            if (!sListeners.containsKey(s)) {
                sListeners.put(s, new UnityAdsListenerObserver());
            }
            sListeners.get(s).onUnityAdsReady(s);
        }

        @Override
        public void onUnityAdsStart(String s) {
            if (!sListeners.containsKey(s)) {
                sListeners.put(s, new UnityAdsListenerObserver());
            }
            sListeners.get(s).onUnityAdsStart(s);
        }

        @Override
        public void onUnityAdsFinish(String s, UnityAds.FinishState finishState) {
            if (!sListeners.containsKey(s)) {
                sListeners.put(s, new UnityAdsListenerObserver());
            }
            sListeners.get(s).onUnityAdsFinish(s, finishState);
        }

        @Override
        public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String s) {
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

    static void registerUnityAdsListener(String placementId, final IUnityAdsListener listener) {
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
