package com.yumi.android.sdk.ads.adapter.unity;

import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;

/**
 * Created by hjl on 2017/11/29..
 * 由于unity是单例模式，导致插屏和视频不能同时开启否则IUnityAdsListener会互相覆盖掉。
 * 现在通过当前类，设置同一个IUnityAdsListener，然后通过IMyUnityAdsListener做分发处理
 */
public class UnityListenerFactory {

    private static IUnityAdsListener unityAdsListener;

    private static IMyUnityAdsListener myCpUnityAdsListener;
    private static IMyUnityAdsListener myMediaUnityAdsListener;

    public static IUnityAdsListener getUnityAdsListenerInstance() {
        if (unityAdsListener == null) {
            unityAdsListener = new IUnityAdsListener() {
                @Override
                public void onUnityAdsReady(String s) {
                    if (myCpUnityAdsListener != null) {
                        myCpUnityAdsListener.onUnityAdsReady(s);
                    }
                    if (myMediaUnityAdsListener != null) {
                        myMediaUnityAdsListener.onUnityAdsReady(s);
                    }
                }

                @Override
                public void onUnityAdsStart(String s) {
                    if (myCpUnityAdsListener != null) {
                        myCpUnityAdsListener.onUnityAdsStart(s);
                    }
                    if (myMediaUnityAdsListener != null) {
                        myMediaUnityAdsListener.onUnityAdsStart(s);
                    }
                }

                @Override
                public void onUnityAdsFinish(String s, UnityAds.FinishState finishState) {
                    if (myCpUnityAdsListener != null) {
                        myCpUnityAdsListener.onUnityAdsFinish(s, finishState);
                    }
                    if (myMediaUnityAdsListener != null) {
                        myMediaUnityAdsListener.onUnityAdsFinish(s, finishState);
                    }
                }

                @Override
                public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String s) {
                    if (myCpUnityAdsListener != null) {
                        myCpUnityAdsListener.onUnityAdsError(unityAdsError, s);
                    }
                    if (myMediaUnityAdsListener != null) {
                        myMediaUnityAdsListener.onUnityAdsError(unityAdsError, s);
                    }
                }
            };
        }
        return unityAdsListener;
    }

    public static void setMyCpUnityAdsListener(IMyUnityAdsListener cpUnityAdsListener) {
        myCpUnityAdsListener = cpUnityAdsListener;
    }

    public static void setMyMediaUnityAdsListener(IMyUnityAdsListener mediaUnityAdsListener) {
        myMediaUnityAdsListener = mediaUnityAdsListener;
    }
}
