package com.yumi.android.sdk.ads.adapter.startapp;

import com.startapp.android.publish.adsCommon.Ad;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.StartAppAd.AdMode;
import com.startapp.android.publish.adsCommon.StartAppSDK;
import com.startapp.android.publish.adsCommon.VideoListener;
import com.startapp.android.publish.adsCommon.adListeners.AdDisplayListener;
import com.startapp.android.publish.adsCommon.adListeners.AdEventListener;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerMediaAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import android.app.Activity;

public final class StartappMediaAdapter extends YumiCustomerMediaAdapter {

	private static final String TAG = "StartappMediaAdapter";
	private StartAppAd media = null;
	private StartAppMediaListener mediaListener = null;
	
	protected StartappMediaAdapter(Activity activity, YumiProviderBean provider) {
		super(activity, provider);
	}

	@Override
	public void onActivityPause() {
		if (media != null) {
			media.onPause();
		}
	}

	@Override
	public void onActivityResume() {
		if (media != null) {
			media.onResume();
		}
	}

	@Override
	protected void onPrepareMedia() {
		ZplayDebug.d(TAG, "startapp request new media", onoff);
		if (media != null) {
			media.loadAd(AdMode.REWARDED_VIDEO, mediaListener);
			media.setVideoListener(mediaListener);
		}
	}

	@Override
	protected void onShowMedia() {
		if (media != null) {
			media.showAd(mediaListener);
		}
	}

	@Override
	protected boolean isMediaReady() {
		if (media != null) {
			return media.isReady();
		}
		return false;
	}

	@Override
	protected void init() {
		ZplayDebug.i(TAG, "interstitial appID : " + getProvider().getKey1(), onoff);
		StartAppSDK.init(getActivity(), getProvider().getKey1(),false);
		if (media == null) {
			media = new StartAppAd(getContext());
			mediaListener = new StartAppMediaListener();
		}
	}

	@Override
	protected void callOnActivityDestroy() {

	}

	private class StartAppMediaListener implements AdEventListener, AdDisplayListener, VideoListener{

		@Override
		public void adClicked(Ad arg0) {
			layerClicked();
		}

		@Override
		public void adDisplayed(Ad arg0) {
			layerExposure();
		}

		@Override
		public void adHidden(Ad arg0) {
			layerClosed();
		}

		@Override
		public void adNotDisplayed(Ad arg0) {
		}

		@Override
		public void onFailedToReceiveAd(Ad arg0) {
			ZplayDebug.d(TAG, "startapp meida prepared failed " + arg0.getErrorMessage(), onoff);
			layerPreparedFailed(LayerErrorCode.ERROR_INTERNAL);
		}

		@Override
		public void onReceiveAd(Ad arg0) {
			ZplayDebug.d(TAG, "startapp meida prepared", onoff);
			layerPrepared();
		}

		@Override
		public void onVideoCompleted() {
			ZplayDebug.d(TAG, "startapp media incentived", onoff);
			layerIncentived();
		}
		
	}
	
}
