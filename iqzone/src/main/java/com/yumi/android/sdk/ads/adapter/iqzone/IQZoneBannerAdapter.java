package com.yumi.android.sdk.ads.adapter.iqzone;

import android.app.Activity;

import com.iqzone.android.AdEventsListener;
import com.iqzone.android.IQzoneBannerView;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerBannerAdapter;

import static com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode.ERROR_INTERNAL;

/**
 * Description:
 * <p>
 * Created by lgd on 2018/11/5.
 */
public class IQZoneBannerAdapter extends YumiCustomerBannerAdapter {
    private static final String TAG = "IQZoneBannerAdapter";
    private IQzoneBannerView imdBannerAd;

    protected IQZoneBannerAdapter(Activity activity, YumiProviderBean yumiProviderBean) {
        super(activity, yumiProviderBean);
    }

    @Override
    protected void onPrepareBannerLayer() {
        imdBannerAd = new IQzoneBannerView(getActivity());
        imdBannerAd.loadAd(getProvider().getKey1(), 0, newAdEventListener());
    }

    private AdEventsListener newAdEventListener() {
        return new AdEventsListener() {
            @Override
            public void adLoaded() {
                ZplayDebug.d(TAG, "adLoaded", onoff);
                layerPrepared(imdBannerAd, true);
            }

            @Override
            public void adImpression() {
                ZplayDebug.d(TAG, "adImpression", onoff);
            }

            @Override
            public void adFailedToLoad() {
                ZplayDebug.d(TAG, "adFailedToLoad", onoff);
                layerPreparedFailed(ERROR_INTERNAL);
            }

            @Override
            public void videoStarted() {
                ZplayDebug.d(TAG, "videoStarted", onoff);
            }

            @Override
            public void videoCompleted(boolean b) {
                ZplayDebug.d(TAG, "videoCompleted", onoff);
            }

            @Override
            public void adDismissed() {
                ZplayDebug.d(TAG, "adDismissed", onoff);
                layerClosed();
            }
        };
    }

    @Override
    protected void init() {
    }

    @Override
    protected void callOnActivityDestroy() {
    }

    @Override
    public void onActivityPause() {
        imdBannerAd.onDetached();
    }

    @Override
    public void onActivityResume() {
        imdBannerAd.onAttached(getActivity());
    }
}
