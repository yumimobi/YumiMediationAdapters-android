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
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerInterstitialAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.yumi.android.sdk.ads.adapter.vungle.VungleUtil.recodeError;

public class VungleInterstitialAdapter extends YumiCustomerInterstitialAdapter {

    private static final String TAG = "VungleInterstitialAdapter";
    private static LoadAdCallback mLoadAdCallback;
    private static PlayAdCallback mPlayAdCallback;

    private static final int RESTART_INIT = 0x001;
    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RESTART_INIT:
                    ZplayDebug.d(TAG, "vungle Interstitial restart init", onoff);
                    VungleInstantiate.getInstantiate().initVungle(getActivity(), getProvider().getKey1(), VungleInstantiate.ADTYPE_INTERSTITIAL);
                    break;
                default:
                    break;
            }
        }
    };

    protected VungleInterstitialAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }

    @Override
    public void onActivityPause() {
    }

    @Override
    public void onActivityResume() {
    }

    @Override
    public boolean onActivityBackPressed() {
        return false;
    }

    @Override
    protected void onPrepareInterstitial() {
        try {
            ZplayDebug.d(TAG, "vungle request new Interstitial", onoff);
            if (Vungle.canPlayAd(getProvider().getKey3())) {
                ZplayDebug.d(TAG, "vungle Interstitial prapared", onoff);
                layerPrepared();
            } else {
                if (Vungle.isInitialized()) {
                    Vungle.loadAd(getProvider().getKey3(), mLoadAdCallback);
                } else {
                    ZplayDebug.d(TAG, "vungle Interstitial init fail", onoff);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            layerPreparedFailed(recodeError(null));
                        }
                    });
                }
                ZplayDebug.d(TAG, "vungle onPrepareInterstitial loadAd:" + getProvider().getKey3(), onoff);
            }
        } catch (Exception e) {
            ZplayDebug.e(TAG, "vungle onPrepareInterstitial error:", e, onoff);
        }
    }

    @Override
    protected void onShowInterstitialLayer(Activity activity) {
        try {
            if (Vungle.canPlayAd(getProvider().getKey3())) {
                AdConfig adConfig = new AdConfig();
                adConfig.setAutoRotate(true);
                adConfig.setMuted(true);
                Vungle.playAd(getProvider().getKey3(), adConfig, mPlayAdCallback);
                ZplayDebug.d(TAG, "vungle Interstitial onShowInterstitialLayer true placementId:" + getProvider().getKey3(), onoff);
            } else {
                ZplayDebug.d(TAG, "vungle Interstitial onShowInterstitialLayer false placementId:" + getProvider().getKey3(), onoff);
            }
        } catch (Exception e) {
            ZplayDebug.e(TAG, "vungle Interstitial onShowInterstitialLayer error:", e, onoff);
        }
    }

    @Override
    protected boolean isInterstitialLayerReady() {
        try {
            if (Vungle.canPlayAd(getProvider().getKey3())) {
                ZplayDebug.d(TAG, "vungle Interstitial isInterstitialLayerReady true", onoff);
                return true;
            }
            ZplayDebug.d(TAG, "vungle Interstitial isInterstitialLayerReady false", onoff);
        } catch (Exception e) {
            ZplayDebug.e(TAG, "vungle Interstitial isInterstitialLayerReady error:", e, onoff);
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
            ZplayDebug.e(TAG, "vungle Interstitial init error:", e, onoff);
        }
    }

    @Override
    protected void callOnActivityDestroy() {
        if (mHandler != null && mHandler.hasMessages(RESTART_INIT)) {
            mHandler.removeMessages(RESTART_INIT);
        }
    }


    private void createVungleListener() {

        mLoadAdCallback = new LoadAdCallback() {
            @Override
            public void onAdLoad(String placementReferenceId) {
                ZplayDebug.d(TAG, "vungle Interstitial LoadAdCallback onAdLoad placementReferenceId:" + placementReferenceId, onoff);
                if (getProvider().getKey3().equals(placementReferenceId)) {
                    layerPrepared();
                }
            }

            @Override
            public void onError(String placementReferenceId, final Throwable throwable) {
                try {
                    ZplayDebug.e(TAG, "vungle Interstitial LoadAdCallback onError  placementReferenceId:" + placementReferenceId + " error:" + throwable.getLocalizedMessage(), onoff);
                    if (getProvider().getKey3().equals(placementReferenceId)) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                layerPreparedFailed(recodeError(throwable));
                            }
                        });
                    }
                } catch (Exception cex) {
                    ZplayDebug.e(TAG, "vungle Interstitial LoadAdCallback onError try error", cex, onoff);
                }
            }
        };
        mPlayAdCallback = new PlayAdCallback() {
            @Override
            public void onAdStart(String placementReferenceId) {
                ZplayDebug.d(TAG, "vungle Interstitial onAdStart placementReferenceId:" + placementReferenceId, onoff);
                if (getProvider().getKey3().equals(placementReferenceId)) {
                    layerExposure();
                }
            }

            @Override
            public void onAdEnd(String placementReferenceId, final boolean completed, final boolean isCTAClicked) {
                ZplayDebug.d(TAG, "vungle Interstitial onAdEnd placementReferenceId:" + placementReferenceId + "   completed:" + completed + "   isCTAClicked" + isCTAClicked, onoff);
                if (getProvider().getKey3().equals(placementReferenceId)) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (isCTAClicked) {
                                    ZplayDebug.d(TAG, "vungle Interstitial clicked", onoff);
                                    layerClicked(-99f, -99f);
                                }
                                ZplayDebug.d(TAG, "vungle Interstitial closed", onoff);
                                layerMediaEnd();
                                layerClosed();
                            } catch (Exception e) {
                                ZplayDebug.e(TAG, "vungle Interstitial onAdEnd error", e, onoff);
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(String placementReferenceId, Throwable throwable) {
                try {
                    VungleException ex = (VungleException) throwable;
                    ZplayDebug.e(TAG, "vungle Interstitial PlayAdCallback onError ExceptionCode : " + ex.getExceptionCode() + "  || LocalizedMessage : " + ex.getLocalizedMessage(), onoff);
                    if (ex.getExceptionCode() == VungleException.VUNGLE_NOT_INTIALIZED) {
                        VungleInstantiate.getInstantiate().initVungle(getActivity(), getProvider().getKey1(), VungleInstantiate.ADTYPE_INTERSTITIAL);
                    }
                } catch (Exception cex) {
                    ZplayDebug.e(TAG, "vungle Interstitial PlayAdCallback onError try error", cex, onoff);
                }
            }
        };

    }


    private void initVungleSDK() {
        VungleInstantiate.setInterstittalInitCallback(new InitCallback() {
            @Override
            public void onSuccess() {
                ZplayDebug.d(TAG, "vungle Interstitial init onSuccess", onoff);
            }

            @Override
            public void onError(Throwable throwable) {
                mHandler.sendEmptyMessageDelayed(RESTART_INIT, 5 * 1000);
            }

            @Override
            public void onAutoCacheAdAvailable(String s) {

            }
        });
        VungleInstantiate.getInstantiate().initVungle(getActivity(), getProvider().getKey1(), VungleInstantiate.ADTYPE_INTERSTITIAL);
    }
}