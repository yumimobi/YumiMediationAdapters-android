package com.yumi.android.sdk.ads.adapter.startapp;


import com.startapp.android.publish.adsCommon.Ad;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.StartAppSDK;
import com.startapp.android.publish.adsCommon.adListeners.AdDisplayListener;
import com.startapp.android.publish.adsCommon.adListeners.AdEventListener;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerInterstitialAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import android.app.Activity;

public final class StartappInterstitialAdapter extends
		YumiCustomerInterstitialAdapter {
	
	private static final String TAG = "StartappInterstitialAdapter";
	private StartAppAd interstitial = null;
	private StartAppInterstitalListener interstitialListener = null;
	
	protected StartappInterstitialAdapter(Activity activity,
			YumiProviderBean provider) {
		super(activity, provider);
	}

	@Override
	public void onActivityPause() {
		if (interstitial != null) {
			interstitial.onPause();
		}
	}

	@Override
	public void onActivityResume() {
		if (interstitial != null) {
			interstitial.onResume();
		}
	}

	@Override
	public boolean onActivityBackPressed() {
		return false;
	}

	@Override
	protected void onPrepareInterstitial() {
		ZplayDebug.d(TAG, "startapp request new interstitial", onoff);
		if (interstitial != null) {
			interstitial.loadAd(interstitialListener);
		}
	}

	@Override
	protected void onShowInterstitialLayer(Activity activity) {
		if (interstitial != null) {
			interstitial.showAd(interstitialListener);
		}
	}

	@Override
	protected boolean isInterstitialLayerReady() {
		if (interstitial != null) {
			return interstitial.isReady();
		}
		return false;
	}

	@Override
	protected void init() {
		ZplayDebug.i(TAG, "interstitial appID : " + getProvider().getKey1(), onoff);
		StartAppSDK.init(getActivity(), getProvider().getKey1(),false);
		if (interstitial == null) {
			interstitial = new StartAppAd(getActivity());
			interstitialListener = new StartAppInterstitalListener();
		}
	}

	@Override
	protected void callOnActivityDestroy() {

	}

	private class StartAppInterstitalListener implements AdEventListener, AdDisplayListener{

		@Override
		public void adClicked(Ad arg0) {
			layerClicked(-99f, -99f);
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
			ZplayDebug.d(TAG, "startapp interstitial prepared failed " + arg0.getErrorMessage(), onoff);
			layerPreparedFailed(LayerErrorCode.ERROR_INTERNAL);
		}

		@Override
		public void onReceiveAd(Ad arg0) {
			ZplayDebug.d(TAG, "startapp interstitial prepared", onoff);
			layerPrepared();
		}
		
	}
	
}
