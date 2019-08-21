package com.yumi.android.sdk.ads.adapter.mobvista;

import android.app.Activity;

import com.mintegral.msdk.MIntegralConstans;
import com.mintegral.msdk.MIntegralSDK;
import com.mintegral.msdk.out.InterstitialListener;
import com.mintegral.msdk.out.MIntegralSDKFactory;
import com.mintegral.msdk.out.MTGInterstitialHandler;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.AdError;
import com.yumi.android.sdk.ads.publish.YumiSettings;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerInterstitialAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.publish.enumbean.YumiGDPRStatus;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import java.util.HashMap;
import java.util.Map;

import static com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode.ERROR_FAILED_TO_SHOW;

public class MobvistaInterstitialAdapter extends YumiCustomerInterstitialAdapter {
    private static final String TAG = "MobvistaInterstitialAdapter";
    private MTGInterstitialHandler mInterstitialHandler;
    private boolean isReady = false;

    protected MobvistaInterstitialAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }

    @Override
    protected void onPrepareInterstitial() {
        if (mInterstitialHandler != null) {
            isReady = false;
            mInterstitialHandler.preload();
        }
    }

    @Override
    protected void onShowInterstitialLayer(Activity activity) {
        if (mInterstitialHandler != null) {
            mInterstitialHandler.show();
        }
    }

    @Override
    protected boolean isInterstitialLayerReady() {
        return isReady;
    }

    @Override
    protected void init() {
        try {
            ZplayDebug.d(TAG, "Mobvista intertitial init appId : " + getProvider().getKey1() + "   || appKey : " + getProvider().getKey2(), onoff);
            MIntegralSDK sdk = MIntegralSDKFactory.getMIntegralSDK();
            Map<String, String> map = sdk.getMTGConfigurationMap(getProvider().getKey1(), getProvider().getKey2()); //appId, appKey
            if (YumiSettings.getGDPRStatus() != YumiGDPRStatus.UNKNOWN) {
                int isConsent = YumiSettings.getGDPRStatus() == YumiGDPRStatus.PERSONALIZED ? MIntegralConstans.IS_SWITCH_ON : MIntegralConstans.IS_SWITCH_OFF;
                sdk.setUserPrivateInfoType(getActivity(), MIntegralConstans.AUTHORITY_ALL_INFO, isConsent);
            }
            sdk.init(map, getContext());
            initHandler();
        } catch (Exception e) {
            ZplayDebug.e(TAG, "Mobvista intertitial init error:", e, onoff);
        }
    }

    private void initHandler() {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put(MIntegralConstans.PROPERTIES_UNIT_ID, getProvider().getKey3());
        mInterstitialHandler = new MTGInterstitialHandler(getContext(), hashMap);
        mInterstitialHandler.setInterstitialListener(new InterstitialListener() {

            @Override
            public void onInterstitialLoadSuccess() {
                ZplayDebug.d(TAG, "Mobvista Interstitial onLoadSuccess", onoff);
                isReady = true;
                layerPrepared();
            }

            @Override
            public void onInterstitialLoadFail(String errorMsg) {
                ZplayDebug.d(TAG, "Mobvista Interstitial onLoadFail errorMsg:" + errorMsg, onoff);
                isReady = false;
                AdError error = new AdError(LayerErrorCode.ERROR_NO_FILL);
                error.setErrorMessage("minteral errorMsg: " + errorMsg);
                layerPreparedFailed(error);
            }

            @Override
            public void onInterstitialShowSuccess() {
                ZplayDebug.d(TAG, "Mobvista Interstitial onShowSuccess", onoff);
                isReady = false;
                layerStartPlaying();
                layerExposure();
            }

            @Override
            public void onInterstitialShowFail(String errorMsg) {
                ZplayDebug.d(TAG, "Mobvista Interstitial onShowFail errorMsg:" + errorMsg, onoff);
                AdError adError = new AdError(ERROR_FAILED_TO_SHOW);
                adError.setErrorMessage("Mobvista errorMsg: " + errorMsg);
                layerExposureFailed(adError);
            }

            @Override
            public void onInterstitialClosed() {
                ZplayDebug.d(TAG, "Mobvista Interstitial onClosed", onoff);
                layerClosed();
            }

            @Override
            public void onInterstitialAdClick() {
                ZplayDebug.d(TAG, "Mobvista Interstitial onClick", onoff);
                layerClicked(-999f, -999f);
            }
        });
    }


    @Override
    public void onActivityPause() {

    }

    @Override
    public void onActivityResume() {

    }

    @Override
    public boolean onActivityBackPressed() {
        return false;
    }
}
