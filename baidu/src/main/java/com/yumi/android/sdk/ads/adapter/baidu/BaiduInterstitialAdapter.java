package com.yumi.android.sdk.ads.adapter.baidu;

import com.baidu.mobads.AdView;
import com.baidu.mobads.InterstitialAd;
import com.baidu.mobads.InterstitialAdListener;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerInterstitialAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import android.app.Activity;

import static com.yumi.android.sdk.ads.adapter.baidu.BaiduUtil.recodeError;

public class BaiduInterstitialAdapter extends YumiCustomerInterstitialAdapter {
	private static final String TAG = "BaiduInstertitialAdapter";
	private InterstitialAdListener instertitialListener;
	private InterstitialAd instertitial;

	protected BaiduInterstitialAdapter(Activity activity,
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
		ZplayDebug.d(TAG, "baidu request new interstitial", onoff);
		if (instertitial == null) {
			instertitial = new InterstitialAd(getActivity(), getProvider().getKey2());
//			instertitial = new InterstitialAd(getContext(), AdSize., getProvider().getKey2())
//			AdView.setAppSid(getContext(), getProvider().getKey1());
//			AdView.setAppSec(getContext(), getProvider().getKey1());
			instertitial.setListener(instertitialListener);
		}
		instertitial.loadAd();
	}

	@Override
	protected void onShowInterstitialLayer(Activity activity) {
		instertitial.showAd(activity);
	}

	@Override
	protected boolean isInterstitialLayerReady() {
		if (instertitial != null && instertitial.isAdReady()) {
			return true;
		}
		return false;
	}

	@Override
	protected void init() {
		ZplayDebug.i(TAG, "appSid : " + getProvider().getKey1(), onoff);
		ZplayDebug.i(TAG, "adPlaceID : " + getProvider().getKey2(), onoff);
		createListener();
		AdView.setAppSid(getContext(), getProvider().getKey1());
	}

	private void createListener() {
		if (instertitialListener == null) {
			instertitialListener = new InterstitialAdListener() {

				@Override
				public void onAdReady() {
					ZplayDebug.d(TAG, "baidu interstitial prepared", onoff);
					layerPrepared();
				}

				@Override
				public void onAdPresent() {
					ZplayDebug.d(TAG, "baidu interstitial shown", onoff);
					layerExposure();
					layerStartPlaying();
				}

				@Override
				public void onAdFailed(String arg0) {
					ZplayDebug.d(TAG, "baidu interstitial failed " +  arg0, onoff);
					layerPreparedFailed(recodeError(arg0));
				}

				@Override
				public void onAdDismissed() {
					ZplayDebug.d(TAG, "baidu interstitial closed", onoff);
					layerClosed();
				}

				@Override
				public void onAdClick(InterstitialAd arg0) {
					ZplayDebug.d(TAG, "baidu interstitial clicked", onoff);
					layerClicked(-99f, -99f);
				}
			};
		}
	}

}
