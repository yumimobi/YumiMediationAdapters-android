package com.yumi.android.sdk.ads.adapter.ironsource;

import android.app.Activity;

import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.ISDemandOnlyInterstitialListener;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerInterstitialAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

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
        ZplayDebug.i(TAG, "IronSource Interstitial onPrepareInterstitial  instanceId : "+getProvider().getKey2(), onoff);
        boolean isReady = IronSource.isISDemandOnlyInterstitialReady(getProvider().getKey2());
        if(isReady)
        {
            ZplayDebug.i(TAG, "IronSource Interstitial onPrepareInterstitial isReady  instanceId : "+getProvider().getKey2(), onoff);
            layerPrepared();
        }else {
            IronSource.loadISDemandOnlyInterstitial(getProvider().getKey2());
        }
    }

    @Override
    protected void onShowInterstitialLayer(Activity activity) {
        ZplayDebug.i(TAG, "IronSource Interstitial onShowInterstitialLayer   instanceId : "+getProvider().getKey2(), onoff);
        IronSource.showISDemandOnlyInterstitial(getProvider().getKey2());
    }

    @Override
    protected boolean isInterstitialLayerReady() {
        boolean isReady = IronSource.isISDemandOnlyInterstitialReady(getProvider().getKey2());
        ZplayDebug.i(TAG, "IronSource Interstitial isInterstitialLayerReady   instanceId : "+getProvider().getKey2()+"  isReady : " + isReady, onoff);
        return isReady;
    }

    @Override
    protected void init() {
        ZplayDebug.i(TAG, "IronSource Interstitial init Key1 : " + getProvider().getKey1() + "  Key2 : " + getProvider().getKey2(), onoff);
        createMediaListener();
        IronsourceListenerHandler.initIronsourceInterstitialListener(getActivity(),getProvider().getKey1());
    }


    private void createMediaListener() {
        if(adListener==null) {
            adListener = new ISDemandOnlyInterstitialListener() {

                /**
                 * Invoked when Interstitial Ad is ready to be shown after load function was called.
                 */
                @Override
                public void onInterstitialAdReady(String instanceId) {
                    ZplayDebug.i(TAG, "IronSource Interstitial onInterstitialAdReady instanceId : " + instanceId, onoff);
                    if (instanceId.equals(getProvider().getKey2())) {
                        layerPrepared();
                    }
                }

                /**
                 * invoked when there is no Interstitial Ad available after calling load function.
                 */
                @Override
                public void onInterstitialAdLoadFailed(String instanceId, IronSourceError error) {
                    ZplayDebug.e(TAG, "IronSource Interstitial onInterstitialAdLoadFailed : " + instanceId + "   getErrorCode : " + error.getErrorCode() + "   || getErrorMessage : " + error.getErrorMessage(), onoff);
                    if (instanceId.equals(getProvider().getKey2())) {
                        if (error.getErrorCode() == IronSourceError.ERROR_BN_LOAD_NO_FILL) {
                            layerPreparedFailed(LayerErrorCode.ERROR_NO_FILL);
                        } else {
                            layerPreparedFailed(LayerErrorCode.ERROR_INTERNAL);
                        }
                    }
                }

                /**
                 * Invoked when the Interstitial Ad Unit is opened
                 */
                @Override
                public void onInterstitialAdOpened(String instanceId) {
                    ZplayDebug.i(TAG, "IronSource Interstitial onInterstitialAdOpened instanceId : " + instanceId, onoff);
                }

                /*
                 * Invoked when the ad is closed and the user is about to return to the application.
                 */
                @Override
                public void onInterstitialAdClosed(String instanceId) {
                    ZplayDebug.i(TAG, "IronSource Interstitial onInterstitialAdClosed instanceId : " + instanceId, onoff);
                    if (instanceId.equals(getProvider().getKey2())) {
                        layerClosed();
                    }
                }

                /*
                 * Invoked when the ad was opened and shown successfully.
                 */
                @Override
                public void onInterstitialAdShowSucceeded(String instanceId) {
                    ZplayDebug.i(TAG, "IronSource Interstitial onInterstitialAdShowSucceeded instanceId : " + instanceId, onoff);
                    if (instanceId.equals(getProvider().getKey2())) {
                        layerExposure();
                    }
                }

                /**
                 * Invoked when Interstitial ad failed to show.
                 * // @param error - An object which represents the reason of showInterstitial failure.
                 */
                @Override
                public void onInterstitialAdShowFailed(String instanceId, IronSourceError error) {
                    ZplayDebug.e(TAG, "IronSource Interstitial onInterstitialAdShowFailed instanceId : " + instanceId + "  getErrorCode : " + error.getErrorCode() + "   || getErrorMessage : " + error.getErrorMessage(), onoff);
                }

                /*
                 * Invoked when the end user clicked on the interstitial ad.
                 */
                @Override
                public void onInterstitialAdClicked(String instanceId) {
                    ZplayDebug.i(TAG, "IronSource Interstitial onInterstitialAdClicked instanceId : " + instanceId, onoff);
                    if (instanceId.equals(getProvider().getKey2())) {
                        layerClicked(-99f, -99f);
                    }
                }
            };
            IronsourceListenerHandler.setMyIronsourceInterstitialListener(adListener);
        }
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

    @Override
    public boolean onActivityBackPressed() {
        return false;
    }
}
