package com.yumi.android.sdk.ads.adapter.applovin;

import java.util.Map;

import com.applovin.adview.AppLovinIncentivizedInterstitial;
import com.applovin.sdk.AppLovinAd;
import com.applovin.sdk.AppLovinAdClickListener;
import com.applovin.sdk.AppLovinAdDisplayListener;
import com.applovin.sdk.AppLovinAdLoadListener;
import com.applovin.sdk.AppLovinAdRewardListener;
import com.applovin.sdk.AppLovinAdUpdateListener;
import com.applovin.sdk.AppLovinAdVideoPlaybackListener;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerMediaAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import android.app.Activity;

public class ApplovinMediaAdapter extends YumiCustomerMediaAdapter {

	private static final String TAG = "ApplovinMediaAdapter";
	private AppLovinIncentivizedInterstitial media;
	private ApplovinMediaListener mediaListener;
	private boolean isFirstClick = false;

	protected ApplovinMediaAdapter(Activity activity, YumiProviderBean provider) {
		super(activity, provider);
	}

	@Override
	public void onActivityPause() {

	}

	@Override
	public void onActivityResume() {

	}

	@Override
	protected void onPrepareMedia() {
		ZplayDebug.d(TAG, "applovin request new media", onoff);
		isFirstClick = false;
		if (media != null) {
			media.preload(mediaListener);
		}
	}

	@Override
	protected void onShowMedia() {
		if (media != null) {
			media.show(getActivity(), mediaListener, mediaListener, mediaListener, mediaListener);
		}
	}

	@Override
	protected boolean isMediaReady() {
		if (media != null && media.isAdReadyToDisplay()) {
			return true;
		}
		return false;
	}

	@Override
	protected void init() {
		ZplayDebug.i(TAG, "sdkKey : " + getProvider().getKey1(), onoff);
		createMediaListener();
		ApplovinExtraHolder.initApplovinSDK(getActivity(), getProvider().getKey1());
		media = AppLovinIncentivizedInterstitial.create(ApplovinExtraHolder.getAppLovinSDK());
	}

	private void createMediaListener() {
		mediaListener = new ApplovinMediaListener() {
			
			@Override
			public void validationRequestFailed(AppLovinAd arg0, int arg1) {
				ZplayDebug.d(null, "request failed", onoff);
			}
			
			@Override
			public void userRewardVerified(AppLovinAd arg0, @SuppressWarnings("rawtypes") Map arg1) {
				ZplayDebug.d(TAG, "reward verified", onoff);
				layerIncentived();
			}
			
			@Override
			public void userRewardRejected(AppLovinAd arg0, @SuppressWarnings("rawtypes") Map arg1) {
				ZplayDebug.d(TAG, "reward rejected", onoff);
			}
			
			@Override
			public void userOverQuota(AppLovinAd arg0, @SuppressWarnings("rawtypes") Map arg1) {
				ZplayDebug.d(TAG, "over quota", onoff);
			}
			
			@Override
			public void userDeclinedToViewAd(AppLovinAd arg0) {
				ZplayDebug.d(TAG, "declined to view ad", onoff);
			}
			
			@Override
			public void adUpdated(AppLovinAd arg0) {
				ZplayDebug.d(TAG, "ad update", onoff);
			}
			
			@Override
			public void videoPlaybackEnded(AppLovinAd arg0, double arg1, boolean arg2) {
				ZplayDebug.d(TAG, "end", onoff);		
				layerMediaEnd();
			}
			
			@Override
			public void videoPlaybackBegan(AppLovinAd arg0) {
				ZplayDebug.d(TAG, "began", onoff);
				layerMediaStart();
			}
			
			@Override
			public void adHidden(AppLovinAd arg0) {
				ZplayDebug.d(TAG, "hidden", onoff);
				layerClosed();
				if (media != null) {
		            media.preload(mediaListener);
		        }
			}
			
			@Override
			public void adDisplayed(AppLovinAd arg0) {
				ZplayDebug.d(TAG, "displayed", onoff);
				layerExposure();
			}
			
			@Override
			public void failedToReceiveAd(int arg0) {
				ZplayDebug.d(TAG, "failed received ad " + arg0, onoff);
				if (arg0 == 204) {
					layerPreparedFailed(LayerErrorCode.ERROR_NO_FILL);
				}else {
					layerPreparedFailed(LayerErrorCode.ERROR_INTERNAL);
				}
			}
			
			@Override
			public void adReceived(AppLovinAd arg0) {
				ZplayDebug.d(TAG, "ad received", onoff);
				layerPrepared();
			}
			
			@Override
			public void adClicked(AppLovinAd arg0) {
				if (!isFirstClick) {
					ZplayDebug.d(TAG, "clicked" + arg0.getAdIdNumber(), onoff);		
					layerClicked();
					isFirstClick = true;
				}
			}
		};
	}

	@Override
	protected void callOnActivityDestroy() {

	}

	private abstract class ApplovinMediaListener implements
			AppLovinAdClickListener, AppLovinAdLoadListener,
			AppLovinAdDisplayListener, AppLovinAdVideoPlaybackListener,
			AppLovinAdUpdateListener, AppLovinAdRewardListener {

	}

}
