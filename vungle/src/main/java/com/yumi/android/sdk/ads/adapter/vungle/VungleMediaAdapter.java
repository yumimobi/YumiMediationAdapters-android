package com.yumi.android.sdk.ads.adapter.vungle;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
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
    private static final int RESTART_INIT = 0x001;
    private static LoadAdCallback mLoadAdCallback;
    private static PlayAdCallback mPlayAdCallback;
    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            Log.d(TAG, "handleMessage: " + msg.what);
            if (msg.what == RESTART_INIT) {
                VungleInstance.getInstance().initVungle(getActivity(), getProvider().getKey1(), VungleInstance.ADTYPE_MEDIA);
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
    protected final void onDestroy() {
        if (mHandler != null && mHandler.hasMessages(RESTART_INIT)) {
            mHandler.removeMessages(RESTART_INIT);
        }
    }

    @Override
    protected void onPrepareMedia() {
        try {
            updateGDPRStatus(getContext());
            final boolean isReady = Vungle.canPlayAd(getProvider().getKey2());
            ZplayDebug.d(TAG, "onPrepareMedia: " + isReady + ", placementId: " + getProvider().getKey2());
            if (isReady) {
                layerPrepared();
            } else if (Vungle.isInitialized()) {
                ZplayDebug.d(TAG, "onPrepareMedia: loadAd");
                Vungle.loadAd(getProvider().getKey2(), VungleInstance.createVungleMediaLoadListener());
            } else {
                ZplayDebug.d(TAG, "onPrepareMedia: notifyFiled");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        layerPreparedFailed(recodeError(null));
                    }
                });
            }
        } catch (Exception e) {
            ZplayDebug.d(TAG, "onPrepareMedia: error: " + e);
        }
    }

    @Override
    protected void onShowMedia() {
        try {
            final boolean isReady = Vungle.canPlayAd(getProvider().getKey2());
            ZplayDebug.d(TAG, "onShowMedia: " + isReady + ", placementId: " + getProvider().getKey2());
            if (isReady) {
                AdConfig adConfig = new AdConfig();
                adConfig.setAutoRotate(true);
                adConfig.setMuted(true);
                Vungle.playAd(getProvider().getKey2(), adConfig, VungleInstance.createVungleMediaPlayListener());
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
        try {
            ZplayDebug.d(TAG, "init: " + getProvider().getKey1());
            createVungleListener();
            initVungleSDK();
        } catch (Exception e) {
            ZplayDebug.d(TAG, "init: error: " + e);
        }
    }

    private void createVungleListener() {

        mLoadAdCallback = new LoadAdCallback() {
            @Override
            public void onAdLoad(String placementReferenceId) {
                ZplayDebug.d(TAG, "onAdLoad: " + placementReferenceId);
                if (getProvider().getKey2().equals(placementReferenceId)) {
                    layerPrepared();
                }
            }

            @Override
            public void onError(String placementReferenceId, final Throwable throwable) {
                try {
                    ZplayDebug.d(TAG, "onError: " + placementReferenceId + ", error: " + throwable);
                    if (getProvider().getKey2().equals(placementReferenceId)) {
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
            public void onError(String placementReferenceId, Throwable throwable) {
                try {
                    VungleException ex = (VungleException) throwable;
                    ZplayDebug.d(TAG, "onError: errorCode: " + ex.getExceptionCode() + ", " + ex);
                    if (ex.getExceptionCode() == VungleException.VUNGLE_NOT_INTIALIZED) {
                        VungleInstance.getInstance().initVungle(getActivity(), getProvider().getKey1(), VungleInstance.ADTYPE_MEDIA);
                    }
                    if (getProvider().getKey2().equals(placementReferenceId)) {
                        AdError adError = new AdError(ERROR_FAILED_TO_SHOW);
                        adError.setErrorMessage("vungle error: " + throwable);
                        layerExposureFailed(adError);
                    }
                } catch (Exception e) {
                    ZplayDebug.d(TAG, "onError: error: " + e);

                }
            }
        };

        VungleInstance.setMediaLoadAdCallback(getProvider().getKey2(), mLoadAdCallback);
        VungleInstance.setMediaPlayAdCallback(getProvider().getKey2(), mPlayAdCallback);
    }

    private void initVungleSDK() {
        VungleInstance.setMediaInitCallback(new InitCallback() {
            @Override
            public void onSuccess() {
                ZplayDebug.d(TAG, "onSuccess: ");
            }

            @Override
            public void onError(Throwable throwable) {
                ZplayDebug.d(TAG, "onError: error: " + throwable);
                mHandler.sendEmptyMessageDelayed(RESTART_INIT, 5 * 1000);
            }

            @Override
            public void onAutoCacheAdAvailable(String s) {
                ZplayDebug.d(TAG, "onAutoCacheAdAvailable: " + s);
                ZplayDebug.d(TAG, "vungle media onAutoCacheAdAvailable : " + s, onoff);
            }
        });
        VungleInstance.getInstance().initVungle(getActivity(), getProvider().getKey1(), VungleInstance.ADTYPE_MEDIA);
    }

    @Override
    public String getProviderVersion() {
        return sdkVersion();
    }
}