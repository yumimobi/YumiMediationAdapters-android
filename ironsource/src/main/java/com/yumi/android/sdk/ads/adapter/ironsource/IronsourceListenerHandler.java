package com.yumi.android.sdk.ads.adapter.ironsource;

import android.app.Activity;

import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.ISDemandOnlyInterstitialListener;
import com.ironsource.mediationsdk.sdk.ISDemandOnlyRewardedVideoListener;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hjl on 2018/8/20.
 * Ironsource 回调分发类，用于应对多层配置时由于Ironsource SDK 是单例导致的只有一个回调生效
 */
class IronsourceListenerHandler {

    private static final String TAG = "IronsourceListenerHandler";
    // 设置给Ironsource SDK setISDemandOnlyRewardedVideoListener 的回调
    private static ISDemandOnlyRewardedVideoListener ironsourceVideoListener;
    // 多层配置后Ironsource 视频适配器实现的回调
    private static Map<String, ISDemandOnlyRewardedVideoListener> myIronsourceVideoListener;

    // 设置给Ironsource SDK setISDemandOnlyRewardedVideoListener 的回调
    private static ISDemandOnlyInterstitialListener ironsourceInterstitialListener;
    // 多层配置后Ironsource 插屏适配器实现的回调
    private static Map<String, ISDemandOnlyInterstitialListener> myIronsourceInterstitialListener;

    /**
     * 初始化Ironsource插屏，并设置回调（这样写是因为Ironsource初始化虽然是单例，但是多次调用会影响回调。。。）
     */
    static void initIronsourceInterstitialListener(Activity context, String Key1) {
        if (ironsourceInterstitialListener == null) {
            ironsourceInterstitialListener = new ISDemandOnlyInterstitialListener() {

                @Override
                public void onInterstitialAdReady(String instanceId) {
                    ZplayDebug.d(TAG, "onInterstitialAdReady: " + instanceId);
                    if (myIronsourceInterstitialListener != null) {
                        ISDemandOnlyInterstitialListener ml = myIronsourceInterstitialListener.get(instanceId);
                        if (ml != null) {
                            ml.onInterstitialAdReady(instanceId);
                        }
                    }
                }

                @Override
                public void onInterstitialAdLoadFailed(String instanceId, IronSourceError error) {
                    ZplayDebug.d(TAG, "onInterstitialAdLoadFailed: " + instanceId + ", error: " + error);
                    if (myIronsourceInterstitialListener != null) {
                        ISDemandOnlyInterstitialListener ml = myIronsourceInterstitialListener.get(instanceId);
                        if (ml != null) {
                            ml.onInterstitialAdLoadFailed(instanceId, error);
                        }
                    }
                }

                @Override
                public void onInterstitialAdOpened(String instanceId) {
                    ZplayDebug.d(TAG, "onInterstitialAdOpened: " + instanceId);
                    if (myIronsourceInterstitialListener != null) {
                        ISDemandOnlyInterstitialListener ml = myIronsourceInterstitialListener.get(instanceId);
                        if (ml != null) {
                            ml.onInterstitialAdOpened(instanceId);
                        }
                    }
                }

                @Override
                public void onInterstitialAdClosed(String instanceId) {
                    ZplayDebug.d(TAG, "onInterstitialAdClosed: " + instanceId);
                    if (myIronsourceInterstitialListener != null) {
                        ISDemandOnlyInterstitialListener ml = myIronsourceInterstitialListener.get(instanceId);
                        if (ml != null) {
                            ml.onInterstitialAdClosed(instanceId);
                        }
                    }
                }

                @Override
                public void onInterstitialAdShowFailed(String instanceId, IronSourceError error) {
                    ZplayDebug.d(TAG, "onInterstitialAdShowFailed: " + instanceId + ", error: " + error);
                    if (myIronsourceInterstitialListener != null) {
                        ISDemandOnlyInterstitialListener ml = myIronsourceInterstitialListener.get(instanceId);
                        if (ml != null) {
                            ml.onInterstitialAdShowFailed(instanceId, error);
                        }
                    }
                }

                @Override
                public void onInterstitialAdClicked(String instanceId) {
                    ZplayDebug.d(TAG, "onInterstitialAdClicked: " + instanceId);
                    if (myIronsourceInterstitialListener != null) {
                        ISDemandOnlyInterstitialListener ml = myIronsourceInterstitialListener.get(instanceId);
                        if (ml != null) {
                            ml.onInterstitialAdClicked(instanceId);
                        }
                    }
                }
            };
            IronSource.setISDemandOnlyInterstitialListener(ironsourceInterstitialListener);
            IronSource.initISDemandOnly(context, Key1, IronSource.AD_UNIT.INTERSTITIAL);
        }
    }

    static void setMyIronsourceInterstitialListener(String key2, ISDemandOnlyInterstitialListener ml) {
        if (myIronsourceInterstitialListener == null) {
            myIronsourceInterstitialListener = new HashMap<>();
        }
        myIronsourceInterstitialListener.put(key2, ml);
    }


    /**
     * 初始化Ironsource视频，并设置回调（这样写是因为Ironsource初始化虽然是单例，但是多次调用会影响回调。。。）
     */
    static void initIronsourceVideoListener(Activity context, String Key1) {
        if (ironsourceVideoListener == null) {
            ironsourceVideoListener = new ISDemandOnlyRewardedVideoListener() {
                @Override
                public void onRewardedVideoAdLoadSuccess(String instanceId) {
                    ZplayDebug.d(TAG, "onRewardedVideoAdLoadSuccess: " + instanceId);
                    if (myIronsourceVideoListener != null) {
                        ISDemandOnlyRewardedVideoListener ml = myIronsourceVideoListener.get(instanceId);
                        if (ml != null) {
                            ml.onRewardedVideoAdLoadSuccess(instanceId);
                        }
                    }
                }

                @Override
                public void onRewardedVideoAdLoadFailed(String instanceId, IronSourceError ironSourceError) {
                    ZplayDebug.d(TAG, "onRewardedVideoAdLoadFailed: " + instanceId);
                    if (myIronsourceVideoListener != null) {
                        ISDemandOnlyRewardedVideoListener ml = myIronsourceVideoListener.get(instanceId);
                        if (ml != null) {
                            ml.onRewardedVideoAdLoadFailed(instanceId, ironSourceError);
                        }
                    }
                }

                @Override
                public void onRewardedVideoAdOpened(String instanceId) {
                    ZplayDebug.d(TAG, "onRewardedVideoAdOpened: " + instanceId);
                    if (myIronsourceVideoListener != null) {
                        ISDemandOnlyRewardedVideoListener ml = myIronsourceVideoListener.get(instanceId);
                        if (ml != null) {
                            ml.onRewardedVideoAdOpened(instanceId);
                        }
                    }
                }

                @Override
                public void onRewardedVideoAdClosed(String instanceId) {
                    ZplayDebug.d(TAG, "onRewardedVideoAdClosed: " + instanceId);
                    if (myIronsourceVideoListener != null) {
                        ISDemandOnlyRewardedVideoListener ml = myIronsourceVideoListener.get(instanceId);
                        if (ml != null) {
                            ml.onRewardedVideoAdClosed(instanceId);
                        }
                    }
                }

                @Override
                public void onRewardedVideoAdShowFailed(String instanceId, IronSourceError ironSourceError) {
                    ZplayDebug.d(TAG, "onRewardedVideoAdShowFailed: " + instanceId + ", error: " + ironSourceError);
                    if (myIronsourceVideoListener != null) {
                        ISDemandOnlyRewardedVideoListener ml = myIronsourceVideoListener.get(instanceId);
                        if (ml != null) {
                            ml.onRewardedVideoAdShowFailed(instanceId, ironSourceError);
                        }
                    }
                }

                @Override
                public void onRewardedVideoAdClicked(String instanceId) {
                    ZplayDebug.d(TAG, "onRewardedVideoAdClicked: " + instanceId);
                    if (myIronsourceVideoListener != null) {
                        ISDemandOnlyRewardedVideoListener ml = myIronsourceVideoListener.get(instanceId);
                        if (ml != null) {
                            ml.onRewardedVideoAdClicked(instanceId);
                        }
                    }
                }

                @Override
                public void onRewardedVideoAdRewarded(String instanceId) {
                    ZplayDebug.d(TAG, "onRewardedVideoAdRewarded: " + instanceId);
                    if (myIronsourceVideoListener != null) {
                        ISDemandOnlyRewardedVideoListener ml = myIronsourceVideoListener.get(instanceId);
                        if (ml != null) {
                            ml.onRewardedVideoAdRewarded(instanceId);
                        }
                    }
                }

            };
            IronSource.setISDemandOnlyRewardedVideoListener(ironsourceVideoListener);
            IronSource.initISDemandOnly(context, Key1, IronSource.AD_UNIT.REWARDED_VIDEO);
        }
    }

    static void setMyIronsourceVideoListener(String key2, ISDemandOnlyRewardedVideoListener ml) {
        if (myIronsourceVideoListener == null) {
            myIronsourceVideoListener = new HashMap<>();
        }
        myIronsourceVideoListener.put(key2, ml);
    }
}
