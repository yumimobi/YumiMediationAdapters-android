package com.yumi.android.sdk.ads.adapter.vungle;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.vungle.publisher.VungleAdEventListener;
import com.vungle.publisher.VunglePub;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerInterstitialAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

public class VungleInterstitialAdapter extends YumiCustomerInterstitialAdapter {

    private static final String TAG = "VungleInterstitialAdapter";

    private VunglePub vungle;
    private VungleAdEventListener eventListener;

    private boolean isPrepared = false;

    protected VungleInterstitialAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }

    @Override
    public void onActivityPause() {
        try {
            vungle.onPause();
        } catch (Exception e) {
            ZplayDebug.e(TAG, "vungle onActivityPause error:", e, onoff);
        }
    }

    @Override
    public void onActivityResume() {
        try {
            vungle.onResume();
        } catch (Exception e) {
            ZplayDebug.e(TAG, "vungle onActivityResume error:", e, onoff);
        }
    }

    @Override
    public boolean onActivityBackPressed() {
        return false;
    }

    @Override
    protected void onPrepareInterstitial() {
        try {
            ZplayDebug.d(TAG, "vungle request new media", onoff);
            if(vungle==null) {
                vungle = VungleInstantiate.getInstantiate().getVunglePub();
            }
            if (vungle.isAdPlayable(getProvider().getKey2())) {
                ZplayDebug.d(TAG, "vungle Interstitial prapared", onoff);
                layerPrepared();
                isPrepared = true;
            }else{
                isPrepared = false;
            }
        } catch (Exception e) {
            ZplayDebug.e(TAG, "vungle onPrepareMedia error:", e, onoff);
        }
    }

    @Override
    protected void onShowInterstitialLayer(Activity activity) {
        try {
            if (vungle.isAdPlayable(getProvider().getKey2())) {
                vungle.playAd(getProvider().getKey2(), null);
                ZplayDebug.d(TAG, "vungle Interstitial onShowInterstitialLayer true placementId:"+getProvider().getKey2(), onoff);
            } else {
                ZplayDebug.d(TAG, "vungle Interstitial onShowInterstitialLayer false placementId:"+getProvider().getKey2(), onoff);
            }
        } catch (Exception e) {
            ZplayDebug.e(TAG, "vungle onShowInterstitialLayer error:", e, onoff);
        }
    }

    @Override
    protected boolean isInterstitialLayerReady() {
        try {
            if (vungle != null && vungle.isAdPlayable(getProvider().getKey2())) {
                ZplayDebug.d(TAG, "vungle Interstitial isInterstitialLayerReady true", onoff);
                return true;
            }
            ZplayDebug.d(TAG, "vungle Interstitial isInterstitialLayerReady false", onoff);
        } catch (Exception e) {
            ZplayDebug.e(TAG, "vungle isInterstitialLayerReady error:", e, onoff);
        }
        return false;
    }

    @Override
    protected void init() {
        try {
            ZplayDebug.i(TAG, "appId : " + getProvider().getKey1(), onoff);
            if(vungle==null) {
                vungle = VungleInstantiate.getInstantiate().getVunglePub();
            }
            initVungleSDK();
            createVungleListener();
        } catch (Exception e) {
            ZplayDebug.e(TAG, "vungle Interstitial init error:", e, onoff);
        }
    }

    @Override
    protected void callOnActivityDestroy() {
        try {
            vungle.removeEventListeners(eventListener);
        } catch (Exception e) {
            ZplayDebug.e(TAG, "vungle callOnActivityDestroy error:", e, onoff);
        }
    }


    private void createVungleListener() {
        eventListener = new VungleAdEventListener() {
            @Override
            public void onAdEnd(@NonNull String placementReferenceId, boolean wasSuccessfulView, boolean wasCallToActionClicked) {
                ZplayDebug.d(TAG, "vungle Interstitial onAdEnd placementReferenceId:" + placementReferenceId + "   wasSuccessfulView:" + wasSuccessfulView + "   wasCallToActionClicked" + wasCallToActionClicked, onoff);
                if (getProvider().getKey2().equals(placementReferenceId)) {
                    // 当用户离开广告，控制转回至您的应用程序时调用
                    // 如果 wasSuccessfulView 为 true，表示用户观看了广告，应获得奖励
                    //（如果是奖励广告）。
                    // 如果 wasCallToActionClicked 为 true，表示用户点击了广告中的
                    // 行动号召按钮。
                    if (wasCallToActionClicked) {
                        ZplayDebug.d(TAG, "vungle Interstitial clicked", onoff);
                        layerClicked(-99f, -99f);
                    }
                    ZplayDebug.d(TAG, "vungle Interstitial closed", onoff);
                    layerMediaEnd();
                    layerClosed();
                }
            }

            @Override
            public void onAdStart(@NonNull String placementReferenceId) {
                ZplayDebug.d(TAG, "vungle Interstitial onAdStart placementReferenceId:" + placementReferenceId, onoff);
                if (getProvider().getKey2().equals(placementReferenceId)) {
                    layerExposure();
                }
            }

            @Override
            public void onUnableToPlayAd(@NonNull String placementReferenceId, String reason) {
                ZplayDebug.d(TAG, "vungle Interstitial onUnableToPlayAd placementReferenceId:" + placementReferenceId + "   reason:" + reason, onoff);
                if (getProvider().getKey2().equals(placementReferenceId)) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            layerPreparedFailed(LayerErrorCode.ERROR_NO_FILL);
                        }
                    });
                }
            }

            @Override
            public void onAdAvailabilityUpdate(@NonNull String placementReferenceId, boolean isAdAvailable) {
                ZplayDebug.d(TAG, "vungle Interstitial onAdAvailabilityUpdate placementReferenceId:" + placementReferenceId + "   isAdAvailable:" + isAdAvailable, onoff);
                if (getProvider().getKey2().equals(placementReferenceId) && isAdAvailable && !isPrepared) {
                    layerPrepared();
                }
            }
        };
        vungle.addEventListeners(eventListener);
    }


    private void initVungleSDK() {
        VungleInstantiate.getInstantiate().initVungle(getActivity(), getProvider().getKey1(), getProvider().getKey2(), getProvider().getKey3());
        vungle.loadAd(getProvider().getKey2());
    }
}