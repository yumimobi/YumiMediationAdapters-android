package com.yumi.android.sdk.ads.adapter.unity;

import com.unity3d.ads.UnityAds;

/**
 * Created by hjl on 2017/11/29.
 */

public interface IMyUnityAdsListener {
    void onUnityAdsError(UnityAds.UnityAdsError error, String message);

    void onUnityAdsFinish(String zoneId, UnityAds.FinishState result);

    void onUnityAdsReady(String zoneId);

    void onUnityAdsStart(String zoneId);
}
