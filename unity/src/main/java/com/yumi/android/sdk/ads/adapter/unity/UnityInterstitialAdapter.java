package com.yumi.android.sdk.ads.adapter.unity;

import android.app.Activity;

import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAds.FinishState;
import com.unity3d.ads.UnityAds.UnityAdsError;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerInterstitialAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

public class UnityInterstitialAdapter extends YumiCustomerInterstitialAdapter {


    private static final String TAG = "UnityInterstitialAdapter";
    private IUnityAdsListener unityAdsListener;
    
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
        
        UnityAds.setListener(unityAdsListener);
    }

    @Override
    public boolean onActivityBackPressed() {
        return false;
    }

    @Override
    protected void onPrepareInterstitial() {
        ZplayDebug.d(TAG, "unity Interstitial request new", onoff);
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
        ZplayDebug.i(TAG, "gameid : " + getProvider().getKey1(), onoff);
        if (unityAdsListener == null) {
            unityAdsListener = new IUnityAdsListener() {

                @Override
                public void onUnityAdsError(UnityAdsError arg0, String arg1) {
                    ZplayDebug.d(TAG, "unity Interstitial prepared failed UnityAdsError:"+arg0+" || arg1:"+arg1, onoff);
                    layerPreparedFailed(LayerErrorCode.ERROR_INTERNAL);
                }

                @Override
                public void onUnityAdsFinish(String arg0, FinishState arg1) {
                    ZplayDebug.d(TAG, "unity Interstitial onUnityAdsFinish", onoff);
                    layerClosed();
                    layerMediaEnd();
                }

                @Override
                public void onUnityAdsReady(String zoneId) {
                    ZplayDebug.d(TAG, "unity Interstitial onUnityAdsReady isPrepared="+isPrepared+"   zoneId=zoneI"+zoneId, onoff);
                    if (!isPrepared && getProvider().getKey2().equals(zoneId)) {
                        callLayerPrepared();
                    }
                }

                @Override
                public void onUnityAdsStart(String arg0) {
                    ZplayDebug.d(TAG, "unity Interstitial onUnityAdsStart", onoff);
                    layerExposure();
                }

            };
            UnityAds.initialize(getActivity(), getProvider().getKey1(), unityAdsListener, isDebugMode);
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
