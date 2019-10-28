package com.yumi.android.sdk.ads.adapter.ironsource;

import android.app.Activity;
import android.text.TextUtils;

import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.ISDemandOnlyRewardedVideoListener;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.AdError;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerMediaAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.yumi.android.sdk.ads.adapter.ironsource.IronsourceUtil.generateLayerErrorCode;
import static com.yumi.android.sdk.ads.adapter.ironsource.IronsourceUtil.sdkVersion;
import static com.yumi.android.sdk.ads.adapter.ironsource.IronsourceUtil.updateGDPRStatus;
import static com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode.ERROR_FAILED_TO_SHOW;

/**
 * Created by hjl on 2018/8/10.
 */
public class IronsourceMediaAdapter extends YumiCustomerMediaAdapter {

    private static final String TAG = "IronsourceMediaAdapter";
    private ISDemandOnlyRewardedVideoListener adListener;
    private boolean isRewarded = false;

    protected IronsourceMediaAdapter(Activity activity, YumiProviderBean yumiProviderBean) {
        super(activity, yumiProviderBean);
    }

    @Override
    protected void onPrepareMedia() {
        updateGDPRStatus(getContext());
        final String instanceId = getProvider().getKey2();
        boolean isReady = IronSource.isISDemandOnlyRewardedVideoAvailable(instanceId);
        ZplayDebug.d(TAG, "onPrepareMedia: " + isReady + ", instanceId: " + instanceId);
        if (isReady) {
            layerPrepared();
        } else {
            IronSource.loadISDemandOnlyRewardedVideo(instanceId);
        }
    }

    @Override
    protected void onShowMedia() {
        final String instanceId = getProvider().getKey2();
        boolean isReady = IronSource.isISDemandOnlyRewardedVideoAvailable(instanceId);
        ZplayDebug.d(TAG, "onShowMedia: " + isReady + ", instanceId: " + instanceId);
        if (isReady) {
            IronSource.showISDemandOnlyRewardedVideo(instanceId);
        }
    }

    @Override
    protected boolean isMediaReady() {
        final String instanceId = getProvider().getKey2();
        boolean isReady = IronSource.isISDemandOnlyRewardedVideoAvailable(instanceId);
        ZplayDebug.d(TAG, "isMediaReady: " + isReady + ", instanceId: " + instanceId);
        return isReady;
    }

    @Override
    protected void init() {
        final String appKey = getProvider().getKey1();
        ZplayDebug.d(TAG, "init: " + appKey);
        createMediaListener();
        IronsourceListenerHandler.initIronsourceVideoListener(getActivity(), getProvider().getKey1());
    }

    private void createMediaListener() {
        adListener = new ISDemandOnlyRewardedVideoListener() {
            @Override
            public void onRewardedVideoAdLoadSuccess(String instanceId) {
                ZplayDebug.d(TAG, "onRewardedVideoAdLoadSuccess: " + instanceId);
                if (instanceId.equals(getProvider().getKey2())) {
                    layerPrepared();
                }
            }

            @Override
            public void onRewardedVideoAdLoadFailed(String instanceId, IronSourceError ironSourceError) {
                ZplayDebug.d(TAG, "onRewardedVideoAdLoadFailed: " + instanceId + ", error: " + ironSourceError);
                if (instanceId.equals(getProvider().getKey2())) {
                    layerPreparedFailed(generateLayerErrorCode(ironSourceError));
                }
            }

            // Invoked when the RewardedVideo ad view is about to open.
            @Override
            public void onRewardedVideoAdOpened(String instanceId) {
                ZplayDebug.d(TAG, "onRewardedVideoAdOpened: " + instanceId);
                if (instanceId.equals(getProvider().getKey2())) {
                    isRewarded = false;
                    layerExposure();
                    layerStartPlaying();
                }
            }

            //Invoked when the RewardedVideo ad view is about to be closed.
            //Your activity will now regain its focus.
            @Override
            public void onRewardedVideoAdClosed(String instanceId) {
                ZplayDebug.d(TAG, "onRewardedVideoAdClosed: " + instanceId);
                if (instanceId.equals(getProvider().getKey2())) {
                    layerClosed(isRewarded);
                }
            }

            // Invoked when RewardedVideo call to show a rewarded video has
            // failed. IronSourceError contains the reason for the failure.
            @Override
            public void onRewardedVideoAdShowFailed(String instanceId, IronSourceError error) {
                ZplayDebug.d(TAG, "onRewardedVideoAdShowFailed: " + instanceId + ", error: " + error);
                if (TextUtils.equals(instanceId, getProvider().getKey2())) {
                    AdError adError = new AdError(ERROR_FAILED_TO_SHOW);
                    adError.setErrorMessage("IronSource errorMsg: " + error);
                    layerExposureFailed(adError);
                }
            }

            @Override
            public void onRewardedVideoAdClicked(String instanceId) {
                ZplayDebug.d(TAG, "onRewardedVideoAdClicked: " + instanceId);
                if (instanceId.equals(getProvider().getKey2())) {
                    layerClicked();
                }
            }

            @Override
            public void onRewardedVideoAdRewarded(String instanceId) {
                ZplayDebug.d(TAG, "onRewardedVideoAdRewarded: " + instanceId);
                if (instanceId.equals(getProvider().getKey2())) {
                    isRewarded = true;
                    layerIncentived();
                }
            }

        };
        IronsourceListenerHandler.setMyIronsourceVideoListener(getProvider().getKey2(), adListener);
    }

    @Override
    protected void onDestroy() {
        IronSource.onPause(getActivity());
    }

    @Override
    public void onActivityPause() {
        IronSource.onPause(getActivity());
    }

    @Override
    public void onActivityResume() {
        IronSource.onResume(getActivity());
    }

    @Override
    public String getProviderVersion() {
        return sdkVersion();
    }
}
