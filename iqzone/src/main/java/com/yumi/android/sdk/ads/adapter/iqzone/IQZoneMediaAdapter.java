package com.yumi.android.sdk.ads.adapter.iqzone;

import android.app.Activity;

import com.iqzone.android.AdEventsListener;
import com.iqzone.android.IQzoneInterstitialAdManager;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerMediaAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.yumi.android.sdk.ads.adapter.iqzone.IQZoneUtil.recodeError;
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
        ZplayDebug.d(TAG, "IQZone Video onPrepareMedia", onoff);
        imdRewardedVideoAdManager.loadInterstitial();
    }

    @Override
    protected void onShowMedia() {
        ZplayDebug.d(TAG, "IQZone Video onShowMedia", onoff);
        imdRewardedVideoAdManager.showInterstitial();
    }

    @Override
    protected boolean isMediaReady() {
        return isReady;
    }

    @Override
    protected void init() {
        ZplayDebug.d(TAG, "IQZone Video init", onoff);
        imdRewardedVideoAdManager = new IQzoneInterstitialAdManager(getContext(), getProvider().getKey1(), newAdEventsListener());
        imdRewardedVideoAdManager.onAttached(getActivity());
    }

    private AdEventsListener newAdEventsListener() {
        return new AdEventsListener() {
            @Override
            public void adLoaded() {
                ZplayDebug.d(TAG, "IQZone Video Interstitial Ready", onoff);
                isReady = true;
                layerPrepared();
            }

            @Override
            public void adImpression() {
                isReady = false;
                ZplayDebug.d(TAG, "IQZone Video adImpression", onoff);
                layerExposure();
            }

            @Override
            public void adClicked() {
                ZplayDebug.d(TAG, "IQZone Video adClicked", onoff);
                layerClicked();
            }

            @Override
            public void adDismissed() {
                ZplayDebug.d(TAG, "IQZone Video adDismissed", onoff);
                layerClosed();
            }

            @Override
            public void adFailedToLoad() {
                ZplayDebug.d(TAG, "IQZone Video adFailedToLoad", onoff);
                layerPreparedFailed(recodeError(CODE_FAILED));
            }

            @Override
            public void videoStarted() {
                ZplayDebug.d(TAG, "IQZone Video videoStarted", onoff);
                layerStartPlaying();
            }

            @Override
            public void videoCompleted(boolean skipped) {
                ZplayDebug.d(TAG, "IQZone Video videoCompleted", onoff);
                layerIncentived();
            }
        };
    }

    @Override
    protected void callOnActivityDestroy() {
    }

    @Override
    public void onActivityPause() {
        ZplayDebug.d(TAG, "IQZone Video onActivityPause", onoff);
        imdRewardedVideoAdManager.onDetached();
    }

    @Override
    public void onActivityResume() {
        ZplayDebug.d(TAG, "IQZone Video onActivityResume", onoff);
        imdRewardedVideoAdManager.onAttached(getActivity());
    }
}
