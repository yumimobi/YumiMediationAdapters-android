package com.yumi.android.sdk.ads.adapter.vungle;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.vungle.warren.AdConfig;
import com.vungle.warren.InitCallback;
import com.vungle.warren.LoadAdCallback;
import com.vungle.warren.PlayAdCallback;
import com.vungle.warren.Vungle;
import com.vungle.warren.error.VungleException;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.AdError;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerInterstitialAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.yumi.android.sdk.ads.adapter.vungle.VungleUtil.recodeError;
import static com.yumi.android.sdk.ads.adapter.vungle.VungleUtil.sdkVersion;
import static com.yumi.android.sdk.ads.adapter.vungle.VungleUtil.updateGDPRStatus;
import static com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode.ERROR_FAILED_TO_SHOW;

public class VungleInterstitialAdapter extends YumiCustomerInterstitialAdapter {

    private static final String TAG = "VungleInterstitialAdapter";
    private static final int RESTART_INIT = 0x001;
    private static LoadAdCallback mLoadAdCallback;
    private static PlayAdCallback mPlayAdCallback;
    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            ZplayDebug.d(TAG, "handleMessage: " + msg.what);
            if (msg.what == RESTART_INIT) {
                VungleInstance.getInstance().initVungle(getActivity(), getProvider().getKey1(), VungleInstance.ADTYPE_INTERSTITIAL);
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
            updateGDPRStatus(getContext());
            boolean isReady = Vungle.canPlayAd(getProvider().getKey3());
            ZplayDebug.d(TAG, "onPrepareInterstitial: " + isReady + ", placementId: " + getProvider().getKey3());

            if (isReady) {
                layerPrepared();
            } else if (Vungle.isInitialized()) {
                ZplayDebug.d(TAG, "onPrepareInterstitial: loadAd");
                Vungle.loadAd(getProvider().getKey3(), mLoadAdCallback);
            } else {
                ZplayDebug.d(TAG, "onPrepareInterstitial: notifyFailed");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        layerPreparedFailed(recodeError(null));
                    }
                });
            }
        } catch (Exception e) {
            ZplayDebug.i(TAG, "onPrepareInterstitial: error: " + e, onoff);
        }
    }

    @Override
    protected void onShowInterstitialLayer(Activity activity) {
        try {
            final boolean isReady = Vungle.canPlayAd(getProvider().getKey3());
            ZplayDebug.d(TAG, "onShowInterstitialLayer: " + isReady + ", placementId: " + getProvider().getKey3());
            if (isReady) {
                AdConfig adConfig = new AdConfig();
                adConfig.setAutoRotate(true);
                adConfig.setMuted(true);
                Vungle.playAd(getProvider().getKey3(), adConfig, mPlayAdCallback);
            }
        } catch (Exception e) {
            ZplayDebug.d(TAG, "onShowInterstitialLayer: error: " + e);
        }
    }

    @Override
    protected boolean isInterstitialLayerReady() {
        try {
            final boolean isReady = Vungle.canPlayAd(getProvider().getKey3());
            ZplayDebug.d(TAG, "isInterstitialLayerReady: " + isReady);
            return isReady;
        } catch (Exception e) {
            ZplayDebug.d(TAG, "isInterstitialLayerReady: error: " + e);
        }
        return false;
    }

    @Override
    protected void init() {
        try {
            ZplayDebug.d(TAG, "init: " + getProvider().getKey1());
            createVungleListener();
            initVungleSDK();
        } catch (Exception e) {
            ZplayDebug.d(TAG, "init: error: " + e);
        }
    }

    @Override
    protected void onDestroy() {
        if (mHandler != null && mHandler.hasMessages(RESTART_INIT)) {
            mHandler.removeMessages(RESTART_INIT);
        }
    }


    private void createVungleListener() {
        mLoadAdCallback = new LoadAdCallback() {
            @Override
            public void onAdLoad(String placementReferenceId) {
                ZplayDebug.d(TAG, "onAdLoad: " + placementReferenceId);
                if (getProvider().getKey3().equals(placementReferenceId)) {
                    layerPrepared();
                }
            }

            @Override
            public void onError(String placementReferenceId, final Throwable throwable) {
                try {
                    ZplayDebug.d(TAG, "onError: " + placementReferenceId + ", error: " + throwable);
                    if (getProvider().getKey3().equals(placementReferenceId)) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                layerPreparedFailed(recodeError(throwable));
                            }
                        });
                    }
                } catch (Exception e) {
                    ZplayDebug.d(TAG, "onError: error: " + e);
                }
            }
        };

        mPlayAdCallback = new PlayAdCallback() {
            @Override
            public void onAdStart(String placementReferenceId) {
                ZplayDebug.d(TAG, "onAdStart: " + placementReferenceId);
                if (getProvider().getKey3().equals(placementReferenceId)) {
                    layerExposure();
                    layerStartPlaying();
                }
            }

            @Override
            public void onAdEnd(String placementReferenceId, final boolean completed, final boolean isCTAClicked) {
                ZplayDebug.d(TAG, "onAdEnd: " + placementReferenceId + ", completed: " + completed);
                if (getProvider().getKey3().equals(placementReferenceId)) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                ZplayDebug.d(TAG, "onAdEnd: isCTAClicked: " + isCTAClicked);
                                if (isCTAClicked) {
                                    layerClicked(-99f, -99f);
                                }
                                layerClosed();
                            } catch (Exception e) {
                                ZplayDebug.d(TAG, "onAdEnd: error: " + e);
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(String placementReferenceId, Throwable throwable) {
                try {
                    VungleException ex = (VungleException) throwable;
                    ZplayDebug.d(TAG, "onError: " + placementReferenceId + ", errorCode: " + ex.getExceptionCode() + ", " + ex.getLocalizedMessage());
                    if (ex.getExceptionCode() == VungleException.VUNGLE_NOT_INTIALIZED) {
                        VungleInstance.getInstance().initVungle(getActivity(), getProvider().getKey1(), VungleInstance.ADTYPE_INTERSTITIAL);
                    }
                    if (TextUtils.equals(placementReferenceId, getProvider().getKey3())) {
                        AdError adError = new AdError(ERROR_FAILED_TO_SHOW);
                        adError.setErrorMessage("Vungle error: " + throwable);
                        layerExposureFailed(adError);
                    }
                } catch (Exception e) {
                    ZplayDebug.d(TAG, "onError: error: " + e);
                }
            }
        };
    }


    private void initVungleSDK() {
        VungleInstance.setInterstitialInitCallback(new InitCallback() {
            @Override
            public void onSuccess() {
                ZplayDebug.d(TAG, "onSuccess: ");
            }

            @Override
            public void onError(Throwable throwable) {
                ZplayDebug.d(TAG, "onError: " + throwable);
                mHandler.sendEmptyMessageDelayed(RESTART_INIT, 5 * 1000);
            }

            @Override
            public void onAutoCacheAdAvailable(String s) {
                ZplayDebug.d(TAG, "onAutoCacheAdAvailable: " + s);
            }
        });
        VungleInstance.getInstance().initVungle(getActivity(), getProvider().getKey1(), VungleInstance.ADTYPE_INTERSTITIAL);
    }

    @Override
    public String getProviderVersion() {
        return sdkVersion();
    }
}