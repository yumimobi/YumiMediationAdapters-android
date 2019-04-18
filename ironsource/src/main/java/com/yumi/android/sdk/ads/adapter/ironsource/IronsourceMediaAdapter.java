package com.yumi.android.sdk.ads.adapter.ironsource;

import android.app.Activity;

import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.model.Placement;
import com.ironsource.mediationsdk.sdk.ISDemandOnlyRewardedVideoListener;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerMediaAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

/**
 * Created by hjl on 2018/8/10.
 */
public class IronsourceMediaAdapter extends YumiCustomerMediaAdapter {

    private static final String TAG = "IronsourceMediaAdapter";
    private ISDemandOnlyRewardedVideoListener adListener;

    protected IronsourceMediaAdapter(Activity activity, YumiProviderBean yumiProviderBean) {
        super(activity, yumiProviderBean);
    }

    @Override
    protected void onPrepareMedia() {

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
            // Invoked when the RewardedVideo ad view is about to open.
            @Override
            public void onRewardedVideoAdOpened(String instanceId) {
                ZplayDebug.i(TAG, "IronSource Media onRewardedVideoAdOpened instanceId : " + instanceId, onoff);
                if (instanceId.equals(getProvider().getKey2())) {
                    layerExposure();
                }
            }

            //Invoked when the RewardedVideo ad view is about to be closed.
            //Your activity will now regain its focus.
            @Override
            public void onRewardedVideoAdClosed(String instanceId) {
                ZplayDebug.i(TAG, "IronSource Media onRewardedVideoAdClosed instanceId:" + instanceId, onoff);
                if (instanceId.equals(getProvider().getKey2())) {
                    layerClosed();
                }
            }

            // Invoked when there is a change in the ad availability status.
            // @param - available - value will change to true when rewarded videos
            // are available.
            // You can then show the video by calling showRewardedVideo().
            // Value will change to false when no videos are available.
            // Change the in-app 'Traffic Driver' state according to availability.
            @Override
            public void onRewardedVideoAvailabilityChanged(String instanceId, boolean available) {
                ZplayDebug.i(TAG, "IronSource Media onRewardedVideoAvailabilityChanged instanceId : " + instanceId + "  available : " + available, onoff);
                if (instanceId.equals(getProvider().getKey2()) && available) {
                    layerPrepared();
                }
            }

            // Invoked when the user completed the video and should be rewarded.
            // If using server-to-server callbacks you may ignore this events and
            // wait for the callback from the ironSource server.
            // @param - placement - the Placement the user completed a video
            // from.
            @Override
            public void onRewardedVideoAdRewarded(String instanceId, Placement placement) {
                // here you can reward the user according to the given amount.
                String rewardName = placement.getRewardName();
                int rewardAmount = placement.getRewardAmount();
                ZplayDebug.i(TAG, "IronSource Media onRewardedVideoAdRewarded instanceId : " + instanceId + "  placement:" + placement.getPlacementName(), onoff);
                if (instanceId.equals(getProvider().getKey2())) {
                    layerIncentived();
                }
            }

            // Invoked when RewardedVideo call to show a rewarded video has
            // failed. IronSourceError contains the reason for the failure.
            @Override
            public void onRewardedVideoAdShowFailed(String instanceId, IronSourceError error) {
                ZplayDebug.e(TAG, "IronSource Media onRewardedVideoAdShowFailed  instanceId : " + instanceId + "  getErrorCode : " + error.getErrorCode() + "   || getErrorMessage : " + error.getErrorMessage(), onoff);
            }

            // Invoked when the end user clicked on the RewardedVideo ad
            @Override
            public void onRewardedVideoAdClicked(String instanceId, Placement placement) {
                ZplayDebug.i(TAG, "IronSource Media onRewardedVideoAdClicked instanceId : " + instanceId + "  placement:" + placement.getPlacementName(), onoff);
                if (instanceId.equals(getProvider().getKey2())) {
                    layerClicked();
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
