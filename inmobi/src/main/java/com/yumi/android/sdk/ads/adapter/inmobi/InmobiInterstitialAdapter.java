package com.yumi.android.sdk.ads.adapter.inmobi;

import android.app.Activity;

import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiInterstitial;
import com.inmobi.ads.listeners.InterstitialAdEventListener;
import com.inmobi.sdk.InMobiSdk;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerInterstitialAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import org.json.JSONException;
import org.json.JSONObject;

import static com.yumi.android.sdk.ads.adapter.inmobi.InmobUtil.recodeError;
import static com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode.ERROR_OVER_RETRY_LIMIT;

public class InmobiInterstitialAdapter extends YumiCustomerInterstitialAdapter {

    private static final String TAG = "InmobiInterstitialAdapter";
    private InMobiInterstitial interstitial;
    private InterstitialAdEventListener interstitialListener;

    protected InmobiInterstitialAdapter(Activity activity,
                                        YumiProviderBean provider) {
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
        InmobiExtraHolder.onDestroy();
    }

    @Override
    public boolean onActivityBackPressed() {
        return false;
    }

    @Override
    protected void onPrepareInterstitial() {
        ZplayDebug.d(TAG, "inmobi request new interstitial", onoff);
        if (interstitial == null) {
            String key2 = getProvider().getKey2();
            long placementID = 0L;
            if (key2 != null && key2.length() > 0) {
                try {
                    placementID = Long.valueOf(key2);
                } catch (NumberFormatException e) {
                    ZplayDebug.e(TAG, "", e, onoff);
                    layerPreparedFailed(recodeError(ERROR_OVER_RETRY_LIMIT, "inmobi key2 error"));
                    return;
                }
            } else {
                layerPreparedFailed(recodeError(ERROR_OVER_RETRY_LIMIT, "inmobi key2 error"));
                return;
            }
            JSONObject consent = new JSONObject();
            try {
                // Provide correct consent value to sdk which is obtained by User
                consent.put(InMobiSdk.IM_GDPR_CONSENT_AVAILABLE, true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            interstitial = new InMobiInterstitial(getActivity(), placementID, interstitialListener);
        }
        interstitial.load();
    }

    @Override
    protected void onShowInterstitialLayer(Activity activity) {
        interstitial.show();
    }

    @Override
    protected boolean isInterstitialLayerReady() {
        return interstitial != null
                && interstitial.isReady();
    }

    @Override
    protected void init() {
        ZplayDebug.i(TAG, "accounID : " + getProvider().getKey1(), onoff);
        ZplayDebug.i(TAG, "placementID : " + getProvider().getKey2(), onoff);
        InmobiExtraHolder.initInmobiSDK(getActivity(), getProvider().getKey1());
        interstitialListener = new InterstitialAdEventListener() {

            @Override
            public void onUserLeftApplication(InMobiInterstitial arg0) {
                ZplayDebug.d(TAG, "inmobi interstitial left application", onoff);
                layerClicked(-99f, -99f);
            }

            @Override
            public void onAdLoadSucceeded(InMobiInterstitial arg0) {
                ZplayDebug.d(TAG, "inmobi interstitial load successed", onoff);
                layerPrepared();
            }

            @Override
            public void onAdLoadFailed(InMobiInterstitial arg0,
                                       InMobiAdRequestStatus arg1) {
                ZplayDebug.d(TAG, "inmobi interstitial load failed " + arg1.getStatusCode(), onoff);
                layerPreparedFailed(recodeError(arg1));
            }

            @Override
            public void onAdDisplayed(InMobiInterstitial arg0) {
                ZplayDebug.d(TAG, "inmobi interstitial exposure", onoff);
                layerExposure();
                layerStartPlaying();
            }

            @Override
            public void onAdDismissed(InMobiInterstitial arg0) {
                ZplayDebug.d(TAG, "inmobi interstitial closed", onoff);
                layerClosed();
            }
        };
    }

}
