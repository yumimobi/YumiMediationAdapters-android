package com.yumi.android.sdk.ads.adapter.vungle;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.vungle.publisher.VungleAdEventListener;
import com.vungle.publisher.VunglePub;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerMediaAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

public class VungleMediaAdapter extends YumiCustomerMediaAdapter {

    private static final String TAG = "VungleMediaAdapter";

    private VunglePub vungle;
    private VungleAdEventListener eventListener;

    protected VungleMediaAdapter(Activity activity, YumiProviderBean provider) {
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
    protected final void callOnActivityDestroy() {
        try {
            vungle.removeEventListeners(eventListener);
        } catch (Exception e) {
            ZplayDebug.e(TAG, "vungle callOnActivityDestroy error:", e, onoff);
        }
    }

    @Override
    protected void onPrepareMedia() {
        try {
            ZplayDebug.d(TAG, "vungle request new media", onoff);
            if(vungle==null) {
                vungle = VungleInstantiate.getInstantiate().getVunglePub();
            }
            vungle.loadAd(getProvider().getKey2());
            ZplayDebug.d(TAG, "vungle onPrepareInterstitial loadAd:"+getProvider().getKey2(), onoff);
            if (vungle.isAdPlayable(getProvider().getKey2())) {
                ZplayDebug.d(TAG, "vungle media prapared", onoff);
                layerPrepared();
            }
        } catch (Exception e) {
            ZplayDebug.e(TAG, "vungle onPrepareMedia error:", e, onoff);
        }
    }

    @Override
    protected void onShowMedia() {
        try {
            if (vungle.isAdPlayable(getProvider().getKey2())) {
                vungle.playAd(getProvider().getKey2(), null);
                ZplayDebug.d(TAG, "vungle media onShowMedia true placementId:"+getProvider().getKey2(), onoff);
            } else {
                ZplayDebug.d(TAG, "vungle media onShowMedia false placementId:"+getProvider().getKey2(), onoff);
            }
        } catch (Exception e) {
            ZplayDebug.e(TAG, "vungle onShowMedia error:", e, onoff);
        }
    }

    @Override
    protected boolean isMediaReady() {
        try {
            if (vungle != null && vungle.isAdPlayable(getProvider().getKey2())) {
                ZplayDebug.d(TAG, "vungle media isMediaReady true", onoff);
                return true;
            }
            ZplayDebug.d(TAG, "vungle media isMediaReady false", onoff);
        } catch (Exception e) {
            ZplayDebug.e(TAG, "vungle isMediaReady error:", e, onoff);
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
            ZplayDebug.e(TAG, "vungle media init error:", e, onoff);
        }
    }

    private void createVungleListener() {
        eventListener = new VungleAdEventListener() {
            @Override
            public void onAdEnd(@NonNull String placementReferenceId, boolean wasSuccessfulView, boolean wasCallToActionClicked) {
                ZplayDebug.d(TAG, "vungle media onAdEnd placementReferenceId:" + placementReferenceId + "   wasSuccessfulView:" + wasSuccessfulView + "   wasCallToActionClicked" + wasCallToActionClicked, onoff);
                if (getProvider().getKey2().equals(placementReferenceId)) {
                    // 当用户离开广告，控制转回至您的应用程序时调用
                    // 如果 wasSuccessfulView 为 true，表示用户观看了广告，应获得奖励
                    //（如果是奖励广告）。
                    // 如果 wasCallToActionClicked 为 true，表示用户点击了广告中的
                    // 行动号召按钮。
                    if (wasCallToActionClicked) {
                        ZplayDebug.d(TAG, "vungle media clicked", onoff);
                        layerClicked();
                    }
                    if (wasSuccessfulView) {
                        ZplayDebug.d(TAG, "vungle media get reward", onoff);
                        layerIncentived();
                    }
                    ZplayDebug.d(TAG, "vungle media closed", onoff);
                    layerMediaEnd();
                    layerClosed();
                }
            }

            @Override
            public void onAdStart(@NonNull String placementReferenceId) {
                ZplayDebug.d(TAG, "vungle media onAdStart placementReferenceId:" + placementReferenceId, onoff);
                if (getProvider().getKey2().equals(placementReferenceId)) {
                    layerExposure();
                    layerMediaStart();
                }
            }

            @Override
            public void onUnableToPlayAd(@NonNull String placementReferenceId, String reason) {
                ZplayDebug.d(TAG, "vungle media onUnableToPlayAd placementReferenceId:" + placementReferenceId + "   reason:" + reason, onoff);
                if (getProvider().getKey2().equals(placementReferenceId)) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            layerPreparedFailed(LayerErrorCode.ERROR_INTERNAL);
                        }
                    });
                }
            }

            @Override
            public void onAdAvailabilityUpdate(@NonNull String placementReferenceId, boolean isAdAvailable) {
                ZplayDebug.d(TAG, "vungle media onAdAvailabilityUpdate placementReferenceId:" + placementReferenceId + "   isAdAvailable:" + isAdAvailable, onoff);
                if (getProvider().getKey2().equals(placementReferenceId) && isAdAvailable) {
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

    @Override
    protected void onRequestNonResponse() {
        super.onRequestNonResponse();
    }

}