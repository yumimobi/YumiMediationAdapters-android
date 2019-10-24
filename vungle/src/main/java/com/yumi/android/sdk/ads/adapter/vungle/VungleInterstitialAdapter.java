package com.yumi.android.sdk.ads.adapter.vungle;

import android.app.Activity;
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
            final boolean isInitialized = Vungle.isInitialized();
            ZplayDebug.d(TAG, "onPrepareInterstitial: " + isInitialized + ", placementId: " + getProvider().getKey1());
            if (!isInitialized) {
                Vungle.init(getProvider().getKey1(), getActivity().getApplicationContext(), new InitCallback() {
                    @Override
                    public void onSuccess() {
                        ZplayDebug.d(TAG, "onPrepareMedia, init - onSuccess: ");
                        loadAd();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        ZplayDebug.d(TAG, "onPrepareMedia, init - onError: " + throwable);
                        layerPreparedFailed(recodeError(throwable));
                    }

                    @Override
                    public void onAutoCacheAdAvailable(String s) {
                        ZplayDebug.d(TAG, "onPrepareMedia, init - onAutoCacheAdAvailable: " + s);
                    }
                });
                return;
            }

            loadAd();
        } catch (Exception e) {
            ZplayDebug.i(TAG, "onPrepareInterstitial: error: " + e, onoff);
        }
    }

    private void loadAd() {
        ZplayDebug.d(TAG, "loadAd: " + getProvider().getKey3());
        updateGDPRStatus(getContext());
        Vungle.loadAd(getProvider().getKey3(), new LoadAdCallback() {
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
        });
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
                Vungle.playAd(getProvider().getKey3(), adConfig, new PlayAdCallback() {
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
                            if (TextUtils.equals(placementReferenceId, getProvider().getKey3())) {
                                AdError adError = new AdError(ERROR_FAILED_TO_SHOW);
                                adError.setErrorMessage("Vungle error: " + throwable);
                                layerExposureFailed(adError);
                            }
                        } catch (Exception e) {
                            ZplayDebug.d(TAG, "onError: error: " + e);
                        }
                    }
                });
            }
        } catch (Exception e) {
            ZplayDebug.d(TAG, "onShowInterstitialLayer: error: " + e);
        }
    }

    @Override
    protected boolean isInterstitialLayerReady() {
        try {
            final boolean isReady = Vungle.canPlayAd(getProvider().getKey3());
            ZplayDebug.d(TAG, "isInterstitialLayerReady: " + isReady + ", placementId: " + getProvider().getKey3());
            return isReady;
        } catch (Exception e) {
            ZplayDebug.d(TAG, "isInterstitialLayerReady: error: " + e);
        }
        return false;
    }

    @Override
    protected void init() {
        ZplayDebug.d(TAG, "init: " + getProvider().getKey1());
    }

    @Override
    public String getProviderVersion() {
        return sdkVersion();
    }
}