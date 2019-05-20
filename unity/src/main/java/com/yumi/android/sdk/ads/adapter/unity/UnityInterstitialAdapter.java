package com.yumi.android.sdk.ads.adapter.unity;

import android.app.Activity;

import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAds.FinishState;
import com.unity3d.ads.UnityAds.UnityAdsError;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerInterstitialAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.yumi.android.sdk.ads.adapter.unity.UnityUtil.generateLayerErrorCode;
import static com.yumi.android.sdk.ads.adapter.unity.UnityUtil.updateGDPRStatus;

public class UnityInterstitialAdapter extends YumiCustomerInterstitialAdapter {


    private static final String TAG = "UnityInterstitialAdapter";
    private IMyUnityAdsListener unityAdsListener;
    
    private static final boolean isDebugMode=false; //测试模式 正式发部需要该成false

    private boolean isPrepared=false;
    
    protected UnityInterstitialAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onActivityPause() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onActivityResume() {
        ZplayDebug.d(TAG, "unity Interstitial changeActivity", onoff);
        UnityListenerFactory.setMyCpUnityAdsListener(unityAdsListener);
        UnityAds.setListener(UnityListenerFactory.getUnityAdsListenerInstance());
    }

    @Override
    public boolean onActivityBackPressed() {
        return false;
    }

    @Override
    protected void onPrepareInterstitial() {
        ZplayDebug.d(TAG, "unity Interstitial request new", onoff);
        updateGDPRStatus(getContext());
        UnityAds.setDebugMode(isDebugMode); //测试
        if (UnityAds.isReady(getProvider().getKey2())) {
            callLayerPrepared();
        } else {
            isPrepared = false;
        }   
    }

    @Override
    protected void onShowInterstitialLayer(Activity activity) {
//        UnityAds.show(getActivity(),"123"); //TODO 测试
        UnityAds.show(getActivity(), getProvider().getKey2());
    }

    @Override
    protected boolean isInterstitialLayerReady() {
        return UnityAds.isReady(getProvider().getKey2());
    }

    @Override
    protected void init() {
        try {
            ZplayDebug.i(TAG, "gameid : " + getProvider().getKey1(), onoff);
            if (unityAdsListener == null) {
                unityAdsListener = new IMyUnityAdsListener() {

                    @Override
                    public void onUnityAdsError(UnityAdsError error, String message) {
                        ZplayDebug.d(TAG, "unity Interstitial prepared failed UnityAdsError:" + error + " || message:" + message, onoff);

                        layerPreparedFailed(generateLayerErrorCode(error, message));
                    }

                    @Override
                    public void onUnityAdsFinish(String zoneId, FinishState finishState) {
                        ZplayDebug.d(TAG, "unity Interstitial onUnityAdsFinish zoneId:" + zoneId + "  finishState:" + finishState, onoff);
                        if (getProvider().getKey2().equals(zoneId)) {
                            ZplayDebug.d(TAG, "unity Interstitial onUnityAdsFinish layerClosed layerMediaEnd", onoff);
                            layerClosed();
                        }
                    }

                    @Override
                    public void onUnityAdsReady(String zoneId) {
                        ZplayDebug.d(TAG, "unity Interstitial onUnityAdsReady isPrepared=" + isPrepared + "   zoneId=zoneI" + zoneId, onoff);
                        if (!isPrepared && getProvider().getKey2().equals(zoneId)) {
                            ZplayDebug.d(TAG, "unity Interstitial onUnityAdsReady callLayerPrepared", onoff);
                            callLayerPrepared();
                        }
                    }

                    @Override
                    public void onUnityAdsStart(String zoneId) {
                        ZplayDebug.d(TAG, "unity Interstitial onUnityAdsStart zoneId:" + zoneId, onoff);
                        if (getProvider().getKey2().equals(zoneId)) {
                            ZplayDebug.d(TAG, "unity Interstitial onUnityAdsStart layerExposure", onoff);
                            layerExposure();
                            layerStartPlaying();
                        }
                    }

                };
                UnityListenerFactory.setMyCpUnityAdsListener(unityAdsListener);
                UnityAds.initialize(getActivity(), getProvider().getKey1(), UnityListenerFactory.getUnityAdsListenerInstance(), isDebugMode);
            }
        }catch (Exception e)
        {
            ZplayDebug.e(TAG, "unity Interstitial init error ",e, onoff);
        }
    }
    
    private synchronized void callLayerPrepared()
    {
        isPrepared = true;
        layerPrepared();
    }

    @Override
    protected void callOnActivityDestroy() {
        // TODO Auto-generated method stub
        
    }

}
