package com.yumi.android.sdk.ads.adapter.vungle;

import com.vungle.publisher.EventListener;
import com.vungle.publisher.VunglePub;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerMediaAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import android.app.Activity;

public class VungleMediaAdapter extends YumiCustomerMediaAdapter {

	private static final String TAG = "VungleMediaAdapter";

	private final VunglePub vungle = VunglePub.getInstance();
	private EventListener eventListener;

	protected VungleMediaAdapter(Activity activity, YumiProviderBean provider) {
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
	protected final void callOnActivityDestroy() {
		VungleExtra.getExtra().onDestroy();
	}

	@Override
	protected void onPrepareMedia() {
		ZplayDebug.d(TAG, "vungle request new media", onoff);
		if (vungle.isAdPlayable()) {
			ZplayDebug.d(TAG, "vungle media prapared", onoff);
			layerPrepared();
		}
	}

	@Override
	protected void onShowMedia() {
		vungle.playAd();
	}

	@Override
	protected boolean isMediaReady() {
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

	private void createVungleListener() {
		eventListener = new EventListener() {

			@Override
			public void onVideoView(boolean isCompletedView, int watchedMillis, int videoDurationMillis) {
			    // 此方法已弃用，将被删除。请勿使用。
		        // 请使用 onAdEnd。
			}

			@Override
			public void onAdUnavailable(String arg0) {
				ZplayDebug.d(TAG, "vungle media failed " + arg0, onoff);
				getActivity().runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						layerPreparedFailed(LayerErrorCode.ERROR_INTERNAL);
					}
				});
			}

			@Override
			public void onAdStart() {
				ZplayDebug.d(TAG, "vungle media shown", onoff);
				layerExposure();
                layerMediaStart();
			}

			@Override
			public void onAdPlayableChanged(boolean arg0) {
				ZplayDebug.d(TAG, "vungle media prepared " + arg0, onoff);
				layerPrepared();
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
					ZplayDebug.d(TAG, "vungle media clicked", onoff);
					layerClicked();
				}
				if(wasSuccessfulView)
				{
				    ZplayDebug.d(TAG, "vungle media get reward", onoff);
                    layerIncentived();
				}
				ZplayDebug.d(TAG, "vungle media closed", onoff);
                layerMediaEnd();
				layerClosed();
				
			}
		};
	}

	private void initVungleSDK() {
		vungle.init(getContext(), getProvider().getKey1());
		vungle.getGlobalAdConfig().setIncentivized(true);
		vungle.setEventListeners(eventListener);
	}

	@Override
	protected void onRequestNonResponse() {
		super.onRequestNonResponse();
		if (!vungle.isAdPlayable()) {
			vungle.playAd();
		} 
	}

}