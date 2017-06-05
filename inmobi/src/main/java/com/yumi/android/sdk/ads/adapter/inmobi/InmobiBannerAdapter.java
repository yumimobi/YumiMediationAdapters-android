package com.yumi.android.sdk.ads.adapter.inmobi;

import java.util.Map;

import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiBanner;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerBannerAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.AdSize;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.widget.FrameLayout;

public class InmobiBannerAdapter extends YumiCustomerBannerAdapter {

	private static final String TAG = "InmobiBannerAdapter";
	private InMobiBanner banner;
	private InMobiBanner.BannerAdListener bannerListener;
	private FrameLayout container;
	private int bannerHeight;
	private int bannerWidth;
	
	protected InmobiBannerAdapter(Activity activity, YumiProviderBean provider) {
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
	protected void onPrepareBannerLayer() {
		calculateBannerSize();
		ZplayDebug.d(TAG, "inmobi request new banner", onoff);
		String key2 = getProvider().getKey2();
		long placementID = 0L;
		if (key2 != null && key2.length() > 0) {
			try {
				placementID = Long.valueOf(key2);
			} catch (NumberFormatException e) {
				ZplayDebug.e(TAG, "", e, onoff);
				layerPreparedFailed(LayerErrorCode.ERROR_OVER_RETRY_LIMIT);
				return ;
			}
		}else {
			layerPreparedFailed(LayerErrorCode.ERROR_OVER_RETRY_LIMIT);
			return;
		}
		container = new FrameLayout(getActivity());
		
		banner = new InMobiBanner(getActivity(), placementID);
		banner.setListener(bannerListener);
		banner.setEnableAutoRefresh(false);
		container.addView(banner, new FrameLayout.LayoutParams(bannerWidth,bannerHeight, Gravity.CENTER));
		sendChangeViewBeforePrepared(container);
		banner.load();
	}

	private void calculateBannerSize() {
        if (isMatchWindowWidth && calculateLayerSize != null) {
            if (calculateLayerSize[0] > 0 && calculateLayerSize[1] > 0) {
                bannerWidth = calculateLayerSize[0];
                bannerHeight = calculateLayerSize[1];
                return;
            } else {
                if (bannerSize == AdSize.BANNER_SIZE_728X90) {
                    bannerWidth = 728;
                    bannerHeight = 90;
                } else {
                    bannerWidth = 320;
                    bannerHeight = 50;
                }
                bannerWidth = dip2px(getContext(), bannerWidth);
                bannerHeight = dip2px(getContext(), bannerHeight);
            }
        } else {
            if (bannerSize == AdSize.BANNER_SIZE_728X90) {
                bannerWidth = 728;
                bannerHeight = 90;
            } else {
                bannerWidth = 320;
                bannerHeight = 50;
            }
            bannerWidth = dip2px(getContext(), bannerWidth);
            bannerHeight = dip2px(getContext(), bannerHeight);
        }
	}

	@Override
	protected void init() {
		ZplayDebug.i(TAG, "accounID : " + getProvider().getKey1(), onoff);
		ZplayDebug.i(TAG, "placementID : " + getProvider().getKey2(), onoff);
		InmobiExtraHolder.initInmobiSDK(getActivity(), getProvider().getKey1());
		bannerListener = new InMobiBanner.BannerAdListener() {
			
			@Override
			public void onUserLeftApplication(InMobiBanner arg0) {
				ZplayDebug.d(TAG, "inmobi banner left application", onoff);
				layerClicked(-99f, -99f);
			}
			
			@Override
			public void onAdRewardActionCompleted(InMobiBanner arg0,
					Map<Object, Object> arg1) {
			}
			
			@Override
			public void onAdLoadSucceeded(InMobiBanner arg0) {
				ZplayDebug.d(TAG, "inmobi banner load successed", onoff);
				layerPrepared(container, true);
			}
			
			@Override
			public void onAdLoadFailed(InMobiBanner arg0, InMobiAdRequestStatus arg1) {
				ZplayDebug.d(TAG, "inmobi banner load failed " + arg1.getStatusCode(), onoff);
				layerPreparedFailed(InmobiExtraHolder.decodeError(arg1.getStatusCode()));
			}
			
			@Override
			public void onAdInteraction(InMobiBanner arg0, Map<Object, Object> arg1) {
			}
			
			@Override
			public void onAdDisplayed(InMobiBanner arg0) {
			}
			
			@Override
			public void onAdDismissed(InMobiBanner arg0) {
			}
		};
		
		
		
//		bannerListener = new IMBannerListener() {
//
//			@Override
//			public void onShowBannerScreen(IMBanner arg0) {
//				ZplayDebug.d(TAG, "inmobi banner shown");
//				layerExposure();
//			}
//
//			@Override
//			public void onLeaveApplication(IMBanner arg0) {
//				ZplayDebug.d(TAG, "inmobi banner clicked");
//				layerClicked(-99f, -99f);
//			}
//
//			@Override
//			public void onDismissBannerScreen(IMBanner arg0) {
//				ZplayDebug.d(TAG, "inmobi banner closed");
//				layerClosed();
//			}
//
//			@Override
//			public void onBannerRequestSucceeded(IMBanner arg0) {
//				ZplayDebug.d(TAG, "inmobi banner prapared");
//				layerPrepared(arg0, false);
//			}
//
//			@Override
//			public void onBannerRequestFailed(IMBanner arg0, IMErrorCode arg1) {
//				ZplayDebug.d(TAG, "inmobi banner failed " + arg1);
//				layerPreparedFailed(InmobiExtraHolder.decodeErrorCode(arg1));
//			}
//
//			@Override
//			public void onBannerInteraction(IMBanner arg0,
//					Map<String, String> arg1) {
//
//			}
//		};
	}

	private final int dip2px(Context context, int dp){
		float scale = context.getResources().getDisplayMetrics().density;
		return ((int)(dp * scale +0.5f));
	}
}
