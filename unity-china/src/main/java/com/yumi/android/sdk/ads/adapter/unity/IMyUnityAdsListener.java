package com.yumi.android.sdk.ads.adapter.unity;

import com.unity3d.ads.UnityAds;

/**
 * Created by hjl on 2017/11/29.
 */

public interface IMyUnityAdsListener {
    public void onUnityAdsError(UnityAds.UnityAdsError error, String message);

    public void onUnityAdsFinish(String zoneId, UnityAds.FinishState result);

    public void onUnityAdsReady(String zoneId);

    public void onUnityAdsStart(String zoneId);
}
