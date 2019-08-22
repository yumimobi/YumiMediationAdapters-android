package com.yumi.android.sdk.ads.adapter.chartboost;

import android.app.Activity;

import com.chartboost.sdk.CBLocation;
import com.chartboost.sdk.Chartboost;
import com.chartboost.sdk.ChartboostDelegate;
import com.chartboost.sdk.Model.CBError.CBImpressionError;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerMediaAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.yumi.android.sdk.ads.adapter.chartboost.ChartboostUtil.recodeError;
import static com.yumi.android.sdk.ads.adapter.chartboost.ChartboostUtil.sdkVersion;
import static com.yumi.android.sdk.ads.adapter.chartboost.ChartboostUtil.updateGDPRStatus;

public class ChartboostMediaAdapter extends YumiCustomerMediaAdapter {

    private static final String TAG = "ChartboostMediaAdapter";
    private ChartboostDelegate delegate;
    private boolean isRewarded = false;

    protected ChartboostMediaAdapter(Activity activity,
                                     YumiProviderBean provider) {
        super(activity, provider);
    }

    @Override
    public void onActivityPause() {
        Chartboost.onPause(getActivity());
        Chartboost.onStop(getActivity());
    }

    @Override
    public void onActivityResume() {
        Chartboost.onStart(getActivity());
        Chartboost.onResume(getActivity());
    }

    @Override
    protected final void onDestroy() {
        ChartboostExtra.getChartboostExtra().onDestroy();
        Chartboost.onDestroy(getActivity());
    }

    @Override
    protected void onPrepareMedia() {
        ZplayDebug.d(TAG, "chartboost request new media", onoff);
        updateGDPRStatus(getContext());
        Chartboost.cacheRewardedVideo(CBLocation.LOCATION_ACHIEVEMENTS);
    }

    @Override
    protected void onShowMedia() {
        Chartboost.showRewardedVideo(CBLocation.LOCATION_ACHIEVEMENTS);
    }

    @Override
    protected boolean isMediaReady() {
        if (Chartboost.hasRewardedVideo(CBLocation.LOCATION_ACHIEVEMENTS)) {
            return true;
        }
        return false;
    }

    @Override
    protected void init() {
        ZplayDebug.i(TAG, "appId : " + getProvider().getKey1(), onoff);
        ZplayDebug.i(TAG, "appSignature : " + getProvider().getKey2(), onoff);
        createDelegate();
        ChartboostExtra.getChartboostExtra().setMediaListener(delegate);
        ChartboostExtra.getChartboostExtra().initChartboostSDK(getActivity(),
                getProvider().getKey1(), getProvider().getKey2());
    }

    private void createDelegate() {
        if (delegate == null) {
            delegate = new ChartboostDelegate() {
                @Override
                public void didCacheRewardedVideo(String location) {
                    ZplayDebug.d(TAG, "chartboost media prepared", onoff);
                    layerPrepared();
                }

                @Override
                public void didFailToLoadRewardedVideo(String location,
                                                       CBImpressionError error) {
                    ZplayDebug.d(TAG, "chartboost media failed " + error, onoff);
                    layerPreparedFailed(recodeError(error));
                    super.didFailToLoadRewardedVideo(location, error);
                }

                @Override
                public void didCloseRewardedVideo(String location) {
                    ZplayDebug.d(TAG, "chartboost media closed", onoff);
                    layerClosed(isRewarded);
                    super.didCloseRewardedVideo(location);
                }

                @Override
                public void didClickRewardedVideo(String location) {
                    ZplayDebug.d(TAG, "chartboost media clicked", onoff);
                    layerClicked();
                    super.didClickRewardedVideo(location);
                }

                @Override
                public void didDismissRewardedVideo(String location) {
                    ZplayDebug.d(TAG, "chartboost media shown", onoff);
                    isRewarded = false;
                    layerExposure();
                    layerStartPlaying();
                    super.didDismissRewardedVideo(location);
                }

                @Override
                public void didCompleteRewardedVideo(String location, int reward) {
                    ZplayDebug.d(TAG, "chartboost media get rewarded", onoff);
                    isRewarded = true;
                    layerIncentived();
                }
            };
        }
    }

    @Override
    public String getProviderVersion() {
        return sdkVersion();
    }
}
