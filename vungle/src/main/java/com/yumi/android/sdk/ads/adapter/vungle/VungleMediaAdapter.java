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
            vungle = VungleInstantiate.getInstantiate().getVunglePub();
            if (vungle.isAdPlayable(getProvider().getKey3())) {
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
            if (!vungle.isAdPlayable(getProvider().getKey3())) {
                vungle.playAd(getProvider().getKey3(), null);
                ZplayDebug.d(TAG, "vungle media onShowMedia true", onoff);
            } else {
                ZplayDebug.d(TAG, "vungle media onShowMedia false", onoff);
            }
        } catch (Exception e) {
            ZplayDebug.e(TAG, "vungle onShowMedia error:", e, onoff);
        }
    }

    @Override
    protected boolean isMediaReady() {
        try {
            if (vungle != null && vungle.isAdPlayable(getProvider().getKey3())) {
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
            createVungleListener();
            initVungleSDK();
        } catch (Exception e) {
            ZplayDebug.e(TAG, "vungle init error:", e, onoff);
        }
    }

    private void createVungleListener() {
        eventListener = new VungleAdEventListener() {

            @Override
            public void onAdEnd(@NonNull String placementReferenceId, boolean wasSuccessfulView, boolean wasCallToActionClicked) {
                ZplayDebug.d(TAG, "vungle media onAdEnd placementReferenceId:" + placementReferenceId + "   wasSuccessfulView:" + wasSuccessfulView + "   wasCallToActionClicked" + wasCallToActionClicked, onoff);
                if (getProvider().getKey3().equals(placementReferenceId)) {
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
                if (getProvider().getKey3().equals(placementReferenceId)) {
                    layerExposure();
                    layerMediaStart();
                }
            }

            @Override
            public void onUnableToPlayAd(@NonNull String placementReferenceId, String reason) {
                ZplayDebug.d(TAG, "vungle media onUnableToPlayAd placementReferenceId:" + placementReferenceId + "   reason:" + reason, onoff);
                if (getProvider().getKey3().equals(placementReferenceId)) {
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
                if (getProvider().getKey3().equals(placementReferenceId) && isAdAvailable) {
                    layerPrepared();
                }
            }
        };
        vungle.addEventListeners(eventListener);
    }

    private void initVungleSDK() {
        VungleInstantiate.getInstantiate().initVungle(getActivity(), getProvider().getKey1(), getProvider().getKey2(), getProvider().getKey3());
    }

    @Override
    protected void onRequestNonResponse() {
        super.onRequestNonResponse();
    }

}