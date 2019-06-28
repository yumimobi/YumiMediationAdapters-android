package com.yumi.android.sdk.ads.adapter.vungle;

import android.app.Activity;

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
public class VungleInstantiate {

    public static final int ADTYPE_INTERSTITIAL = 1;
    public static final int ADTYPE_MEDIA = 2;
    private static final String TAG = "VungleExtra";
    private static final boolean onoff = true;
    private static InitCallback interstittalInitCallback;
    private static InitCallback mediaInitCallback;

    private static LoadAdCallback mediaLoadAdCallback;
    private static PlayAdCallback mediaPlayAdCallback;

    private static Map<String, LoadAdCallback> myVungleMediaLoadAdListener;
    private static Map<String, PlayAdCallback> myVungleMediaPlayAdListener;

    static final VungleInstantiate getInstantiate() {
        return VungleInstantiateHolder.instantiate;
    }

    public static void setInterstittalInitCallback(InitCallback initCallback) {
        interstittalInitCallback = initCallback;
    }

    public static void setMeidaInitCallback(InitCallback initCallback) {
        mediaInitCallback = initCallback;
    }

    public static LoadAdCallback createVungleMediaLoadListener() {
        if (mediaLoadAdCallback == null) {
            mediaLoadAdCallback = new LoadAdCallback() {
                @Override
                public void onAdLoad(String placementReferenceId) {
                    ZplayDebug.d(TAG, "vungle media LoadAdCallback onAdLoad placementReferenceId:" + placementReferenceId, onoff);
                    if (myVungleMediaLoadAdListener != null) {
                        LoadAdCallback ml = myVungleMediaLoadAdListener.get(placementReferenceId);
                        if (ml != null) {
                            ml.onAdLoad(placementReferenceId);
                        }
                    }
                }

                @Override
                public void onError(String placementReferenceId, Throwable throwable) {
                    ZplayDebug.e(TAG, "vungle media LoadAdCallback onError   placementReferenceId:" + placementReferenceId + "  error:" + throwable.getLocalizedMessage(), onoff);
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

    public static PlayAdCallback createVungleMediaPlayListener() {
        if (mediaPlayAdCallback == null) {
            mediaPlayAdCallback = new PlayAdCallback() {
                @Override
                public void onAdStart(String placementReferenceId) {
                    ZplayDebug.d(TAG, "vungle media onAdStart placementReferenceId:" + placementReferenceId, onoff);
                    if (myVungleMediaPlayAdListener != null) {
                        PlayAdCallback mp = myVungleMediaPlayAdListener.get(placementReferenceId);
                        if (mp != null) {
                            mp.onAdStart(placementReferenceId);
                        }
                    }
                }

                @Override
                public void onAdEnd(String placementReferenceId, final boolean completed, final boolean isCTAClicked) {
                    ZplayDebug.d(TAG, "vungle media onAdEnd placementReferenceId:" + placementReferenceId + "   completed:" + completed + "   isCTAClicked" + isCTAClicked, onoff);
                    if (myVungleMediaPlayAdListener != null) {
                        PlayAdCallback mp = myVungleMediaPlayAdListener.get(placementReferenceId);
                        if (mp != null) {
                            mp.onAdEnd(placementReferenceId, completed, isCTAClicked);
                        }
                    }

                }

                @Override
                public void onError(String placementReferenceId, Throwable throwable) {
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

    public static void setMediaLoadAdCallback(String key2, LoadAdCallback ml) {
        if (myVungleMediaLoadAdListener == null) {
            myVungleMediaLoadAdListener = new HashMap<String, LoadAdCallback>();
        }
        myVungleMediaLoadAdListener.put(key2, ml);
    }

    public static void setMediaPlayAdCallback(String key2, PlayAdCallback mp) {
        if (myVungleMediaPlayAdListener == null) {
            myVungleMediaPlayAdListener = new HashMap<String, PlayAdCallback>();
        }
        myVungleMediaPlayAdListener.put(key2, mp);
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

    private static class VungleInstantiateHolder {
        private static final VungleInstantiate instantiate = new VungleInstantiate();
    }

}
