package com.yumi.android.sdk.ads.adapter.facebook.facebook;

import android.app.Activity;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerBannerAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

public class FacebookBannerAdapter extends YumiCustomerBannerAdapter {

	private static final String TAG = "FacebooknativeBannerAdapter";
	private AdView banner;
	private AdListener bannerListener;

	protected FacebookBannerAdapter(Activity activity, YumiProviderBean provider) {
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
		if (banner != null) {
			banner.destroy();
		}
	}

	@Override
	protected void onPrepareBannerLayer() {
		
		ZplayDebug.d(TAG, "facebook request new banner", onoff);
		banner = new AdView(getContext(), getProvider().getKey1(), calculateBannerSize());
		banner.setAdListener(bannerListener);
		banner.loadAd();
	}

	@Override
	protected void init() {
		ZplayDebug.i(TAG, "placementID : " + getProvider().getKey1(), onoff);
		createBannerListener();
	}

	private void createBannerListener() {
		if (bannerListener == null) {
			bannerListener = new AdListener() {

				@Override
				public void onError(Ad arg0, AdError arg1) {
					ZplayDebug.d(TAG, "facebook banner failed " + arg1.getErrorMessage(), onoff);
					layerPreparedFailed(decodeErrorCode(arg1));
				}

				@Override
				public void onAdLoaded(Ad arg0) {
					ZplayDebug.d(TAG, "facebook banner prepared", onoff);
					layerPrepared(banner, true);
				}

				@Override
				public void onAdClicked(Ad arg0) {
					ZplayDebug.d(TAG, "facebook banner clicked", onoff);
					layerClicked(-99f, -99f);
				}

				@Override
				public void onLoggingImpression(Ad ad) {

				}
			};
		}
	}

	protected LayerErrorCode decodeErrorCode(AdError arg1) {
		if (arg1.equals(AdError.NETWORK_ERROR)) {
			return LayerErrorCode.ERROR_NETWORK_ERROR;
		}
		if (arg1.equals(AdError.NO_FILL)) {
			return LayerErrorCode.ERROR_NO_FILL;
		}
		return LayerErrorCode.ERROR_INTERNAL;
	}

	private AdSize calculateBannerSize() {
		if (bannerSize == com.yumi.android.sdk.ads.publish.enumbean.AdSize.BANNER_SIZE_320X50) {
		    if(isMatchWindowWidth)
		    {
		        return AdSize.BANNER_HEIGHT_50;
		    }
			return AdSize.BANNER_320_50;
		}
		if (bannerSize == com.yumi.android.sdk.ads.publish.enumbean.AdSize.BANNER_SIZE_728X90) {
			return AdSize.BANNER_HEIGHT_90;
		}
		return AdSize.BANNER_320_50;
	}
}