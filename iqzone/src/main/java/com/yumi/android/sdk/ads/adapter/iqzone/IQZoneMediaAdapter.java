package com.yumi.android.sdk.ads.adapter.iqzone;

import android.app.Activity;

import com.iqzone.android.AdEventsListener;
import com.iqzone.android.IQzoneInterstitialAdManager;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerMediaAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode.CODE_FAILED;

/**
 * Description:
 * <p>
 * Created by lgd on 2018/11/2.
 */
public class IQZoneMediaAdapter extends YumiCustomerMediaAdapter {
    private static final String TAG = "IQZoneMediaAdapter";
    private IQzoneInterstitialAdManager imdRewardedVideoAdManager;
    private boolean isReady;

    protected IQZoneMediaAdapter(Activity activity, YumiProviderBean yumiProviderBean) {
        super(activity, yumiProviderBean);
    }

    @Override
    protected void onPrepareMedia() {
        ZplayDebug.d(TAG, "Video onPrepareMedia", onoff);
        imdRewardedVideoAdManager.loadInterstitial();
    }

    @Override
    protected void onShowMedia() {
        ZplayDebug.d(TAG, "Video onShowMedia", onoff);
        imdRewardedVideoAdManager.showInterstitial();
    }

    @Override
    protected boolean isMediaReady() {
        return isReady;
    }

    @Override
    protected void init() {
        imdRewardedVideoAdManager = new IQzoneInterstitialAdManager(getContext(), getProvider().getKey1(), newAdEventsListener());
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
                layerIncentived();
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
                layerMediaStart();
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
        imdRewardedVideoAdManager.onDetached();
    }

    @Override
    public void onActivityResume() {
        imdRewardedVideoAdManager.onAttached(getActivity());
    }
}
