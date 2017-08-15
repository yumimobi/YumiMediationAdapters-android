package com.yumi.android.sdk.ads.adapter.adcolony;

import com.adcolony.sdk.AdColony;
import com.adcolony.sdk.AdColonyAdOptions;
import com.adcolony.sdk.AdColonyAppOptions;
import com.adcolony.sdk.AdColonyInterstitial;
import com.adcolony.sdk.AdColonyInterstitialListener;
import com.adcolony.sdk.AdColonyReward;
import com.adcolony.sdk.AdColonyRewardListener;
import com.adcolony.sdk.AdColonyZone;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerMediaAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import android.app.Activity;

public class AdcolonyMediaAdapter extends YumiCustomerMediaAdapter {

	private static final String TAG = "AdcolonyMediaAdapter";
	private static final String CLIENT_OPTIONS = "version:1.0,store:google";
	private AdColonyInterstitial ad;
	private AdColonyInterstitialListener listener;
	private AdColonyRewardListener rewardListennr;
	private AdColonyAdOptions ad_options;

	protected AdcolonyMediaAdapter(Activity activity, YumiProviderBean provider) {
		super(activity, provider);
	}

	private void initAdcolonySDK() {
		createListeners();
		AdColonyAppOptions app_options = new AdColonyAppOptions()
				.setUserID(CLIENT_OPTIONS);
		AdColony.configure(getActivity(), app_options, getProvider().getKey1(),
				getProvider().getKey2());
		/** Ad specific options to be sent with request */
		ad_options = new AdColonyAdOptions().enableConfirmationDialog(false).enableResultsDialog(false);// 控制dialog
		AdColony.setRewardListener(rewardListennr);
	}

	private void createListeners() {

		listener = new AdColonyInterstitialListener() {
			/** Ad passed back in request filled callback, ad can now be shown */
			@Override
			public void onRequestFilled(AdColonyInterstitial ad) {
				AdcolonyMediaAdapter.this.ad = ad;
				layerPrepared();

				ZplayDebug.d(TAG, "onRequestFilled", onoff);
			}

			/** Ad request was not filled */
			@Override
			public void onRequestNotFilled(AdColonyZone zone) {
				layerPreparedFailed(LayerErrorCode.ERROR_NO_FILL);
				ZplayDebug.d(TAG, "onRequestNotFilled" + zone.getZoneType(), onoff);

			}

			/** Ad opened, reset UI to reflect state change */
			@Override
			public void onOpened(AdColonyInterstitial ad) {
				ZplayDebug.d(TAG, "onOpened", onoff);
				layerExposure();
				layerMediaStart();
			}

			/** Request a new ad if ad is expiring */
			@Override
			public void onExpiring(AdColonyInterstitial ad) {
				ZplayDebug.d(TAG, "onExpiring", onoff);
			}
		};
		rewardListennr = new AdColonyRewardListener() {

			@Override
			public void onReward(AdColonyReward arg0) {
				ZplayDebug.d(TAG, "adcolony media closed", onoff);
				layerMediaEnd();
				ZplayDebug.d(TAG, "adcolony media get reward", onoff);
				layerIncentived();
				layerClosed();
			}
		};

	}

	@Override
	public void onActivityPause() {
		// AdColony.pause();
	}

	@Override
	public void onActivityResume() {
	
	}

	@Override
	protected final void callOnActivityDestroy() {
	}

	@Override
	protected void onPrepareMedia() {
		ZplayDebug.d(TAG, "adcolony request new media", onoff);
		AdColony.requestInterstitial(getProvider().getKey2(), listener, ad_options);
	}

	@Override
	protected void onShowMedia() {
		if (ad != null&&!ad.isExpired()) {

			ad.show();
		}
	}

	@Override
	protected boolean isMediaReady() {
		if (ad != null && !ad.isExpired()) {
			return true;
		}else{
			AdColony.requestInterstitial(getProvider().getKey2(), listener,
					ad_options);
		}
		return false;
	}

	@Override
	protected void init() {
		ZplayDebug.i(TAG, "appId : " + getProvider().getKey1(), onoff);
		ZplayDebug.i(TAG, "zoneId : " + getProvider().getKey2(), onoff);
		initAdcolonySDK();
	}

	@Override
	protected void onRequestNonResponse() {
		super.onRequestNonResponse();
	}

}
