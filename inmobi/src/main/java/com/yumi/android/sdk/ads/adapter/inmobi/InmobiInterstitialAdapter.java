package com.yumi.android.sdk.ads.adapter.inmobi;

import java.util.Map;

import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiInterstitial;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerInterstitialAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import android.app.Activity;

import static com.yumi.android.sdk.ads.adapter.inmobi.InmobUtil.recodeError;
import static com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode.ERROR_OVER_RETRY_LIMIT;

public class InmobiInterstitialAdapter extends YumiCustomerInterstitialAdapter {

	private static final String TAG = "InmobiInterstitialAdapter";
	private InMobiInterstitial interstitial;
	private InMobiInterstitial.InterstitialAdListener interstitialListener;

	protected InmobiInterstitialAdapter(Activity activity,
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
		InmobiExtraHolder.onDestroy();
	}

	@Override
	public boolean onActivityBackPressed() {
		return false;
	}

	@Override
	protected void onPrepareInterstitial() {
		ZplayDebug.d(TAG, "inmobi request new interstitial", onoff);
		if (interstitial == null) {
			String key2 = getProvider().getKey2();
			long placementID = 0L;
			if (key2 != null && key2.length() > 0) {
				try {
					placementID = Long.valueOf(key2);
				} catch (NumberFormatException e) {
					ZplayDebug.e(TAG, "", e, onoff);
					layerPreparedFailed(recodeError(ERROR_OVER_RETRY_LIMIT));
					return ;
				}
			}else {
				layerPreparedFailed(recodeError(ERROR_OVER_RETRY_LIMIT));
				return;
			}
			interstitial = new InMobiInterstitial(getActivity(), placementID, interstitialListener);
		}
		interstitial.load();
	}

	@Override
	protected void onShowInterstitialLayer(Activity activity) {
		interstitial.show();
	}

	@Override
	protected boolean isInterstitialLayerReady() {
		if (interstitial != null
				&& interstitial.isReady()) {
			return true;
		}
		return false;
	}

	@Override
	protected void init() {
		ZplayDebug.i(TAG, "accounID : " + getProvider().getKey1(), onoff);
		ZplayDebug.i(TAG, "placementID : " + getProvider().getKey2(), onoff);
		InmobiExtraHolder.initInmobiSDK(getActivity(), getProvider().getKey1());
		interstitialListener = new InMobiInterstitial.InterstitialAdListener() {
			
			@Override
			public void onUserLeftApplication(InMobiInterstitial arg0) {
				ZplayDebug.d(TAG, "inmobi interstitial left application", onoff);
				layerClicked(-99f, -99f);
			}
			
			@Override
			public void onAdRewardActionCompleted(InMobiInterstitial arg0,
					Map<Object, Object> arg1) {
			}
			@Override
			public void onAdLoadSucceeded(InMobiInterstitial arg0) {
				ZplayDebug.d(TAG, "inmobi interstitial load successed", onoff);
				layerPrepared();
			}
			
			@Override
			public void onAdLoadFailed(InMobiInterstitial arg0,
					InMobiAdRequestStatus arg1) {
				ZplayDebug.d(TAG, "inmobi interstitial load failed " + arg1.getStatusCode(), onoff);
				layerPreparedFailed(recodeError(arg1));
			}
			
			@Override
			public void onAdInteraction(InMobiInterstitial arg0,
					Map<Object, Object> arg1) {
			}
			
			@Override
			public void onAdDisplayed(InMobiInterstitial arg0) {
				ZplayDebug.d(TAG, "inmobi interstitial exposure", onoff);
				layerExposure();
			}
			
			@Override
			public void onAdDismissed(InMobiInterstitial arg0) {
				ZplayDebug.d(TAG, "inmobi interstitial closed", onoff);
				layerClosed();
			}
		};
//		interstitialListener = new IMInterstitialListener() {
//
//			@Override
//			public void onShowInterstitialScreen(IMInterstitial arg0) {
//				ZplayDebug.d(TAG, "inmobi interstitial shown");
//				layerExposure();
//			}
//
//			@Override
//			public void onLeaveApplication(IMInterstitial arg0) {
//				ZplayDebug.d(TAG, "inmobi interstitial clicked");
//				layerClicked(-99f, -99f);
//			}
//
//			@Override
//			public void onInterstitialLoaded(IMInterstitial arg0) {
//				ZplayDebug.d(TAG, "inmobi interstitial prepared");
//				layerPrepared();
//			}
//
//			@Override
//			public void onInterstitialInteraction(IMInterstitial arg0,
//					Map<String, String> arg1) {
//
//			}
//
//			@Override
//			public void onInterstitialFailed(IMInterstitial arg0,
//					IMErrorCode arg1) {
//				ZplayDebug.d(TAG, "inmobi interstitial failed " + arg1);
//				layerPreparedFailed(InmobiExtraHolder.decodeErrorCode(arg1));
//			}
//
//			@Override
//			public void onDismissInterstitialScreen(IMInterstitial arg0) {
//				ZplayDebug.d(TAG, "inmobi interstitial closed");
//				layerClosed();
//			}
//		};
	}

}
