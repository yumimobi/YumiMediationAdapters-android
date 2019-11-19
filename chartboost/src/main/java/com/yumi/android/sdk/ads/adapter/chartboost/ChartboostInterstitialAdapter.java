package com.yumi.android.sdk.ads.adapter.chartboost;

import android.app.Activity;

import com.chartboost.sdk.CBLocation;
import com.chartboost.sdk.Chartboost;
import com.chartboost.sdk.ChartboostDelegate;
import com.chartboost.sdk.Model.CBError.CBImpressionError;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerInterstitialAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.yumi.android.sdk.ads.adapter.chartboost.ChartboostUtil.recodeError;
import static com.yumi.android.sdk.ads.adapter.chartboost.ChartboostUtil.sdkVersion;
import static com.yumi.android.sdk.ads.adapter.chartboost.ChartboostUtil.updateGDPRStatus;

public class ChartboostInterstitialAdapter extends
        YumiCustomerInterstitialAdapter {

    private static final String TAG = "ChartboostInterstitialAdapter";
    private ChartboostDelegate delegate;

    protected ChartboostInterstitialAdapter(Activity activity,
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
    public boolean onActivityBackPressed() {
        return Chartboost.onBackPressed();
    }

    @Override
    protected void onPrepareInterstitial() {
        ZplayDebug.d(TAG, "onPrepareInterstitial: ");
        updateGDPRStatus(getContext());
        Chartboost.cacheInterstitial(CBLocation.LOCATION_DEFAULT);
    }

    @Override
    protected void onShowInterstitialLayer(Activity activity) {
        Chartboost.showInterstitial(CBLocation.LOCATION_DEFAULT);
    }

    @Override
    protected boolean isInterstitialLayerReady() {
        return Chartboost.hasInterstitial(CBLocation.LOCATION_DEFAULT);
    }

    @Override
    protected void init() {
        ZplayDebug.i(TAG, "init appId: " + getProvider().getKey1() + ", appSignature: " + getProvider().getKey2());
        createDelegate();
        ChartboostExtra.getChartboostExtra().setInterstitialListener(delegate);
        ChartboostExtra.getChartboostExtra().initChartboostSDK(getActivity(),
                getProvider().getKey1(), getProvider().getKey2());
    }

    private void createDelegate() {
        if (delegate == null) {
            delegate = new ChartboostDelegate() {
                @Override
                public void didCacheInterstitial(String location) {
                    ZplayDebug.d(TAG, "didCacheInterstitial");
                    layerPrepared();
                    super.didCacheInterstitial(location);
                }

                @Override
                public void didFailToLoadInterstitial(String location,
                                                      CBImpressionError error) {
                    ZplayDebug.d(TAG, "didFailToLoadInterstitial error: " + error);
                    layerPreparedFailed(recodeError(error));
                    super.didFailToLoadInterstitial(location, error);
                }

                @Override
                public void didCloseInterstitial(String location) {
                    ZplayDebug.d(TAG, "didCloseInterstitial");
                    layerClosed();
                    super.didCloseInterstitial(location);
                }

                @Override
                public void didClickInterstitial(String location) {
                    ZplayDebug.d(TAG, "didClickInterstitial");
                    layerClicked(-99f, -99f);
                    layerClosed();
                    super.didClickInterstitial(location);
                }

                @Override
                public void didDisplayInterstitial(String location) {
                    ZplayDebug.d(TAG, "didDisplayInterstitial");
                    layerExposure();
                    layerStartPlaying();
                    super.didDisplayInterstitial(location);
                }
            };
        }
    }

    @Override
    public String getProviderVersion() {
        return sdkVersion();
    }
}
