package com.yumi.android.sdk.ads.adapter.iqzone;

import android.app.Activity;

import com.iqzone.android.AdEventsListener;
import com.iqzone.android.IQzoneBannerView;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerBannerAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.yumi.android.sdk.ads.adapter.iqzone.IQZoneUtil.recodeError;
import static com.yumi.android.sdk.ads.adapter.iqzone.IQZoneUtil.updateGDPRStatus;
import static com.yumi.android.sdk.ads.publish.enumbean.AdSize.BANNER_SIZE_SMART;
import static com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode.ERROR_INTERNAL;
import static com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode.ERROR_NO_FILL;

/**
 * Description:
 * <p>
 * Created by lgd on 2018/11/5.
 */
public class IQZoneBannerAdapter extends YumiCustomerBannerAdapter {
    private static final String TAG = "IQZoneBannerAdapter";
    private IQzoneBannerView imdBannerAd;
    private boolean hasShownAd;

    protected IQZoneBannerAdapter(Activity activity, YumiProviderBean yumiProviderBean) {
        super(activity, yumiProviderBean);
        updateGDPRStatus(activity);
    }

    @Override
    protected void onPrepareBannerLayer() {
        if (bannerSize == BANNER_SIZE_SMART) {
            ZplayDebug.d(TAG, "IQZone not support smart banner", onoff);
            layerPreparedFailed(recodeError(ERROR_NO_FILL, "not support smart banner."));
            return;
        }
        imdBannerAd.onAttached(getActivity());
        imdBannerAd.loadAd(getProvider().getKey1(), getProvider().getAutoRefreshInterval(), newAdEventListener());
    }

    private AdEventsListener newAdEventListener() {
        return new AdEventsListener() {
            @Override
            public void adLoaded() {
                ZplayDebug.d(TAG, "IQZone Banner adLoaded", onoff);
                layerPrepared(imdBannerAd, false);
            }

            @Override
            public void adImpression() {
                ZplayDebug.d(TAG, "IQZone Banner adImpression", onoff);
                layerExposure();
                hasShownAd = true;
            }

            @Override
            public void adClicked() {
                ZplayDebug.d(TAG, "IQZone Banner adClicked", onoff);
                layerClicked(-99f, -99f);
            }

            @Override
            public void adFailedToLoad() {
                ZplayDebug.d(TAG, "IQZone Banner adFailedToLoad", onoff);
                layerPreparedFailed(recodeError(ERROR_INTERNAL));
            }

            @Override
            public void videoStarted() {
                ZplayDebug.d(TAG, "IQZone Banner videoStarted", onoff);
            }

            @Override
            public void videoCompleted(boolean b) {
                ZplayDebug.d(TAG, "IQZone Banner videoCompleted", onoff);
            }

            @Override
            public void adDismissed() {
                ZplayDebug.d(TAG, "IQZone Banner adDismissed", onoff);
                if (hasShownAd) {
                    hasShownAd = false;
                    layerClosed();
                }
            }
        };
    }

    @Override
    protected void init() {
        imdBannerAd = new IQzoneBannerView(getActivity());
    }

    @Override
    protected void callOnActivityDestroy() {
        ZplayDebug.d(TAG, "IQZone Banner callOnActivityDestroy", onoff);
        imdBannerAd.onDetached();
    }

    @Override
    public void onActivityPause() {
        ZplayDebug.d(TAG, "IQZone Banner onActivityPause", onoff);
    }

    @Override
    public void onActivityResume() {
        ZplayDebug.d(TAG, "IQZone Banner onActivityResume", onoff);
    }
}
