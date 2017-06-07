package com.yumi.android.sdk.ads.adapter.chartboost;

import com.chartboost.sdk.CBLocation;
import com.chartboost.sdk.Chartboost;
import com.chartboost.sdk.ChartboostDelegate;
import com.chartboost.sdk.Model.CBError.CBImpressionError;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerInterstitialAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import android.app.Activity;

public class ChartboostInterstitialAdapter extends
		YumiCustomerInterstitialAdapter {

	private static final String TAG = "ChartboostInterstitialAdapter";
	private ChartboostDelegate delegate;

	protected ChartboostInterstitialAdapter(Activity activity,
			YumiProviderBean provider) {
		super(activity, provider);
	}

	@Override
	public void onActivityPause() {
		Chartboost.onPause(getActivity());
		Chartboost.onStop(getActivity());
	}

	@Override
	public void onActivityResume() {
		Chartboost.onStart(getActivity());
		Chartboost.onResume(getActivity());
	}

	@Override
	protected final void callOnActivityDestroy() {
		ChartboostExtra.getChartboostExtra().onDestroy();
		Chartboost.onDestroy(getActivity());
	}

	@Override
	public boolean onActivityBackPressed() {
		if (Chartboost.onBackPressed()) {
			return true;
		}
		return false;
	}

	@Override
	protected void onPrepareInterstitial() {
		ZplayDebug.d(TAG, "chartboost request new interstitial", onoff);
		Chartboost.cacheInterstitial(CBLocation.LOCATION_LEADERBOARD);
	}

	@Override
	protected void onShowInterstitialLayer(Activity activity) {
		Chartboost.showInterstitial(CBLocation.LOCATION_LEADERBOARD);
	}

	@Override
	protected boolean isInterstitialLayerReady() {
		if (Chartboost.hasInterstitial(CBLocation.LOCATION_LEADERBOARD)) {
			return true;
		}
		return false;
	}

	@Override
	protected void init() {
		ZplayDebug.i(TAG, "appId : " + getProvider().getKey1(), onoff);
		ZplayDebug.i(TAG, "appSignature : " + getProvider().getKey2(), onoff);
		createDelegate();
		ChartboostExtra.getChartboostExtra().setInstertitialListener(delegate);
		ChartboostExtra.getChartboostExtra().initChartboostSDK(getActivity(),
				getProvider().getKey1(), getProvider().getKey2());
	}

	private void createDelegate() {
		if (delegate == null) {
			delegate = new ChartboostDelegate() {
				@Override
				public void didCacheInterstitial(String location) {
					ZplayDebug.d(TAG, "chartboost interstitial prepared", onoff);
					layerPrepared();
					super.didCacheInterstitial(location);
				}

				@Override
				public void didFailToLoadInterstitial(String location,
						CBImpressionError error) {
					ZplayDebug.d(TAG, "chartboost interstitial failed " + error, onoff);
					layerPreparedFailed(decodeError(error));
					super.didFailToLoadInterstitial(location, error);
				}

				@Override
				public void didCloseInterstitial(String location) {
					ZplayDebug.d(TAG, "chartboost interstitial closed", onoff);
					layerClosed();
					super.didCloseInterstitial(location);
				}

				@Override
				public void didClickInterstitial(String location) {
					ZplayDebug.d(TAG, "chartboost interstitial clicked", onoff);
					layerClicked(-99f, -99f);
					super.didClickInterstitial(location);
				}

				@Override
				public void didDisplayInterstitial(String location) {
					ZplayDebug.d(TAG, "chartboost interstitial shown", onoff);
					layerExposure();
					super.didDisplayInterstitial(location);
				}
			};
		}
	}

	protected LayerErrorCode decodeError(CBImpressionError error) {
		if (error.equals(CBImpressionError.INTERNAL)) {
			return LayerErrorCode.ERROR_INTERNAL;
		}
		if (error.equals(CBImpressionError.NO_AD_FOUND)) {
			return LayerErrorCode.ERROR_NO_FILL;
		}
		if (error.equals(CBImpressionError.INVALID_LOCATION)) {
			return LayerErrorCode.ERROR_INVALID;
		}
		if (error.equals(CBImpressionError.NETWORK_FAILURE)) {
			return LayerErrorCode.ERROR_NETWORK_ERROR;
		}
		return LayerErrorCode.ERROR_INTERNAL;
	}

}
