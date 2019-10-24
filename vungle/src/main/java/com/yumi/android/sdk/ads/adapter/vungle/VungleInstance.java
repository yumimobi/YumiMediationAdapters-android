package com.yumi.android.sdk.ads.adapter.vungle;

import android.app.Activity;
import android.util.Log;

import com.vungle.warren.InitCallback;
import com.vungle.warren.LoadAdCallback;
import com.vungle.warren.PlayAdCallback;
import com.vungle.warren.Vungle;
import com.vungle.warren.error.VungleException;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import java.util.HashMap;
import java.util.Map;

/**
 * vungle 获取对象和初始化类
 */
class VungleInstance {

    static final int ADTYPE_INTERSTITIAL = 1;
    static final int ADTYPE_MEDIA = 2;
    private static final String TAG = "VungleInstance";
    private static InitCallback interstitialInitCallback;
    private static InitCallback mediaInitCallback;

    private static LoadAdCallback mediaLoadAdCallback;
    private static PlayAdCallback mediaPlayAdCallback;

    private static Map<String, LoadAdCallback> myVungleMediaLoadAdListener;
    private static Map<String, PlayAdCallback> myVungleMediaPlayAdListener;

    static VungleInstance getInstance() {
        return VungleInstanceHolder.instance;
    }

    private VungleInstance() {
    }

    static void setInterstitialInitCallback(InitCallback initCallback) {
        interstitialInitCallback = initCallback;
    }

    static void setMediaInitCallback(InitCallback initCallback) {
        mediaInitCallback = initCallback;
    }

    static LoadAdCallback createVungleMediaLoadListener() {
        if (mediaLoadAdCallback == null) {
            mediaLoadAdCallback = new LoadAdCallback() {
                @Override
                public void onAdLoad(String placementReferenceId) {
                    ZplayDebug.d(TAG, "onAdLoad: " + placementReferenceId);
                    if (myVungleMediaLoadAdListener != null) {
                        LoadAdCallback ml = myVungleMediaLoadAdListener.get(placementReferenceId);
                        if (ml != null) {
                            ml.onAdLoad(placementReferenceId);
                        }
                    }
                }

                @Override
                public void onError(String placementReferenceId, Throwable throwable) {
                    ZplayDebug.d(TAG, "onError: " + placementReferenceId + ", error: " + throwable);
                    if (myVungleMediaLoadAdListener != null) {
                        LoadAdCallback ml = myVungleMediaLoadAdListener.get(placementReferenceId);
                        if (ml != null) {
                            ml.onError(placementReferenceId, throwable);
                        }
                    }
                }
            };
        }
        return mediaLoadAdCallback;
    }

    static PlayAdCallback createVungleMediaPlayListener() {
        if (mediaPlayAdCallback == null) {
            mediaPlayAdCallback = new PlayAdCallback() {
                @Override
                public void onAdStart(String placementReferenceId) {
                    Log.d(TAG, "onAdStart: " + placementReferenceId);
                    if (myVungleMediaPlayAdListener != null) {
                        PlayAdCallback mp = myVungleMediaPlayAdListener.get(placementReferenceId);
                        if (mp != null) {
                            mp.onAdStart(placementReferenceId);
                        }
                    }
                }

                @Override
                public void onAdEnd(String placementReferenceId, final boolean completed, final boolean isCTAClicked) {
                    ZplayDebug.d(TAG, "onAdEnd: " + placementReferenceId + ", completed: " + completed + ", isCTAClicked: " + isCTAClicked);
                    if (myVungleMediaPlayAdListener != null) {
                        PlayAdCallback mp = myVungleMediaPlayAdListener.get(placementReferenceId);
                        if (mp != null) {
                            mp.onAdEnd(placementReferenceId, completed, isCTAClicked);
                        }
                    }

                }

                @Override
                public void onError(String placementReferenceId, Throwable throwable) {
                    ZplayDebug.d(TAG, "onError: " + placementReferenceId + ", error: " + throwable);
                    if (myVungleMediaPlayAdListener != null) {
                        PlayAdCallback mp = myVungleMediaPlayAdListener.get(placementReferenceId);
                        if (mp != null) {
                            mp.onError(placementReferenceId, throwable);
                        }
                    }
                }
            };
        }
        return mediaPlayAdCallback;
    }

    static void setMediaLoadAdCallback(String key2, LoadAdCallback ml) {
        if (myVungleMediaLoadAdListener == null) {
            myVungleMediaLoadAdListener = new HashMap<>();
        }
        myVungleMediaLoadAdListener.put(key2, ml);
    }

    static void setMediaPlayAdCallback(String key2, PlayAdCallback mp) {
        if (myVungleMediaPlayAdListener == null) {
            myVungleMediaPlayAdListener = new HashMap<>();
        }
        myVungleMediaPlayAdListener.put(key2, mp);
    }

    synchronized void initVungle(Activity activity, final String appid, final int adType) {
        try {
            final boolean isInitialized = Vungle.isInitialized();
            Log.d(TAG, "initVungle: " + isInitialized + ", appid: " + appid + ", adType: " + adType);
            if (isInitialized) {
                if (adType == ADTYPE_INTERSTITIAL && interstitialInitCallback != null) {
                    interstitialInitCallback.onSuccess();
                } else if (adType == ADTYPE_MEDIA && mediaInitCallback != null) {
                    mediaInitCallback.onSuccess();
                }
                return;
            }

            Vungle.init(appid, activity.getApplicationContext(), new InitCallback() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "onSuccess: ");
                    if (interstitialInitCallback != null) {
                        interstitialInitCallback.onSuccess();
                    }
                    if (mediaInitCallback != null) {
                        mediaInitCallback.onSuccess();
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    try {
                        if (adType == ADTYPE_INTERSTITIAL && interstitialInitCallback != null) {
                            interstitialInitCallback.onError(throwable);
                        } else if (adType == ADTYPE_MEDIA && mediaInitCallback != null) {
                            mediaInitCallback.onError(throwable);
                        }
                        VungleException ex = (VungleException) throwable;
                        ZplayDebug.d(TAG, "onError: errorCode: " + ex.getExceptionCode() + ", error: " + ex);
                    } catch (Exception e) {
                        ZplayDebug.d(TAG, "onError: error: " + e);
                    }
                }

                @Override
                public void onAutoCacheAdAvailable(String placementReferenceId) {
                    ZplayDebug.d(TAG, "onAutoCacheAdAvailable: " + placementReferenceId);
                }
            });
        } catch (Exception e) {
            ZplayDebug.d(TAG, "initVungle: error: " + e);
        }
    }

    private static class VungleInstanceHolder {
        private static final VungleInstance instance = new VungleInstance();
    }

}
