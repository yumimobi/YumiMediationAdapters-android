package com.yumi.android.sdk.ads.adapter.applovin;

import com.applovin.adview.AppLovinInterstitialAd;
import com.applovin.adview.AppLovinInterstitialAdDialog;
import com.applovin.sdk.AppLovinAd;
import com.applovin.sdk.AppLovinAdClickListener;
import com.applovin.sdk.AppLovinAdDisplayListener;
import com.applovin.sdk.AppLovinAdLoadListener;
import com.applovin.sdk.AppLovinAdSize;
import com.applovin.sdk.AppLovinAdUpdateListener;
import com.applovin.sdk.AppLovinAdVideoPlaybackListener;
import com.applovin.sdk.AppLovinSdk;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerInterstitialAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import android.app.Activity;

public class ApplovinInterstitialAdapter extends
		YumiCustomerInterstitialAdapter {

	private static final String TAG = "ApplovinInterstitialAdapter";
	private AppLovinInterstitialAdDialog dialog;
	private ApplovinInterstititlListener appListener;
	private AppLovinSdk appLovinSDK;
	private boolean isReadyToDisplay = false;
	private AppLovinAd currentAd = null;
	private boolean isFirstClick = false; 			

	protected ApplovinInterstitialAdapter(Activity activity,
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
	public boolean onActivityBackPressed() {
		return false;
	}

	@Override
	protected void onPrepareInterstitial() {
		isFirstClick = false;
		if (appLovinSDK != null) {
			isReadyToDisplay = false;
			ZplayDebug.d(TAG, "applovin request new interstitial ", onoff);
			appLovinSDK.getAdService().loadNextAd(AppLovinAdSize.INTERSTITIAL, appListener);
		}
	}

	@Override
	protected void onShowInterstitialLayer(Activity activity) {
		dialog.showAndRender(currentAd);
	}

	@Override
	protected boolean isInterstitialLayerReady() {
		ZplayDebug.d(TAG, "is ready to show " + dialog.isAdReadyToDisplay(), onoff);
		if (currentAd != null && isReadyToDisplay) {
			return true;
		}
		return false;
	}

	@Override
	protected void init() {
		ZplayDebug.i(TAG, "sdkKey : " + getProvider().getKey1(), onoff);
		createAppLovinListener();
		ApplovinExtraHolder.initApplovinSDK(getActivity(), getProvider().getKey1());
		appLovinSDK = ApplovinExtraHolder.getAppLovinSDK();
		dialog = AppLovinInterstitialAd.create(appLovinSDK
				, getActivity());
		dialog.setAdLoadListener(appListener);
		dialog.setAdClickListener(appListener);
		dialog.setAdDisplayListener(appListener);
		dialog.setAdVideoPlaybackListener(appListener);
	}

	private void createAppLovinListener() {
		appListener = new ApplovinInterstititlListener() {

			@Override
			public void adClicked(AppLovinAd arg0) {
				if (!isFirstClick) {
					ZplayDebug.d(TAG, "applovin ad clicked", onoff);
					layerClicked(-99f, -99f);
					isFirstClick = true;
				}
			}

			@Override
			public void videoPlaybackEnded(AppLovinAd arg0, double arg1,
					boolean arg2) {
				ZplayDebug.d(TAG, "applovin video playback ended", onoff);
			}

			@Override
			public void videoPlaybackBegan(AppLovinAd arg0) {
				ZplayDebug.d(TAG, "applovin vide playback began", onoff);
			}

			@Override
			public void failedToReceiveAd(int arg0) {
				ZplayDebug.d(TAG, "applovin load failed " + arg0, onoff);
				if (arg0 == 204) {
					layerPreparedFailed(LayerErrorCode.ERROR_NO_FILL);
				}else {
					layerPreparedFailed(LayerErrorCode.ERROR_INTERNAL);
				}
			}

			@Override
			public void adReceived(AppLovinAd arg0) {
				ZplayDebug.d(TAG, "applovin load success  " + arg0.getAdIdNumber(), onoff);
				currentAd = arg0;
				isReadyToDisplay  = true;
				layerPrepared();
			}

			@Override
			public void adHidden(AppLovinAd arg0) {
				ZplayDebug.d(TAG, "applovin hidden", onoff);
				layerClosed();
			}

			@Override
			public void adDisplayed(AppLovinAd arg0) {
				ZplayDebug.d(TAG, "applovin displayed", onoff);
				layerExposure();
			}

			@Override
			public void adUpdated(AppLovinAd arg0) {
				ZplayDebug.d(TAG, "applovin update", onoff);
			}
		};
	}

	@Override
	protected void callOnActivityDestroy() {
		ApplovinExtraHolder.destroyHolder();
	}

	private abstract class ApplovinInterstititlListener implements
			AppLovinAdClickListener, AppLovinAdLoadListener,
			AppLovinAdDisplayListener, AppLovinAdVideoPlaybackListener,
			AppLovinAdUpdateListener{

	}

}
