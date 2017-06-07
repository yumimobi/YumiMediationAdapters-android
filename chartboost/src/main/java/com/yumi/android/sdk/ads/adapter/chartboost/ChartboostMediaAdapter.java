package com.yumi.android.sdk.ads.adapter.chartboost;

import com.chartboost.sdk.CBLocation;
import com.chartboost.sdk.Chartboost;
import com.chartboost.sdk.ChartboostDelegate;
import com.chartboost.sdk.Model.CBError.CBImpressionError;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerMediaAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import android.app.Activity;

public class ChartboostMediaAdapter extends YumiCustomerMediaAdapter {

	private static final String TAG = "ChartboostMediaAdapter";
	private ChartboostDelegate delegate;

	protected ChartboostMediaAdapter(Activity activity,
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
	protected void onPrepareMedia() {
		ZplayDebug.d(TAG, "chartboost request new media", onoff);
		Chartboost.cacheRewardedVideo(CBLocation.LOCATION_ACHIEVEMENTS);
	}

	@Override
	protected void onShowMedia() {
		Chartboost.showRewardedVideo(CBLocation.LOCATION_ACHIEVEMENTS);
	}

	@Override
	protected boolean isMediaReady() {
		if (Chartboost.hasRewardedVideo(CBLocation.LOCATION_ACHIEVEMENTS)) {
			return true;
		}
		return false;
	}

	@Override
	protected void init() {
		ZplayDebug.i(TAG, "appId : " + getProvider().getKey1(), onoff);
		ZplayDebug.i(TAG, "appSignature : " + getProvider().getKey2(), onoff);
		createDelegate();
		ChartboostExtra.getChartboostExtra().setMediaListener(delegate);
		ChartboostExtra.getChartboostExtra().initChartboostSDK(getActivity(),
				getProvider().getKey1(), getProvider().getKey2());
	}

	private void createDelegate() {
		if (delegate == null) {
			delegate = new ChartboostDelegate() {
				@Override
				public void didCacheRewardedVideo(String location) {
					ZplayDebug.d(TAG, "chartboost media prepared", onoff);
					layerPrepared();
				}

				@Override
				public void didFailToLoadRewardedVideo(String location,
						CBImpressionError error) {
					ZplayDebug.d(TAG, "chartboost media failed " + error, onoff);
					layerPreparedFailed(decodeError(error));
					super.didFailToLoadRewardedVideo(location, error);
				}

				@Override
				public void didCloseRewardedVideo(String location) {
					ZplayDebug.d(TAG, "chartboost media closed", onoff);
	                layerMediaEnd();
					layerClosed(); 
					super.didCloseRewardedVideo(location);
				}

				@Override
				public void didClickRewardedVideo(String location) {
					ZplayDebug.d(TAG, "chartboost media clicked", onoff);
					layerClicked();
					super.didClickRewardedVideo(location);
				}

				@Override
				public void didDismissRewardedVideo(String location) {
					ZplayDebug.d(TAG, "chartboost media shown", onoff);
					layerExposure();
					layerMediaStart();
					super.didDismissRewardedVideo(location);
				}

				@Override
				public void didCompleteRewardedVideo(String location, int reward) {
					ZplayDebug.d(TAG, "chartboost media get rewarded", onoff);
					layerIncentived();
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
