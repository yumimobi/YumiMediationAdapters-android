package com.yumi.android.sdk.ads.adapter.gdtmob;

import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.BannerADListener;
import com.qq.e.ads.banner.BannerView;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerBannerAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import android.app.Activity;

public class GdtmobBannerAdapter extends YumiCustomerBannerAdapter {

	private BannerADListener bannerListener;
	private BannerView banner;

	private static final String TAG = "GdtBannerAdapter";

	protected GdtmobBannerAdapter(Activity activity, YumiProviderBean provider) {
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
		ZplayDebug.d(TAG, "gdt request new banner", onoff);
		banner = new BannerView(getActivity(), ADSize.BANNER,
				getProvider().getKey1(), getProvider().getKey2());
		banner.setADListener(bannerListener);
		banner.setRefresh(0);
		banner.loadAD();
	}

	@Override
	protected void init() {
		ZplayDebug.i(TAG, "appId : " + getProvider().getKey1(), onoff);
		ZplayDebug.i(TAG, "pId : " + getProvider().getKey2(), onoff);
		bannerListener = new BannerADListener() {
			
			@Override
			public void onNoAD(int arg0) {
				ZplayDebug.d(TAG, "gdt banner failed " + arg0, onoff);
				layerPreparedFailed(decodeErrorCode(arg0));				
			}
			
			@Override
			public void onADReceiv() {
				ZplayDebug.d(TAG, "gdt banner prepared", onoff);
				layerPrepared(banner, false);				
			}
			
			@Override
			public void onADOpenOverlay() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onADLeftApplication() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onADExposure() {
				ZplayDebug.d(TAG, "gdt banner shown", onoff);
				layerExposure();				
			}
			
			@Override
			public void onADClosed() {
				ZplayDebug.d(TAG, "gdt banner closed", onoff);
				layerClosed();				
			}
			
			@Override
			public void onADCloseOverlay() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onADClicked() {
				ZplayDebug.d(TAG, "gdt banner clicked", onoff);
				layerClicked(-99f, -99f);				
			}
		};
	}

	private LayerErrorCode decodeErrorCode(int arg0) {
		if (arg0 == 500) {
			return LayerErrorCode.ERROR_INVALID;
		}
		if (arg0 == 501) {
			return LayerErrorCode.ERROR_NO_FILL;
		}
		if (arg0 >= 400 && arg0 < 500) {
			return LayerErrorCode.ERROR_NETWORK_ERROR;
		}
		return LayerErrorCode.ERROR_INTERNAL;
	}
}
