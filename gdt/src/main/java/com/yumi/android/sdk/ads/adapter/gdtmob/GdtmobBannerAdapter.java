package com.yumi.android.sdk.ads.adapter.gdtmob;

import android.app.Activity;

import com.qq.e.ads.banner2.UnifiedBannerADListener;
import com.qq.e.ads.banner2.UnifiedBannerView;
import com.qq.e.comm.util.AdError;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerBannerAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.yumi.android.sdk.ads.adapter.GdtUtil.recodeError;
import static com.yumi.android.sdk.ads.publish.enumbean.AdSize.BANNER_SIZE_SMART;

public class GdtmobBannerAdapter extends YumiCustomerBannerAdapter {

	private UnifiedBannerADListener unifiedBannerListener;
	private UnifiedBannerView unifiedBanner;

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
		if (unifiedBanner != null) {
			unifiedBanner.destroy();
		}
	}

	@Override
	protected void onPrepareBannerLayer() {
		if (bannerSize == BANNER_SIZE_SMART) {
			ZplayDebug.d(TAG, "gdt not support smart banner", onoff);
			layerPreparedFailed(recodeError(new AdError(5004, null), "not support smart banner."));
			return;
		}
		ZplayDebug.d(TAG, "gdt request new banner", onoff);
		unifiedBanner = new UnifiedBannerView(getActivity(), getProvider().getKey1(), getProvider().getKey2(), unifiedBannerListener);
		//设置广告轮播时间，为0或30~120之间的数字，单位为s,0标识不自动轮播
		unifiedBanner.setRefresh(getProvider().getAutoRefreshInterval());
		unifiedBanner.loadAD();
	}

	@Override
	protected void init() {
		ZplayDebug.i(TAG, "appId : " + getProvider().getKey1(), onoff);
		ZplayDebug.i(TAG, "pId : " + getProvider().getKey2(), onoff);
		unifiedBannerListener = new UnifiedBannerADListener() {

			@Override
			public void onNoAD(AdError adError) {
                if (adError == null){
                    ZplayDebug.d(TAG, "gdt banner failed adError = null", onoff);
                    layerPreparedFailed(recodeError(null));
                    return;
                }
				ZplayDebug.d(TAG, "gdt banner failed ErrorCode:" + adError.getErrorCode() + " msg:" + adError.getErrorMsg(), onoff);
				layerPreparedFailed(recodeError(adError));
			}

			@Override
			public void onADReceive() {
				ZplayDebug.d(TAG, "gdt banner prepared", onoff);
				layerPrepared(unifiedBanner, false);
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
