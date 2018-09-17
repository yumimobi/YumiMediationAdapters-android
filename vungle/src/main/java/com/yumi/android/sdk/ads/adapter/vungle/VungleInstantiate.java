package com.yumi.android.sdk.ads.adapter.vungle;

import android.app.Activity;

import com.vungle.warren.InitCallback;
import com.vungle.warren.Vungle;
import com.vungle.warren.error.VungleException;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

/**
 * vungle 获取对象和初始化类
 */
public class VungleInstantiate {

    private static final String TAG = "VungleExtra";
    private static final boolean onoff = true;

    public static final int ADTYPE_INTERSTITIAL = 1;
    public static final int ADTYPE_MEDIA = 2;

    private static InitCallback interstittalInitCallback;
    private static InitCallback mediaInitCallback;

    private static class VungleInstantiateHolder {
        private static final VungleInstantiate instantiate = new VungleInstantiate();
    }

    static final VungleInstantiate getInstantiate() {
        return VungleInstantiateHolder.instantiate;
    }

    public synchronized void initVungle(Activity activity, final String appid, final int adType) {
        try {
            if (!Vungle.isInitialized()) {
                ZplayDebug.d(TAG, "vungle initVungle appid:" + appid, onoff);
                Vungle.init(appid, activity.getApplicationContext(), new InitCallback() {
                    @Override
                    public void onSuccess() {
                        ZplayDebug.d(TAG, "vungle initVungleSDK onSuccess()", onoff);
                        if (interstittalInitCallback != null) {
                            interstittalInitCallback.onSuccess();
                        }
                        if (mediaInitCallback != null) {
                            mediaInitCallback.onSuccess();
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        try {
                            if (adType == ADTYPE_INTERSTITIAL && interstittalInitCallback != null) {
                                interstittalInitCallback.onError(throwable);
                            } else if (adType == ADTYPE_MEDIA && mediaInitCallback != null) {
                                mediaInitCallback.onError(throwable);
                            }
                            VungleException ex = (VungleException) throwable;
                            ZplayDebug.e(TAG, "vungle init onError Throwable ExceptionCode : " + ex.getExceptionCode() + "  || LocalizedMessage : " + ex.getLocalizedMessage(), onoff);
                        } catch (Exception cex) {
                            ZplayDebug.e(TAG, "vungle init onError try error", cex, onoff);
                        }
                    }

                    @Override
                    public void onAutoCacheAdAvailable(String placementReferenceId) {
                        ZplayDebug.d(TAG, "vungle initVungleSDK onAutoCacheAdAvailable placementReferenceId:" + placementReferenceId, onoff);

                    }
                });
                ZplayDebug.d(TAG, "vungle initVungleSDK vungle.init", onoff);
            } else {
                if (adType == ADTYPE_INTERSTITIAL && interstittalInitCallback != null) {
                    interstittalInitCallback.onSuccess();
                } else if (adType == ADTYPE_MEDIA && mediaInitCallback != null) {
                    mediaInitCallback.onSuccess();
                }
                ZplayDebug.d(TAG, "vungle initVungleSDK vungle initialized", onoff);
            }
        } catch (Exception e) {
            ZplayDebug.e(TAG, "vungle initVungle error:", e, onoff);
        }
    }

    public static void setInterstittalInitCallback(InitCallback initCallback) {
        interstittalInitCallback = initCallback;
    }

    public static void setMeidaInitCallback(InitCallback initCallback) {
        mediaInitCallback = initCallback;
    }
}
