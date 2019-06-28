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
        boolean isReady = IronSource.isISDemandOnlyRewardedVideoAvailable(getProvider().getKey2());
        if (isReady) {
            ZplayDebug.i(TAG, "IronSource Media onPrepareMedia isReady  instanceId : " + getProvider().getKey2(), onoff);
            layerPrepared();
        } else {
            IronSource.loadISDemandOnlyRewardedVideo(getProvider().getKey2());
        }
    }

    @Override
    protected void onShowMedia() {
        boolean isReady = IronSource.isISDemandOnlyRewardedVideoAvailable(getProvider().getKey2());
        ZplayDebug.i(TAG, "IronSource Media onShowMedia instanceId : " + getProvider().getKey2() + " isReady : " + isReady, onoff);
        if (isReady) {
            IronSource.showISDemandOnlyRewardedVideo(getProvider().getKey2());
        }
    }

    @Override
    protected boolean isMediaReady() {
        boolean isReady = IronSource.isISDemandOnlyRewardedVideoAvailable(getProvider().getKey2());
        ZplayDebug.i(TAG, "IronSource Media isMediaReady instanceId : " + getProvider().getKey2() + " isReady : " + isReady, onoff);
        return isReady;
    }

    @Override
    protected void init() {
        ZplayDebug.i(TAG, "IronSource Media init Key1 : " + getProvider().getKey1() + "  Key2 : " + getProvider().getKey2(), onoff);
        createMediaListener();
        IronsourceListenerHandler.initIronsourceVideoListener(getActivity(), getProvider().getKey1());
//        IronSource.setISDemandOnlyRewardedVideoListener(IronsourceListenerHandler.getIronsourceVideoListener());
    }

    private void createMediaListener() {
        adListener = new ISDemandOnlyRewardedVideoListener() {
            @Override
            public void onRewardedVideoAdLoadSuccess(String instanceId) {
                ZplayDebug.i(TAG, "IronSource Media onRewardedVideoAdLoadSuccess instanceId : " + instanceId, onoff);
                if (instanceId.equals(getProvider().getKey2())) {
                    layerPrepared();
                }
            }

            @Override
            public void onRewardedVideoAdLoadFailed(String instanceId, IronSourceError ironSourceError) {
                ZplayDebug.i(TAG, "IronSource Media onRewardedVideoAdLoadFailed instanceId : " + instanceId, onoff);
                if (instanceId.equals(getProvider().getKey2())) {
                    layerPreparedFailed(generateLayerErrorCode(ironSourceError));
                }
            }

            // Invoked when the RewardedVideo ad view is about to open.
            @Override
            public void onRewardedVideoAdOpened(String instanceId) {
                ZplayDebug.i(TAG, "IronSource Media onRewardedVideoAdOpened instanceId : " + instanceId, onoff);
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
                ZplayDebug.i(TAG, "IronSource Media onRewardedVideoAdClosed instanceId:" + instanceId, onoff);
                if (instanceId.equals(getProvider().getKey2())) {
                    layerClosed(isRewarded);
                }
            }

            // Invoked when RewardedVideo call to show a rewarded video has
            // failed. IronSourceError contains the reason for the failure.
            @Override
            public void onRewardedVideoAdShowFailed(String instanceId, IronSourceError error) {
                ZplayDebug.e(TAG, "IronSource Media onRewardedVideoAdShowFailed  instanceId : " + instanceId + "  getErrorCode : " + error.getErrorCode() + "   || getErrorMessage : " + error.getErrorMessage(), onoff);
                if (TextUtils.equals(instanceId, getProvider().getKey2())) {
                    AdError adError = new AdError(ERROR_FAILED_TO_SHOW);
                    adError.setErrorMessage("IronSource errorMsg: " + error);
                    layerExposureFailed(adError);
                }
            }

            @Override
            public void onRewardedVideoAdClicked(String instanceId) {
                ZplayDebug.i(TAG, "IronSource Media onRewardedVideoAdClicked instanceId : " + instanceId, onoff);
                if (instanceId.equals(getProvider().getKey2())) {
                    layerClicked();
                }
            }

            @Override
            public void onRewardedVideoAdRewarded(String instanceId) {
                ZplayDebug.i(TAG, "IronSource Media onRewardedVideoAdRewarded instanceId : " + instanceId, onoff);
                if (instanceId.equals(getProvider().getKey2())) {
                    isRewarded = true;
                    layerIncentived();
                }
            }

        };
        IronsourceListenerHandler.setMyIronsourceVideoListener(getProvider().getKey2(), adListener);
    }

    @Override
    protected void callOnActivityDestroy() {
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
}
