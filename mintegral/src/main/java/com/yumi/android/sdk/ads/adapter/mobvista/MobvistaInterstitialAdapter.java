package com.yumi.android.sdk.ads.adapter.mobvista;

import android.app.Activity;
import android.text.TextUtils;

import com.mintegral.msdk.MIntegralConstans;
import com.mintegral.msdk.MIntegralSDK;
import com.mintegral.msdk.interstitialvideo.out.InterstitialVideoListener;
import com.mintegral.msdk.interstitialvideo.out.MTGInterstitialVideoHandler;
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

import static com.yumi.android.sdk.ads.adapter.mobvista.Util.sdkVersion;
import static com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode.ERROR_FAILED_TO_SHOW;

public class MobvistaInterstitialAdapter extends YumiCustomerInterstitialAdapter {
    private static final String TAG = "MobvistaInterstitialAdapter";
    private static final String INTERSTITIAL_IMAGE = "2";
    private MTGInterstitialHandler mInterstitialHandler;
    private MTGInterstitialVideoHandler mInterstitialVideoHandler;
    private boolean isReady = false;

    protected MobvistaInterstitialAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }

    @Override
    protected void onPrepareInterstitial() {
        ZplayDebug.d(TAG, "Mobvista request new intertitial", onoff);

        if (TextUtils.equals(INTERSTITIAL_IMAGE, getProvider().getExtraData("inventory").trim())) {
            initInterstitialHandler();
            if (mInterstitialHandler != null) {
                isReady = false;
                mInterstitialHandler.preload();
            }
        } else {
            initInterstitialVideoHandler();
            if (mInterstitialVideoHandler != null) {
                mInterstitialVideoHandler.load();
            }
        }
    }

    @Override
    protected void onShowInterstitialLayer(Activity activity) {
        if (TextUtils.equals(INTERSTITIAL_IMAGE, getProvider().getExtraData("inventory").trim())) {
            if (mInterstitialHandler != null) {
                mInterstitialHandler.show();
            }
        } else {
            if (mInterstitialVideoHandler != null) {
                mInterstitialVideoHandler.show();
            }
        }
    }

    @Override
    protected boolean isInterstitialLayerReady() {
        if (TextUtils.equals(INTERSTITIAL_IMAGE, getProvider().getExtraData("inventory").trim())) {
            return isReady;
        } else {
            if (mInterstitialVideoHandler != null) {
                return mInterstitialVideoHandler.isReady();
            }
        }
        return false;
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
        } catch (Exception e) {
            ZplayDebug.e(TAG, "Mobvista intertitial init error:", e, onoff);
        }
    }

    private void initInterstitialHandler() {
        if (mInterstitialHandler != null) {
            return;
        }

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

    private void initInterstitialVideoHandler() {
        if (mInterstitialVideoHandler != null) {
            return;
        }

        mInterstitialVideoHandler = new MTGInterstitialVideoHandler(getContext(), getProvider().getKey3());
        mInterstitialVideoHandler.setInterstitialVideoListener(new InterstitialVideoListener() {
            @Override
            public void onLoadSuccess(String s) {
                ZplayDebug.d(TAG, "Mobvista Interstitial onLoadSuccess", onoff);
            }

            @Override
            public void onVideoLoadSuccess(String s) {
                ZplayDebug.d(TAG, "Mobvista Interstitial onVideoLoadSuccess", onoff);
                layerPrepared();
            }

            @Override
            public void onVideoLoadFail(String errorMsg) {
                ZplayDebug.d(TAG, "Mobvista Interstitial onVideoLoadFail errorMsg: " + errorMsg, onoff);
                AdError error = new AdError(LayerErrorCode.ERROR_NO_FILL);
                error.setErrorMessage("minteral errorMsg: " + errorMsg);
                layerPreparedFailed(error);
            }

            @Override
            public void onAdShow() {
                ZplayDebug.d(TAG, "Mobvista Interstitial video onAdShow", onoff);
                layerStartPlaying();
                layerExposure();
            }

            @Override
            public void onAdClose(boolean b) {
                ZplayDebug.d(TAG, "Mobvista Interstitial video onAdClose", onoff);
                layerClosed();
            }

            @Override
            public void onShowFail(String errorMsg) {
                ZplayDebug.d(TAG, "Mobvista Interstitial video onShowFail", onoff);
                AdError adError = new AdError(ERROR_FAILED_TO_SHOW);
                adError.setErrorMessage("Mobvista errorMsg: " + errorMsg);
                layerExposureFailed(adError);
            }

            @Override
            public void onVideoAdClicked(String s) {
                ZplayDebug.d(TAG, "Mobvista Interstitial onVideoAdClicked", onoff);
                layerClicked(-999f, -999f);
            }

            @Override
            public void onVideoComplete(String s) {
                ZplayDebug.d(TAG, "Mobvista Interstitial onVideoComplete", onoff);
            }

            @Override
            public void onEndcardShow(String s) {
                ZplayDebug.d(TAG, "Mobvista Interstitial video onEndcardShow", onoff);
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

    @Override
    public String getProviderVersion() {
        return sdkVersion();
    }
}
