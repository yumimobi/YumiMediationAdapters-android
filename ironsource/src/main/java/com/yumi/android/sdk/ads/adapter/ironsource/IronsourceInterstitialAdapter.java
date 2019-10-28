package com.yumi.android.sdk.ads.adapter.ironsource;

import android.app.Activity;
import android.text.TextUtils;

import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.ISDemandOnlyInterstitialListener;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.AdError;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerInterstitialAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.yumi.android.sdk.ads.adapter.ironsource.IronsourceUtil.generateLayerErrorCode;
import static com.yumi.android.sdk.ads.adapter.ironsource.IronsourceUtil.sdkVersion;
import static com.yumi.android.sdk.ads.adapter.ironsource.IronsourceUtil.updateGDPRStatus;
import static com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode.ERROR_FAILED_TO_SHOW;

/**
 * Created by hjl on 2018/8/10.
 */
public class IronsourceInterstitialAdapter extends YumiCustomerInterstitialAdapter {

    private static final String TAG = "IronsourceInterstitialAdapter";
    private ISDemandOnlyInterstitialListener adListener;

    protected IronsourceInterstitialAdapter(Activity activity, YumiProviderBean yumiProviderBean) {
        super(activity, yumiProviderBean);
    }

    @Override
    protected void onPrepareInterstitial() {
        final String instanceId = getProvider().getKey2();
        boolean isReady = IronSource.isISDemandOnlyInterstitialReady(instanceId);
        ZplayDebug.d(TAG, "onPrepareInterstitial: " + isReady + ", instanceId: " + instanceId);
        updateGDPRStatus(getContext());
        if (isReady) {
            layerPrepared();
        } else {
            IronSource.loadISDemandOnlyInterstitial(instanceId);
        }
    }

    @Override
    protected void onShowInterstitialLayer(Activity activity) {
        final String instanceId = getProvider().getKey2();
        ZplayDebug.d(TAG, "onShowInterstitialLayer: " + instanceId);
        IronSource.showISDemandOnlyInterstitial(instanceId);
    }

    @Override
    protected boolean isInterstitialLayerReady() {
        final String instanceId = getProvider().getKey2();
        boolean isReady = IronSource.isISDemandOnlyInterstitialReady(instanceId);
        ZplayDebug.d(TAG, "isInterstitialLayerReady: " + isReady + ", instanceId: " + instanceId);
        return isReady;
    }

    @Override
    protected void init() {
        final String appKey = getProvider().getKey1();
        ZplayDebug.d(TAG, "init: " + appKey);
        createMediaListener();
        IronsourceListenerHandler.initIronsourceInterstitialListener(getActivity(), appKey);
    }


    private void createMediaListener() {
        if (adListener == null) {
            adListener = new ISDemandOnlyInterstitialListener() {

                /**
                 * Invoked when Interstitial Ad is ready to be shown after load function was called.
                 */
                @Override
                public void onInterstitialAdReady(String instanceId) {
                    ZplayDebug.d(TAG, "onInterstitialAdReady: " + instanceId);
                    if (instanceId.equals(getProvider().getKey2())) {
                        layerPrepared();
                    }
                }

                /**
                 * invoked when there is no Interstitial Ad available after calling load function.
                 */
                @Override
                public void onInterstitialAdLoadFailed(String instanceId, IronSourceError error) {
                    ZplayDebug.d(TAG, "onInterstitialAdLoadFailed: " + instanceId + ", error: " + error);
                    if (instanceId.equals(getProvider().getKey2())) {
                        layerPreparedFailed(generateLayerErrorCode(error));
                    }
                }

                /**
                 * Invoked when the Interstitial Ad Unit is opened
                 */
                @Override
                public void onInterstitialAdOpened(String instanceId) {
                    ZplayDebug.d(TAG, "onInterstitialAdOpened: " + instanceId);
                    if (instanceId.equals(getProvider().getKey2())) {
                        layerExposure();
                        layerStartPlaying();
                    }
                }

                /*
                 * Invoked when the ad is closed and the user is about to return to the application.
                 */
                @Override
                public void onInterstitialAdClosed(String instanceId) {
                    ZplayDebug.d(TAG, "onInterstitialAdClosed: " + instanceId);
                    if (instanceId.equals(getProvider().getKey2())) {
                        layerClosed();
                    }
                }

                /**
                 * Invoked when Interstitial ad failed to show.
                 * // @param error - An object which represents the reason of showInterstitial failure.
                 */
                @Override
                public void onInterstitialAdShowFailed(String instanceId, IronSourceError error) {
                    ZplayDebug.d(TAG, "onInterstitialAdShowFailed: " + instanceId + ", error: " + error);
                    if (TextUtils.equals(instanceId, getProvider().getKey2())) {
                        AdError adError = new AdError(ERROR_FAILED_TO_SHOW);
                        adError.setErrorMessage("IronSource errorMsg: " + error);
                        layerExposureFailed(adError);
                    }
                }

                /*
                 * Invoked when the end user clicked on the interstitial ad.
                 */
                @Override
                public void onInterstitialAdClicked(String instanceId) {
                    ZplayDebug.d(TAG, "onInterstitialAdClicked: " + instanceId);
                    if (instanceId.equals(getProvider().getKey2())) {
                        layerClicked(-99f, -99f);
                    }
                }
            };
            IronsourceListenerHandler.setMyIronsourceInterstitialListener(getProvider().getKey2(), adListener);
        }
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
    public boolean onActivityBackPressed() {
        return false;
    }

    @Override
    public String getProviderVersion() {
        return sdkVersion();
    }
}
