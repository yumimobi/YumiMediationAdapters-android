package com.yumi.android.sdk.ads.adapter.inmobi;

import android.app.Activity;

import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiInterstitial;
import com.inmobi.ads.listeners.InterstitialAdEventListener;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerMediaAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import java.util.Map;

import static com.yumi.android.sdk.ads.adapter.inmobi.InmobUtil.recodeError;
import static com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode.ERROR_OVER_RETRY_LIMIT;

public class InmobiMediaAdapter extends YumiCustomerMediaAdapter {

    private static final String TAG = "InmobiMediaAdapter";
    private InMobiInterstitial media;
    private InterstitialAdEventListener mediaListener;
    private boolean isCallbackInExposure = false;


    protected InmobiMediaAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }

    @Override
    public void onActivityPause() {
    }

    @Override
    public void onActivityResume() {
    }

    @Override
    protected final void callOnActivityDestroy() {

    }

    @Override
    protected void onPrepareMedia() {
        ZplayDebug.d(TAG, "inmobi request new media", onoff);
        isCallbackInExposure = false;
        if (media == null) {
            String key2 = getProvider().getKey2();
            long placementID = 0L;
            if (key2 != null && key2.length() > 0) {
                try {
                    placementID = Long.valueOf(key2);
                } catch (NumberFormatException e) {
                    ZplayDebug.e(TAG, "", e, onoff);
                    layerPreparedFailed(recodeError(ERROR_OVER_RETRY_LIMIT));
                    return;
                }
            } else {
                layerPreparedFailed(recodeError(ERROR_OVER_RETRY_LIMIT));
                return;
            }
            media = new InMobiInterstitial(getActivity(), placementID,
                    mediaListener);
        }
        media.load();
    }

    @Override
    protected void onShowMedia() {
        isCallbackInExposure = true;
        media.show();
    }

    @Override
    protected boolean isMediaReady() {
        if (media != null && media.isReady()) {
            return true;
        }
        return false;
    }

    @Override
    protected void init() {
        ZplayDebug.i(TAG, "accounID : " + getProvider().getKey1(), onoff);
        ZplayDebug.i(TAG, "placementID : " + getProvider().getKey2(), onoff);
        InmobiExtraHolder.initInmobiSDK(getActivity(), getProvider().getKey1());
        mediaListener = new InterstitialAdEventListener() {

            @Override
            public void onUserLeftApplication(InMobiInterstitial arg0) {
                ZplayDebug.d(TAG, "inmobi media left application", onoff);
                layerClicked();
            }

            @Override
            public void onRewardsUnlocked(InMobiInterstitial inMobiInterstitial, Map<Object, Object> map) {
                super.onRewardsUnlocked(inMobiInterstitial, map);
                ZplayDebug.d(TAG, "onRewardsUnlocked ", onoff);
                layerIncentived();
                layerMediaEnd();
            }

            @Override
            public void onAdLoadSucceeded(InMobiInterstitial arg0) {
                if (!isCallbackInExposure) {
                    ZplayDebug.d(TAG, "inmobi media load successed", onoff);
                    layerPrepared();
                }
            }

            @Override
            public void onAdLoadFailed(InMobiInterstitial arg0,
                                       InMobiAdRequestStatus arg1) {
                if (!isCallbackInExposure) {
                    ZplayDebug.d(TAG, "inmobi media load failed " + arg1.getStatusCode(), onoff);
                    layerPreparedFailed(recodeError(arg1));
                }
            }

            @Override
            public void onAdDisplayed(InMobiInterstitial arg0) {
                ZplayDebug.d(TAG, "inmobi media exposure", onoff);
                layerExposure();
                layerMediaStart();
            }

            @Override
            public void onAdDismissed(InMobiInterstitial arg0) {
                ZplayDebug.d(TAG, "inmobi media closed", onoff);
                layerClosed();
            }
        };
    }

}
