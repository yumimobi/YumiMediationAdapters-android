package com.yumi.android.sdk.ads.adapter.startapp;


import com.startapp.android.publish.ads.banner.Banner;
import com.startapp.android.publish.ads.banner.BannerListener;
import com.startapp.android.publish.adsCommon.StartAppSDK;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerBannerAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import android.app.Activity;
import android.view.View;

public final class StartappBannerAdapter extends YumiCustomerBannerAdapter {

	private static final String TAG = "StartappBannerAdapter";
	private Banner banner;
	private BannerListener bannerListener;
	
	
	protected StartappBannerAdapter(Activity activity, YumiProviderBean provider) {
		super(activity, provider);
	}

	@Override
	public void onActivityPause() {
		
	}

	@Override
	public void onActivityResume() {

	}

	@Override
	protected void onPrepareBannerLayer() {
		banner = new Banner(getActivity(), bannerListener);
		sendChangeViewBeforePrepared(banner);
	}

	@Override
	protected void init() {
		ZplayDebug.i(TAG, "banner appID : " + getProvider().getKey1(), onoff);
		StartAppSDK.init(getActivity(), getProvider().getKey1(), false);
		bannerListener = new BannerListener() {
			
			@Override
			public void onReceiveAd(View arg0) {
				ZplayDebug.d(TAG, "banner prepared", onoff);
				layerPrepared(banner, true);
			}
			
			@Override
			public void onFailedToReceiveAd(View arg0) {
				ZplayDebug.d(TAG, "banner prepared failed ", onoff);
				layerPreparedFailed(LayerErrorCode.ERROR_INTERNAL);
			}
			
			@Override
			public void onClick(View arg0) {
				layerClicked(-99f, -99f);
			}
		};
	}

	@Override
	protected void callOnActivityDestroy() {
	}

}
