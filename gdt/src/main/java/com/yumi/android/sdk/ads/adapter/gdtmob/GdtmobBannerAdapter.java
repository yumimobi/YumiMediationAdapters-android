package com.yumi.android.sdk.ads.adapter.gdtmob;

import android.app.Activity;

import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.BannerADListener;
import com.qq.e.ads.banner.BannerView;
import com.qq.e.comm.util.AdError;
import com.yumi.android.sdk.ads.adapter.ErrorCodeHelp;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerBannerAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

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
			
//			@Override
//			public void onNoAD(AdError arg0) {
//				ZplayDebug.d(TAG, "gdt banner failed ErrorCode:" + arg0.getErrorCode()+" ErrorMessage:"+arg0.getErrorMsg(), onoff);
//				layerPreparedFailed(ErrorCodeHelp.decodeErrorCode(arg0.getErrorCode()));
//			}

//			@Override
//			public void onNoAD(int errorCode) {
//				ZplayDebug.d(TAG, "gdt banner failed ErrorCode:" + errorCode, onoff);
//				layerPreparedFailed(ErrorCodeHelp.decodeErrorCode(errorCode));
//			}

			@Override
			public void onNoAD(AdError adError) {
				ZplayDebug.d(TAG, "gdt banner failed ErrorCode:" + adError.getErrorCode() + " msg:" + adError.getErrorMsg(), onoff);
				layerPreparedFailed(ErrorCodeHelp.decodeErrorCode(adError.getErrorCode()));
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
}
