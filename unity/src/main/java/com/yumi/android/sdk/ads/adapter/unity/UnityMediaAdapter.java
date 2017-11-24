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
				public void onUnityAdsError(UnityAdsError error, String message) {
					ZplayDebug.d(TAG, "unity media prepared failed UnityAdsError : "+error+" || message : "+message, onoff);
					layerPreparedFailed(LayerErrorCode.ERROR_INTERNAL);
				}

				@Override
				public void onUnityAdsFinish(String zoneId, FinishState result) {
					ZplayDebug.d(TAG, "unity media onUnityAdsFinish zoneId : "+zoneId+ "  FinishState : "+result, onoff);
					if(result==FinishState.COMPLETED){
						layerIncentived();
					}
					layerMediaEnd();
					layerClosed();
				}

				@Override
				public void onUnityAdsReady(String zoneId) {
					ZplayDebug.d(TAG, "unity media onUnityAdsReady zoneId : "+zoneId, onoff);
                    if(getProvider().getKey2().equals(zoneId)) {
                        layerPrepared();
                    }
				}

				@Override
				public void onUnityAdsStart(String zoneId) {
					ZplayDebug.d(TAG, "unity media onUnityAdsStart zoneId : "+zoneId, onoff);
					layerExposure();
					layerMediaStart();
				}
			};
			UnityAds.initialize(getActivity(), getProvider().getKey1(), unityAdsListener,isDebugMode);
		}
	}

	@Override
	protected void callOnActivityDestroy() {
	}

}
