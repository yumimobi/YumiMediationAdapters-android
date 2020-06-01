package com.yumi.android.sdk.ads.adapter.vungle;

import android.app.Activity;
import android.util.Log;

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
    protected void onPrepareMedia() {
        try {
            final boolean isInitialized = Vungle.isInitialized();
            ZplayDebug.d(TAG, "load new media isInitialized: " + isInitialized + ", placementId: " + getProvider().getKey1());
            if (!isInitialized) {
                Vungle.init(getProvider().getKey1(), getActivity().getApplicationContext(), new InitCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "onPrepareMedia, init - onSuccess: ");
                        loadAd();
                    }

                    @Override
                    public void onError(VungleException exception) {
                        Log.d(TAG, "onPrepareMedia, init - onError: " + exception.getMessage());
                        layerPreparedFailed(recodeError(exception.getMessage()));
                    }

                    @Override
                    public void onAutoCacheAdAvailable(String s) {
                        Log.d(TAG, "onPrepareMedia, init - onAutoCacheAdAvailable: " + s);
                    }
                });
                return;
            }
            loadAd();
        } catch (Exception e) {
            ZplayDebug.d(TAG, "onPrepareMedia: error: " + e);
        }
    }

    private void loadAd() {
        updateGDPRStatus(getContext());
        ZplayDebug.d(TAG, "loadAd: " + getProvider().getKey2());
        Vungle.loadAd(getProvider().getKey2(), new LoadAdCallback() {
            @Override
            public void onAdLoad(String placementReferenceId) {
                ZplayDebug.d(TAG, "onAdLoad: " + placementReferenceId);
                if (getProvider().getKey2().equals(placementReferenceId)) {
                    layerPrepared();
                }
            }

            @Override
            public void onError(String placementReferenceId, final VungleException exception) {
                try {
                    ZplayDebug.d(TAG, "onError: " + placementReferenceId + ", error: " + exception.getMessage());
                    if (getProvider().getKey2().equals(placementReferenceId)) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                layerPreparedFailed(recodeError(exception.getMessage()));
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
    protected void onShowMedia() {
        try {
            final boolean isReady = Vungle.canPlayAd(getProvider().getKey2());
            ZplayDebug.d(TAG, "onShowMedia: " + isReady + ", placementId: " + getProvider().getKey2());
            if (isReady) {
                AdConfig adConfig = new AdConfig();
//                adConfig.setsetAutoRotate(true);
                adConfig.setMuted(true);
                Vungle.playAd(getProvider().getKey2(), adConfig, new PlayAdCallback() {
                    @Override
                    public void onAdStart(String placementReferenceId) {
                        Log.d(TAG, "onAdStart: " + placementReferenceId);
                        if (getProvider().getKey2().equals(placementReferenceId)) {
                            layerExposure();
                            layerStartPlaying();
                        }
                    }

                    @Override
                    public void onAdEnd(String placementReferenceId, final boolean completed, final boolean isCTAClicked) {
                        Log.d(TAG, "onAdEnd: " + placementReferenceId + ", completed: " + completed + ", isCTAClicked: " + isCTAClicked);
                        if (getProvider().getKey2().equals(placementReferenceId)) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        ZplayDebug.d(TAG, "onAdEnd: " + isCTAClicked + ", " + completed);
                                        if (isCTAClicked) {
                                            layerClicked();
                                        }
                                        if (completed) {
                                            layerIncentived();
                                        }
                                        layerClosed(completed);
                                    } catch (Exception e) {
                                        ZplayDebug.d(TAG, "onAdEnd: error: " + e);
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(String placementReferenceId, VungleException exception) {
                        try {
                            ZplayDebug.d(TAG, "onError: errorCode: " + exception.getExceptionCode() + ", " + exception);
                            if (getProvider().getKey2().equals(placementReferenceId)) {
                                AdError adError = new AdError(ERROR_FAILED_TO_SHOW);
                                adError.setErrorMessage("vungle error: " + exception.getMessage());
                                layerExposureFailed(adError);
                            }
                        } catch (Exception e) {
                            ZplayDebug.d(TAG, "onError: error: " + e);

                        }
                    }

                });
            }
        } catch (Exception e) {
            ZplayDebug.d(TAG, "onShowMedia: error: " + e);
        }
    }

    @Override
    protected boolean isMediaReady() {
        try {
            final boolean isReady = Vungle.canPlayAd(getProvider().getKey2());
            ZplayDebug.d(TAG, "isMediaReady: " + isReady + ", placementId: " + getProvider().getKey2());
            return isReady;
        } catch (Exception e) {
            ZplayDebug.d(TAG, "isMediaReady: error: " + e);
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