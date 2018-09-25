package com.yumi.android.sdk.ads.adapter.vungle;

import android.app.Activity;

import com.vungle.warren.AdConfig;
import com.vungle.warren.InitCallback;
import com.vungle.warren.LoadAdCallback;
import com.vungle.warren.PlayAdCallback;
import com.vungle.warren.Vungle;
import com.vungle.warren.error.VungleException;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerMediaAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

public class VungleMediaAdapter extends YumiCustomerMediaAdapter {

    private static final String TAG = "VungleMediaAdapter";
    private static LoadAdCallback mLoadAdCallback;
    private static PlayAdCallback mPlayAdCallback;

    protected VungleMediaAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }

    @Override
    public void onActivityPause() {
    }

    @Override
    public void onActivityResume() {
    }

    @Override
    protected final void callOnActivityDestroy() {
    }

    @Override
    protected void onPrepareMedia() {
        try {
            ZplayDebug.d(TAG, "vungle request new media", onoff);
            if (Vungle.canPlayAd(getProvider().getKey2())) {
                ZplayDebug.d(TAG, "vungle media prapared", onoff);
                layerPrepared();
            } else {
                if (Vungle.isInitialized()) {
                    Vungle.loadAd(getProvider().getKey2(), mLoadAdCallback);
                }
                ZplayDebug.d(TAG, "vungle onPrepareMedia loadAd:" + getProvider().getKey2(), onoff);
            }
        } catch (Exception e) {
            ZplayDebug.e(TAG, "vungle onPrepareMedia error:", e, onoff);
        }
    }

    @Override
    protected void onShowMedia() {
        try {
            if (Vungle.canPlayAd(getProvider().getKey2())) {
                AdConfig adConfig = new AdConfig();
                adConfig.setAutoRotate(true);
                adConfig.setMuted(true);
                Vungle.playAd(getProvider().getKey2(), adConfig, mPlayAdCallback);
                ZplayDebug.d(TAG, "vungle media onShowMedia true placementId:" + getProvider().getKey2(), onoff);
            } else {
                ZplayDebug.d(TAG, "vungle media onShowMedia false placementId:" + getProvider().getKey2(), onoff);
            }
        } catch (Exception e) {
            ZplayDebug.e(TAG, "vungle onShowMedia error:", e, onoff);
        }
    }

    @Override
    protected boolean isMediaReady() {
        try {
            if (Vungle.canPlayAd(getProvider().getKey2())) {
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
            ZplayDebug.e(TAG, "vungle media init error:", e, onoff);
        }
    }

    private void createVungleListener() {

        mLoadAdCallback = new LoadAdCallback() {
            @Override
            public void onAdLoad(String placementReferenceId) {
                ZplayDebug.d(TAG, "vungle media LoadAdCallback onAdLoad placementReferenceId:" + placementReferenceId, onoff);
                if (getProvider().getKey2().equals(placementReferenceId)) {
                    layerPrepared();
                }
            }

            @Override
            public void onError(String placementReferenceId, Throwable throwable) {
                try {
                    VungleException ex = (VungleException) throwable;
                    ZplayDebug.e(TAG, "vungle media LoadAdCallback onError placementReferenceId:" + placementReferenceId + " ExceptionCode : " + ex.getExceptionCode() + "  || LocalizedMessage : " + ex.getLocalizedMessage(), onoff);
                    final int exceptionCode = ex.getExceptionCode();
                    if (exceptionCode == VungleException.VUNGLE_NOT_INTIALIZED) {
                        VungleInstantiate.getInstantiate().initVungle(getActivity(), getProvider().getKey1());
                    }
                    if (getProvider().getKey2().equals(placementReferenceId)) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (exceptionCode == 1) {
                                    layerPreparedFailed(LayerErrorCode.ERROR_NO_FILL);
                                } else {
                                    layerPreparedFailed(LayerErrorCode.ERROR_INTERNAL);
                                }
                            }
                        });
                    }
                } catch (Exception cex) {
                    ZplayDebug.e(TAG, "vungle media PlayAdCallback onError try error", cex, onoff);
                }

            }
        };
        mPlayAdCallback = new PlayAdCallback() {
            @Override
            public void onAdStart(String placementReferenceId) {
                ZplayDebug.d(TAG, "vungle media onAdStart placementReferenceId:" + placementReferenceId, onoff);
                if (getProvider().getKey2().equals(placementReferenceId)) {
                    layerExposure();
                    layerMediaStart();
                }
            }

            @Override
            public void onAdEnd(String placementReferenceId, final boolean completed, final boolean isCTAClicked) {
                ZplayDebug.d(TAG, "vungle media onAdEnd placementReferenceId:" + placementReferenceId + "   completed:" + completed + "   isCTAClicked" + isCTAClicked, onoff);
                if (getProvider().getKey2().equals(placementReferenceId)) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (isCTAClicked) {
                                    ZplayDebug.d(TAG, "vungle media clicked", onoff);
                                    layerClicked();
                                }
                                if (completed) {
                                    ZplayDebug.d(TAG, "vungle media get reward", onoff);
                                    layerIncentived();
                                }
                                ZplayDebug.d(TAG, "vungle media closed", onoff);
                                layerMediaEnd();
                                layerClosed();
                            } catch (Exception e) {
                                ZplayDebug.e(TAG, "vungle media onAdEnd error", e, onoff);
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(String placementReferenceId, Throwable throwable) {
                try {
                    VungleException ex = (VungleException) throwable;
                    ZplayDebug.e(TAG, "vungle media PlayAdCallback onError ExceptionCode : " + ex.getExceptionCode() + "  || LocalizedMessage : " + ex.getLocalizedMessage(), onoff);
                    if (ex.getExceptionCode() == VungleException.VUNGLE_NOT_INTIALIZED) {
                        VungleInstantiate.getInstantiate().initVungle(getActivity(), getProvider().getKey1());
                    }
                } catch (Exception cex) {
                    ZplayDebug.e(TAG, "vungle media PlayAdCallback onError try error", cex, onoff);
                }
            }
        };
    }

    private void initVungleSDK() {
        VungleInstantiate.setMeidaInitCallback(new InitCallback() {
            @Override
            public void onSuccess() {
                ZplayDebug.d(TAG, "vungle media loadAd", onoff);
                Vungle.loadAd(getProvider().getKey2(), mLoadAdCallback);
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onAutoCacheAdAvailable(String s) {

            }
        });
        VungleInstantiate.getInstantiate().initVungle(getActivity(), getProvider().getKey1());
    }

    @Override
    protected void onRequestNonResponse() {
        super.onRequestNonResponse();
    }

}