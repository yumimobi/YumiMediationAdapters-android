package com.yumi.android.sdk.ads.adapter.ironsource;

import android.app.Activity;

import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.model.Placement;
import com.ironsource.mediationsdk.sdk.RewardedVideoListener;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerMediaAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

/**
 * Created by hjl on 2018/8/10.
 */
public class IronsourceMediaAdapter extends YumiCustomerMediaAdapter {

    private static final String TAG = "IronsourceMediaAdapter";
    private RewardedVideoListener adListener;

    protected IronsourceMediaAdapter(Activity activity, YumiProviderBean yumiProviderBean) {
        super(activity, yumiProviderBean);
    }

    @Override
    protected void onPrepareMedia() {

    }

    @Override
    protected void onShowMedia() {
        boolean isReady = IronSource.isRewardedVideoAvailable();
        ZplayDebug.i(TAG, "IronSource Media onShowMedia isReady : " + isReady, onoff);
        if (isReady) {
            IronSource.showRewardedVideo();
        }
    }

    @Override
    protected boolean isMediaReady() {
        boolean isReady = IronSource.isRewardedVideoAvailable();
        ZplayDebug.i(TAG, "IronSource Media isMediaReady : " + isReady, onoff);
        return isReady;
    }

    @Override
    protected void init() {
        ZplayDebug.i(TAG, "IronSource Media init Key1 : " + getProvider().getKey1() + "  Key2 : " + getProvider().getKey2(), onoff);
        createMediaListener();
        IronSource.setRewardedVideoListener(adListener);
        IronSource.init(getActivity(), getProvider().getKey1(), IronSource.AD_UNIT.REWARDED_VIDEO);
    }

    private void createMediaListener() {
        adListener = new RewardedVideoListener() {
            /**
             * Invoked when the RewardedVideo ad view has opened.
             * Your Activity will lose focus. Please avoid performing heavy
             * tasks till the video ad will be closed.
             */
            @Override
            public void onRewardedVideoAdOpened() {
                ZplayDebug.i(TAG, "IronSource Media onRewardedVideoAdOpened ", onoff);
                layerExposure();
                layerMediaStart();
            }

            /*Invoked when the RewardedVideo ad view is about to be closed.
            Your activity will now regain its focus.*/
            @Override
            public void onRewardedVideoAdClosed() {
                ZplayDebug.i(TAG, "IronSource Media onRewardedVideoAdClosed ", onoff);
                layerMediaEnd();
                layerClosed();
            }

            /**
             * Invoked when there is a change in the ad availability status.
             *
             * @param - available - value will change to true when rewarded videos are *available.
             *          You can then show the video by calling showRewardedVideo().
             *          Value will change to false when no videos are available.
             */
            @Override
            public void onRewardedVideoAvailabilityChanged(boolean available) {
                //Change the in-app 'Traffic Driver' state according to availability.
                ZplayDebug.i(TAG, "IronSource Media onRewardedVideoAvailabilityChanged : " + available, onoff);
                if(available)
                {
                    layerPrepared();
                }
            }

            /**
             * Invoked when the video ad starts playing.
             */
            @Override
            public void onRewardedVideoAdStarted() {
                ZplayDebug.i(TAG, "IronSource Media onRewardedVideoAdStarted ", onoff);
            }

            /* Invoked when the video ad finishes plating. */
            @Override
            public void onRewardedVideoAdEnded() {
                ZplayDebug.i(TAG, "IronSource Media onRewardedVideoAdEnded ", onoff);
            }

            /**
             * Invoked when the user completed the video and should be rewarded.
             * If using server-to-server callbacks you may ignore this events and wait *for the callback from the ironSource server.
             *
             * @param - placement - the Placement the user completed a video from.
             */
            @Override
            public void onRewardedVideoAdRewarded(Placement placement) {
                ZplayDebug.i(TAG, "IronSource Media onRewardedVideoAdRewarded placement:" + placement.getPlacementName(), onoff);
                layerIncentived();
            }

            /* Invoked when RewardedVideo call to show a rewarded video has failed
             * IronSourceError contains the reason for the failure.
             */
            @Override
            public void onRewardedVideoAdShowFailed(IronSourceError error) {
                ZplayDebug.e(TAG, "IronSource Media onRewardedVideoAdEnded getErrorCode : " + error.getErrorCode() + "   || getErrorMessage : " + error.getErrorMessage(), onoff);
            }

            /*Invoked when the end user clicked on the RewardedVideo ad
             */
            @Override
            public void onRewardedVideoAdClicked(Placement placement) {
                ZplayDebug.i(TAG, "IronSource Media onRewardedVideoAdRewarded placement:" + placement.getPlacementName(), onoff);
                layerClicked();
            }
        };
    }

    @Override
    protected void callOnActivityDestroy() {
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
