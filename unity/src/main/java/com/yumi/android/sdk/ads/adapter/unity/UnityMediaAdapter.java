package com.yumi.android.sdk.ads.adapter.unity;
import android.app.Activity;

import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAds.FinishState;
import com.unity3d.ads.UnityAds.UnityAdsError;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerMediaAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.ZplayDebug;


public class UnityMediaAdapter extends YumiCustomerMediaAdapter {

	private static final String TAG = "UnityMediaAdapter";
	private IUnityAdsListener unityAdsListener;
	
	private static final boolean isDebugMode=false; //测试模式 正式发部需要该成false
	
	protected UnityMediaAdapter(Activity activity, YumiProviderBean provider) {
		super(activity, provider);
	}

	@Override
	public void onActivityPause() {
		
	}

	@Override
	public void onActivityResume() {
		ZplayDebug.d(TAG, "unity media changeActivity", onoff);
		
		UnityAds.setListener(unityAdsListener);
		
	}

	@Override
	protected void onPrepareMedia() {
		ZplayDebug.d(TAG, "unity media request new media", onoff);
		UnityAds.setDebugMode(isDebugMode); //测试
		
		if (UnityAds.isReady(getProvider().getKey2())) {
			layerPrepared();
			return;
		}
	}

	@Override
	protected void onShowMedia() {
//        UnityAds.show(getActivity(),"123"); //TODO 测试
      UnityAds.show(getActivity(), getProvider().getKey2());
	}

	@Override
	protected boolean isMediaReady() {
		return UnityAds.isReady(getProvider().getKey2());
	}

	@Override
	protected void init() {
		ZplayDebug.i(TAG, "gameid : "+ getProvider().getKey1(), onoff);
		if (unityAdsListener == null) {
			unityAdsListener = new IUnityAdsListener() {

                @Override
                public void onUnityAdsError(UnityAdsError arg0, String arg1) {
                    ZplayDebug.d(TAG, "unity media prepared failed UnityAdsError:"+arg0+" || arg1:"+arg1, onoff);
                    layerPreparedFailed(LayerErrorCode.ERROR_INTERNAL);
                }

                @Override
                public void onUnityAdsFinish(String arg0, FinishState arg1) {
                    ZplayDebug.d(TAG, "unity media onUnityAdsFinish", onoff);
                    layerIncentived();
                    layerMediaEnd();
                    layerClosed();
                }

                @Override
                public void onUnityAdsReady(String arg0) {
                    ZplayDebug.d(TAG, "unity media onUnityAdsReady ", onoff);
                    layerPrepared();
                }

                @Override
                public void onUnityAdsStart(String arg0) {
                    ZplayDebug.d(TAG, "unity media onUnityAdsStart", onoff);
                    layerExposure();
                    layerMediaStart();
                }
                
                
//                @Override
//                public void onVideoStarted() {
//                    ZplayDebug.d(TAG, "unity media started", onoff);
//                    layerMediaStart();
//                }
//                
//                @Override
//                public void onVideoCompleted(String rewardItemKey, boolean skipped) {
//                    ZplayDebug.d(TAG, "unity media completed and is skipped " + skipped, onoff);
//                    if (!skipped) {
//                        layerIncentived();
//                        layerMediaEnd();
//                    }
//                } 
//                
//                @Override
//                public void onShow() {
//                    ZplayDebug.d(TAG, "unity media on shown", onoff);
//                    layerExposure();
//                }
//                
//                @Override
//                public void onHide() {
//                    ZplayDebug.d(TAG, "unity media on closed", onoff);
//                    layerClosed();
//                }
//                
//                @Override
//                public void onFetchFailed() {
//                    ZplayDebug.d(TAG, "unity media prepared failed", onoff);
//                    layerPreparedFailed(LayerErrorCode.ERROR_INTERNAL);
//                }
//                
//                @Override
//                public void onFetchCompleted() {
//                    ZplayDebug.d(TAG, "unity media prepared ", onoff);13180539917
                
//                    layerPrepared();
//                }
			};
			UnityAds.initialize(getActivity(), getProvider().getKey1(), unityAdsListener,isDebugMode);
		}
	}

	@Override
	protected void callOnActivityDestroy() {
	}

}
