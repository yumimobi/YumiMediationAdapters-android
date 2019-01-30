package com.yumi.android.sdk.ads.adapter.ironsource;

import android.app.Activity;

import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.model.Placement;
import com.ironsource.mediationsdk.sdk.ISDemandOnlyInterstitialListener;
import com.ironsource.mediationsdk.sdk.ISDemandOnlyRewardedVideoListener;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hjl on 2018/8/20.
 * Ironsource 回调分发类，用于应对多层配置时由于Ironsource SDK 是单例导致的只有一个回调生效
 */
public class IronsourceListenerHandler {

    public static final boolean onoff = true;
    private static final String TAG = "IronsourceMediaAdapter";
    //设置给Ironsource SDK setISDemandOnlyRewardedVideoListener 的回调
    private static ISDemandOnlyRewardedVideoListener ironsourceVideoListener;
    //多层配置后Ironsource 视频适配器实现的回调
    private static Map<String, ISDemandOnlyRewardedVideoListener> myIronsourceVideoListener;

    //设置给Ironsource SDK setISDemandOnlyRewardedVideoListener 的回调
    private static ISDemandOnlyInterstitialListener ironsourceInterstitialListener;
    //多层配置后Ironsource 插屏适配器实现的回调
    private static Map<String, ISDemandOnlyInterstitialListener> myIronsourceInterstitialListener;

    /**
     * 初始化Ironsource插屏，并设置回调（这样写是因为Ironsource初始化虽然是单例，但是多次调用会影响回调。。。）
     *
     * @return
     */
    public static ISDemandOnlyInterstitialListener initIronsourceInterstitialListener(Activity context, String Key1) {
        if (ironsourceInterstitialListener == null) {
            ironsourceInterstitialListener = new ISDemandOnlyInterstitialListener() {

                /**
                 Invoked when Interstitial Ad is ready to be shown after load function was called.
                 */
                @Override
                public void onInterstitialAdReady(String instanceId) {
                    ZplayDebug.i(TAG, "IronSource Interstitial onInterstitialAdReady instanceId : " + instanceId, onoff);
                    if (myIronsourceInterstitialListener != null) {
                        ISDemandOnlyInterstitialListener ml = myIronsourceInterstitialListener.get(instanceId);
                        if (ml != null) {
                            ml.onInterstitialAdReady(instanceId);
                        }
                    }
                }

                /**
                 invoked when there is no Interstitial Ad available after calling load function.
                 */
                @Override
                public void onInterstitialAdLoadFailed(String instanceId, IronSourceError error) {
                    ZplayDebug.e(TAG, "IronSource Interstitial onInterstitialAdLoadFailed : " + instanceId + "   getErrorCode : " + error.getErrorCode() + "   || getErrorMessage : " + error.getErrorMessage(), onoff);
                    if (myIronsourceInterstitialListener != null) {
                        ISDemandOnlyInterstitialListener ml = myIronsourceInterstitialListener.get(instanceId);
                        if (ml != null) {
                            ml.onInterstitialAdLoadFailed(instanceId, error);
                        }
                    }
                }

                /**
                 Invoked when the Interstitial Ad Unit is opened
                 */
                @Override
                public void onInterstitialAdOpened(String instanceId) {
                    ZplayDebug.i(TAG, "IronSource Interstitial onInterstitialAdOpened instanceId : " + instanceId, onoff);
                    if (myIronsourceInterstitialListener != null) {
                        ISDemandOnlyInterstitialListener ml = myIronsourceInterstitialListener.get(instanceId);
                        if (ml != null) {
                            ml.onInterstitialAdOpened(instanceId);
                        }
                    }
                }

                /*
                 * Invoked when the ad is closed and the user is about to return to the application.
                 */
                @Override
                public void onInterstitialAdClosed(String instanceId) {
                    ZplayDebug.i(TAG, "IronSource Interstitial onInterstitialAdClosed instanceId : " + instanceId, onoff);
                    if (myIronsourceInterstitialListener != null) {
                        ISDemandOnlyInterstitialListener ml = myIronsourceInterstitialListener.get(instanceId);
                        if (ml != null) {
                            ml.onInterstitialAdClosed(instanceId);
                        }
                    }
                }

                /*
                 * Invoked when the ad was opened and shown successfully.
                 */
                @Override
                public void onInterstitialAdShowSucceeded(String instanceId) {
                    ZplayDebug.i(TAG, "IronSource Interstitial onInterstitialAdShowSucceeded instanceId : " + instanceId, onoff);
                    if (myIronsourceInterstitialListener != null) {
                        ISDemandOnlyInterstitialListener ml = myIronsourceInterstitialListener.get(instanceId);
                        if (ml != null) {
                            ml.onInterstitialAdShowSucceeded(instanceId);
                        }
                    }
                }

                /**
                 * Invoked when Interstitial ad failed to show.
                 // @param error - An object which represents the reason of showInterstitial failure.
                 */
                @Override
                public void onInterstitialAdShowFailed(String instanceId, IronSourceError error) {
                    ZplayDebug.e(TAG, "IronSource Interstitial onInterstitialAdShowFailed instanceId : " + instanceId + "  getErrorCode : " + error.getErrorCode() + "   || getErrorMessage : " + error.getErrorMessage(), onoff);
                    if (myIronsourceInterstitialListener != null) {
                        ISDemandOnlyInterstitialListener ml = myIronsourceInterstitialListener.get(instanceId);
                        if (ml != null) {
                            ml.onInterstitialAdShowFailed(instanceId, error);
                        }
                    }
                }

                /*
                 * Invoked when the end user clicked on the interstitial ad.
                 */
                @Override
                public void onInterstitialAdClicked(String instanceId) {
                    ZplayDebug.i(TAG, "IronSource Interstitial onInterstitialAdClicked instanceId : " + instanceId, onoff);
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
        return ironsourceInterstitialListener;
    }


    /**
     * 设置适配器实现的回调
     *
     * @param ml
     */
    public static void setMyIronsourceInterstitialListener(String key2, ISDemandOnlyInterstitialListener ml) {
        if (myIronsourceInterstitialListener == null) {
            myIronsourceInterstitialListener = new HashMap<String, ISDemandOnlyInterstitialListener>();
        }
        myIronsourceInterstitialListener.put(key2, ml);
    }


    /**
     * 初始化Ironsource视频，并设置回调（这样写是因为Ironsource初始化虽然是单例，但是多次调用会影响回调。。。）
     *
     * @return
     */
    public static ISDemandOnlyRewardedVideoListener initIronsourceVideoListener(Activity context, String Key1) {
        if (ironsourceVideoListener == null) {
            ironsourceVideoListener = new ISDemandOnlyRewardedVideoListener() {
                @Override
                public void onRewardedVideoAdOpened(String s) {
                    ZplayDebug.i(TAG, "IronSource Media onRewardedVideoAdOpened instanceId : " + s, onoff);
                    if (myIronsourceVideoListener != null) {
                        ISDemandOnlyRewardedVideoListener ml = myIronsourceVideoListener.get(s);
                        if (ml != null) {
                            ml.onRewardedVideoAdOpened(s);
                        }
                    }
                }

                @Override
                public void onRewardedVideoAdClosed(String s) {
                    ZplayDebug.i(TAG, "IronSource Media onRewardedVideoAdClosed instanceId:" + s, onoff);
                    if (myIronsourceVideoListener != null) {
                        ISDemandOnlyRewardedVideoListener ml = myIronsourceVideoListener.get(s);
                        if (ml != null) {
                            ml.onRewardedVideoAdClosed(s);
                        }
                    }
                }

                @Override
                public void onRewardedVideoAvailabilityChanged(String s, boolean b) {
                    ZplayDebug.i(TAG, "IronSource Media onRewardedVideoAvailabilityChanged instanceId : " + s + "  available : " + b, onoff);
                    if (myIronsourceVideoListener != null) {
                        ISDemandOnlyRewardedVideoListener ml = myIronsourceVideoListener.get(s);
                        if (ml != null) {
                            ml.onRewardedVideoAvailabilityChanged(s, b);
                        }
                    }
                }

                @Override
                public void onRewardedVideoAdRewarded(String s, Placement placement) {
                    ZplayDebug.i(TAG, "IronSource Media onRewardedVideoAdRewarded instanceId : " + s + "  placement:" + placement.getPlacementName(), onoff);
                    if (myIronsourceVideoListener != null) {
                        ISDemandOnlyRewardedVideoListener ml = myIronsourceVideoListener.get(s);
                        if (ml != null) {
                            ml.onRewardedVideoAdRewarded(s, placement);
                        }
                    }
                }

                @Override
                public void onRewardedVideoAdShowFailed(String s, IronSourceError ironSourceError) {
                    ZplayDebug.e(TAG, "IronSource Media onRewardedVideoAdShowFailed  instanceId : " + s + "  getErrorCode : " + ironSourceError.getErrorCode() + "   || getErrorMessage : " + ironSourceError.getErrorMessage(), onoff);
                    if (myIronsourceVideoListener != null) {
                        ISDemandOnlyRewardedVideoListener ml = myIronsourceVideoListener.get(s);
                        if (ml != null) {
                            ml.onRewardedVideoAdShowFailed(s, ironSourceError);
                        }
                    }
                }

                @Override
                public void onRewardedVideoAdClicked(String s, Placement placement) {
                    ZplayDebug.i(TAG, "IronSource Media onRewardedVideoAdClicked instanceId : " + s + "  placement:" + placement.getPlacementName(), onoff);
                    if (myIronsourceVideoListener != null) {
                        ISDemandOnlyRewardedVideoListener ml = myIronsourceVideoListener.get(s);
                        if (ml != null) {
                            ml.onRewardedVideoAdClicked(s, placement);
                        }
                    }
                }
            };
            IronSource.setISDemandOnlyRewardedVideoListener(ironsourceVideoListener);
            IronSource.initISDemandOnly(context, Key1, IronSource.AD_UNIT.REWARDED_VIDEO);
        }
        return ironsourceVideoListener;
    }


    /**
     * 设置适配器实现的回调
     *
     * @param ml
     */
    public static void setMyIronsourceVideoListener(String key2, ISDemandOnlyRewardedVideoListener ml) {
        if (myIronsourceVideoListener == null) {
            myIronsourceVideoListener = new HashMap<String, ISDemandOnlyRewardedVideoListener>();
        }
        myIronsourceVideoListener.put(key2, ml);
    }
}
