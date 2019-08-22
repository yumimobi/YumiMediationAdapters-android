package com.yumi.android.sdk.ads.adapter.vungle;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import com.vungle.warren.AdConfig;
import com.vungle.warren.InitCallback;
import com.vungle.warren.LoadAdCallback;
import com.vungle.warren.PlayAdCallback;
import com.vungle.warren.Vungle;
import com.vungle.warren.error.VungleException;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.AdError;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerMediaAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.yumi.android.sdk.ads.adapter.vungle.VungleUtil.recodeError;
import static com.yumi.android.sdk.ads.adapter.vungle.VungleUtil.sdkVersion;
import static com.yumi.android.sdk.ads.adapter.vungle.VungleUtil.updateGDPRStatus;
import static com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode.ERROR_FAILED_TO_SHOW;

public class VungleMediaAdapter extends YumiCustomerMediaAdapter {

    private static final String TAG = "VungleMediaAdapter";
    private static final int RESTART_INIT = 0x001;
    private static LoadAdCallback mLoadAdCallback;
    private static PlayAdCallback mPlayAdCallback;
    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RESTART_INIT:
                    ZplayDebug.d(TAG, "vungle media restart init", onoff);
                    VungleInstantiate.getInstantiate().initVungle(getActivity(), getProvider().getKey1(), VungleInstantiate.ADTYPE_MEDIA);
                    break;
                default:
                    break;
            }
        }
    };

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
        if (mHandler != null && mHandler.hasMessages(RESTART_INIT)) {
            mHandler.removeMessages(RESTART_INIT);
        }
    }

    @Override
    protected void onPrepareMedia() {
        try {
            updateGDPRStatus(getContext());
            ZplayDebug.d(TAG, "vungle request new media", onoff);
            if (Vungle.canPlayAd(getProvider().getKey2())) {
                ZplayDebug.d(TAG, "vungle media prapared", onoff);
                layerPrepared();
            } else {
                if (Vungle.isInitialized()) {
                    Vungle.loadAd(getProvider().getKey2(), VungleInstantiate.createVungleMediaLoadListener());
                } else {
                    ZplayDebug.d(TAG, "vungle media init fail", onoff);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            layerPreparedFailed(recodeError(null));
                        }
                    });
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
                Vungle.playAd(getProvider().getKey2(), adConfig, VungleInstantiate.createVungleMediaPlayListener());
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
            public void onError(String placementReferenceId, final Throwable throwable) {
                try {
                    ZplayDebug.e(TAG, "vungle media LoadAdCallback onError   placementReferenceId:" + placementReferenceId + "  error:" + throwable.getLocalizedMessage(), onoff);
                    if (getProvider().getKey2().equals(placementReferenceId)) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                layerPreparedFailed(recodeError(throwable));
                            }
                        });
                    }


                } catch (Exception cex) {
                    ZplayDebug.e(TAG, "vungle media LoadAdCallback onError try error", cex, onoff);
                }
            }
        };
        mPlayAdCallback = new PlayAdCallback() {
            @Override
            public void onAdStart(String placementReferenceId) {
                ZplayDebug.d(TAG, "vungle media onAdStart placementReferenceId:" + placementReferenceId, onoff);
                if (getProvider().getKey2().equals(placementReferenceId)) {
                    layerExposure();
                    layerStartPlaying();
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
                                layerClosed(completed);
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
                        VungleInstantiate.getInstantiate().initVungle(getActivity(), getProvider().getKey1(), VungleInstantiate.ADTYPE_MEDIA);
                    }

                    if (getProvider().getKey2().equals(placementReferenceId)) {
                        AdError adError = new AdError(ERROR_FAILED_TO_SHOW);
                        adError.setErrorMessage("vungle error: " + throwable);
                        layerExposureFailed(adError);
                    }
                } catch (Exception cex) {
                    ZplayDebug.e(TAG, "vungle media PlayAdCallback onError try error", cex, onoff);

                }
            }
        };

        VungleInstantiate.getInstantiate().setMediaLoadAdCallback(getProvider().getKey2(), mLoadAdCallback);
        VungleInstantiate.getInstantiate().setMediaPlayAdCallback(getProvider().getKey2(), mPlayAdCallback);
    }

    private void initVungleSDK() {
        VungleInstantiate.setMeidaInitCallback(new InitCallback() {
            @Override
            public void onSuccess() {
                ZplayDebug.d(TAG, "vungle media init onSuccess", onoff);
            }

            @Override
            public void onError(Throwable throwable) {
                mHandler.sendEmptyMessageDelayed(RESTART_INIT, 5 * 1000);
            }

            @Override
            public void onAutoCacheAdAvailable(String s) {
                ZplayDebug.d(TAG, "vungle media onAutoCacheAdAvailable : " + s, onoff);
            }
        });
        VungleInstantiate.getInstantiate().initVungle(getActivity(), getProvider().getKey1(), VungleInstantiate.ADTYPE_MEDIA);
    }

    @Override
    protected void onRequestNonResponse() {
        super.onRequestNonResponse();
    }

    @Override
    public String getProviderVersion() {
        return sdkVersion();
    }
}