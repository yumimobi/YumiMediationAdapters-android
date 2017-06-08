package com.yumi.android.sdk.ads.adapter.vungle;

import android.app.Activity;

import com.vungle.publisher.EventListener;
import com.vungle.publisher.VunglePub;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerInterstitialAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

public class VungleInterstitialAdapter extends YumiCustomerInterstitialAdapter {

	private static final String TAG = "VungleInterstitialAdapter";

	private final VunglePub vungle = VunglePub.getInstance();
	private EventListener eventListener;
	
	private boolean isPrepared=false;
	
	protected VungleInterstitialAdapter(Activity activity, YumiProviderBean provider) {
		super(activity, provider);
	}

    @Override
    public void onActivityPause() {
        VunglePub.getInstance().onPause();
    }

    @Override
    public void onActivityResume() {
        VunglePub.getInstance().onResume();
    }

    @Override
    public boolean onActivityBackPressed() {
        return false;
    }

    @Override
    protected void onPrepareInterstitial() {
        ZplayDebug.d(TAG, "vungle request new Interstitial", onoff);
        if (vungle.isAdPlayable()) {
            ZplayDebug.d(TAG, "vungle Interstitial prapared", onoff);
                layerPrepared();
                isPrepared=true;
        }else{
            isPrepared=false;
        } 
    }

    @Override
    protected void onShowInterstitialLayer(Activity activity) {
        ZplayDebug.d(TAG, "vungle onShowInterstitialLayer", onoff);
        vungle.playAd();
    }

    @Override
    protected boolean isInterstitialLayerReady() {
        if (vungle != null && vungle.isAdPlayable()) {
            return true;
        }
        return false;
    }

    @Override
    protected void init() {
        ZplayDebug.i(TAG, "appId : " + getProvider().getKey1(), onoff);
        createVungleListener();
        initVungleSDK();
    }

    @Override
    protected void callOnActivityDestroy() {
        VungleExtra.getExtra().onDestroy();
    }
    
    
    private void createVungleListener() {
        eventListener = new EventListener() {

            @Override
            public void onVideoView(boolean isCompletedView, int watchedMillis, int videoDurationMillis) {
                // 此方法已弃用，将被删除。请勿使用。
                // 请使用 onAdEnd。
            }

            @Override
            public void onAdUnavailable(String arg0) {
                ZplayDebug.d(TAG, "vungle Interstitial failed " + arg0, onoff);
                getActivity().runOnUiThread(new Runnable() {
                    
                    @Override
                    public void run() {
                        layerPreparedFailed(LayerErrorCode.ERROR_INTERNAL);
                    }
                });
            }

            @Override
            public void onAdStart() {
                ZplayDebug.d(TAG, "vungle Interstitial shown", onoff);
                layerExposure();
            }

            @Override
            public void onAdPlayableChanged(boolean arg0) {
                ZplayDebug.d(TAG, "vungle Interstitial prepared " + arg0, onoff);
                if (arg0 && !isPrepared) {
                    layerPrepared();
                }
            }

            @Override
            public void onAdEnd(boolean wasSuccessfulView , boolean wasCallToActionClicked) {
                // 当用户离开广告，控制转回至您的应用程序时调用
                // 如果 wasSuccessfulView 为 true，表示用户观看了广告，应获得奖励
                //（如果是奖励广告）。
                // 如果 wasCallToActionClicked 为 true，表示用户点击了广告中的
                // 行动号召按钮。
                ZplayDebug.d(TAG, "vungle media onAdEnd  wasSuccessfulView="+wasSuccessfulView+" || wasCallToActionClicked="+wasCallToActionClicked, onoff);
                if (wasCallToActionClicked) {
                    ZplayDebug.d(TAG, "vungle Interstitial clicked", onoff);
                    layerClicked(-99f, -99f);
                }
                ZplayDebug.d(TAG, "vungle Interstitial closed", onoff);
                layerClosed();
                layerMediaEnd();
                
            }
        };
    }

	
    private void initVungleSDK() {
        vungle.init(getContext(), getProvider().getKey1());
//      插页视频就是没有奖励回调的视频
//      vungle.getGlobalAdConfig().setIncentivized(true);
        vungle.setEventListeners(eventListener);
    }
}