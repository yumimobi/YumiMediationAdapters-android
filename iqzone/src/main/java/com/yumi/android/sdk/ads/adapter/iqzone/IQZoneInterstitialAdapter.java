package com.yumi.android.sdk.ads.adapter.iqzone;

import android.app.Activity;

import com.iqzone.android.AdEventsListener;
import com.iqzone.android.IQzoneInterstitialAdManager;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerInterstitialAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode.CODE_FAILED;

/**
 * Description:
 * <p>
 * Created by lgd on 2018/11/2.
 */
public class IQZoneInterstitialAdapter extends YumiCustomerInterstitialAdapter {
    private static final String TAG = "IQZoneMediaAdapter";
    private IQzoneInterstitialAdManager imdInterstitialAdManager;
    private boolean isReady;

    protected IQZoneInterstitialAdapter(Activity activity, YumiProviderBean yumiProviderBean) {
        super(activity, yumiProviderBean);
    }

    @Override
    protected void onPrepareInterstitial() {
        ZplayDebug.d(TAG, "Video onPrepareMedia", onoff);
        imdInterstitialAdManager.loadInterstitial();
    }

    @Override
    protected void onShowInterstitialLayer(Activity activity) {
        ZplayDebug.d(TAG, "Video onShowMedia", onoff);
        imdInterstitialAdManager.showInterstitial();
    }

    @Override
    protected boolean isInterstitialLayerReady() {
        return isReady;
    }

    @Override
    protected void init() {
        imdInterstitialAdManager = new IQzoneInterstitialAdManager(getContext(), getProvider().getKey1(), newAdEventsListener());
    }

    private AdEventsListener newAdEventsListener() {
        return new AdEventsListener() {
            @Override
            public void adLoaded() {
                ZplayDebug.d(TAG, "Playable Interstitial Ready", onoff);
                isReady = true;
                layerPrepared();
            }

            @Override
            public void adImpression() {
                isReady = false;
                ZplayDebug.d(TAG, "adImpression", onoff);
                layerExposure();
            }

            @Override
            public void adDismissed() {
                ZplayDebug.d(TAG, "adDismissed", onoff);
                layerClosed();
            }

            @Override
            public void adFailedToLoad() {
                ZplayDebug.d(TAG, "adFailedToLoad", onoff);
                layerPreparedFailed(CODE_FAILED);
            }

            @Override
            public void videoStarted() {
                ZplayDebug.d(TAG, "videoStarted", onoff);
            }

            @Override
            public void videoCompleted(boolean skipped) {
                ZplayDebug.d(TAG, "videoCompleted", onoff);
                layerMediaEnd();
            }
        };
    }

    @Override
    protected void callOnActivityDestroy() {
    }

    @Override
    public void onActivityPause() {
        imdInterstitialAdManager.onDetached();
    }

    @Override
    public void onActivityResume() {
        imdInterstitialAdManager.onAttached(getActivity());
    }

    @Override
    public boolean onActivityBackPressed() {
        return false;
    }
}