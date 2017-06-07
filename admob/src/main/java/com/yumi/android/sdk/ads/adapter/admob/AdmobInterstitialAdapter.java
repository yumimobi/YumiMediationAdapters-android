package com.yumi.android.sdk.ads.adapter.admob;

import android.app.Activity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerInterstitialAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

public class AdmobInterstitialAdapter extends YumiCustomerInterstitialAdapter {

	private static final String TAG = "AdmobInterstitialAdapter";
	private InterstitialAd instertitial;
	private AdListener adListener;

	protected AdmobInterstitialAdapter(Activity activity,
			YumiProviderBean provider) {
		super(activity, provider);
	}

	@Override
	public void onActivityPause() {

	}

	@Override
	public void onActivityResume() {

	}

	@Override
	protected final void callOnActivityDestroy() {

	}

	@Override
	public boolean onActivityBackPressed() {
		return false;
	}

	@Override
	protected void onPrepareInterstitial() {
		ZplayDebug.d(TAG, "admob request new interstitial", onoff);
		if (instertitial == null) {
			instertitial = new InterstitialAd(getActivity());
			instertitial.setAdUnitId(getProvider().getKey1());
			instertitial.setAdListener(adListener);
		}
		AdRequest req = new AdRequest.Builder().build();
		instertitial.loadAd(req);
	}

	@Override
	protected void onShowInterstitialLayer(Activity activity) {
		instertitial.show();
	}

	@Override
	protected boolean isInterstitialLayerReady() {
		if (instertitial != null && instertitial.isLoaded()) {
			return true;
		}
		return false;
	}

	@Override
	protected void init() {
		ZplayDebug.i(TAG, "unitId : " + getProvider().getKey1(), onoff);
		createAdListener();
	}

	private void createAdListener() {
		adListener = new AdListener() {
			@Override
			public void onAdClosed() {
				ZplayDebug.d(TAG, "admob interstitial closed", onoff);
				layerClosed();
				super.onAdClosed();
			}

			@Override
			public void onAdOpened() {
				ZplayDebug.d(TAG, "admob interstitial shown", onoff);
				layerExposure();
				super.onAdOpened();
			}

			@Override
			public void onAdLeftApplication() {
				ZplayDebug.d(TAG, "admob interstitial clicked", onoff);
				layerClicked(-99f, -99f);
				super.onAdLeftApplication();
			}

			@Override
			public void onAdLoaded() {
				ZplayDebug.d(TAG, "admob interstitial prepared", onoff);
				layerPrepared();
				super.onAdLoaded();
			}

			@Override
			public void onAdFailedToLoad(int errorCode) {
				ZplayDebug.d(TAG, "admob interstitial failed " + errorCode, onoff);
				layerPreparedFailed(decodeErrorCode(errorCode));
				super.onAdFailedToLoad(errorCode);
			}
		};
	}

	private LayerErrorCode decodeErrorCode(int errorCode) {
		switch (errorCode) {
		case AdRequest.ERROR_CODE_INTERNAL_ERROR:
			return LayerErrorCode.ERROR_INTERNAL;
		case AdRequest.ERROR_CODE_INVALID_REQUEST:
			return LayerErrorCode.ERROR_INVALID;
		case AdRequest.ERROR_CODE_NO_FILL:
			return LayerErrorCode.ERROR_NO_FILL;
		case AdRequest.ERROR_CODE_NETWORK_ERROR:
			return LayerErrorCode.ERROR_NETWORK_ERROR;
		default:
			return LayerErrorCode.ERROR_INTERNAL;
		}
	}

}
