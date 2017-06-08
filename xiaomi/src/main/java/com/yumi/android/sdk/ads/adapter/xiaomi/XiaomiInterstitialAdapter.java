package com.yumi.android.sdk.ads.adapter.xiaomi;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import com.xiaomi.ad.AdListener;
import com.xiaomi.ad.AdSdk;
import com.xiaomi.ad.adView.InterstitialAd;
import com.xiaomi.ad.common.pojo.AdError;
import com.xiaomi.ad.common.pojo.AdEvent;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerInterstitialAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

public class XiaomiInterstitialAdapter extends YumiCustomerInterstitialAdapter {

	private static final String TAG = "XiaoMiInterstitialAdapter";
    private  InterstitialAd mInterstitialAd;
	private static AdListener instertitialListener;

    private boolean isFirstPrepare=true;
    
	protected XiaomiInterstitialAdapter(Activity activity,
			YumiProviderBean provider) {
		super(activity, provider);
	}

	@Override
	public void onActivityPause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onActivityResume() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onActivityBackPressed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onPrepareInterstitial() {
		ZplayDebug.d(TAG, "xiaomi request new interstitial", onoff);
		mInterstitialAd.requestAd(getProvider().getKey2(),instertitialListener);
        isFirstPrepare=true;
	}

	@Override
	protected void onShowInterstitialLayer(Activity activity) {
		if (mInterstitialAd.isReady()) {
            try {
                mInterstitialAd.show();
                ZplayDebug.v(TAG, "xiaomi interstitial mInterstitialAd.show", onoff);
            } catch (Exception e) {
                ZplayDebug.e(TAG, "xiaomi interstitial onShowInterstitialLayer error:",e, onoff);
            }
		}
	}

	@Override
	protected boolean isInterstitialLayerReady() {
        ZplayDebug.v(TAG, "xiaomi interstitial isInterstitialLayerReady isReady()="+mInterstitialAd.isReady(), onoff);
		return mInterstitialAd.isReady();
	}

	@Override
	protected void init() {
//		AdSdk.setDebugOn();
//		AdSdk.setMockOn(); // 调试时打开，正式发布时关闭
		AdSdk.initialize(getActivity(), getProvider().getKey1());
        mInterstitialAd = new InterstitialAd(getActivity().getApplicationContext(), getActivity());
		createListener();
	}

	private void createListener() {

		if (instertitialListener == null) {
			instertitialListener = new AdListener() {

				@Override
				public void onAdLoaded() {
					// 这个方法被调用时，表示要展示插屏广告了。如果需要，您可以做一些后续逻辑处理。
			        ZplayDebug.v(TAG, "xiaomi ionAdLoaded isReady()="+mInterstitialAd.isReady(), onoff);
                    if (mInterstitialAd.isReady() && isFirstPrepare) {
                        ZplayDebug.d(TAG, "xiaomi interstitial prepared", onoff);
                        layerPrepared();
                        isFirstPrepare=false;
                    }
				}

				@Override
				public void onAdEvent(AdEvent event) {

					// 这儿会有一系列用户行为相关的回调事件。如果需要，您可以通过event.mType来判断用户的行为类型，从而采取不同的处理方式。
					if (AdEvent.TYPE_SKIP == event.mType) {
						// 用户关闭了插播广告
						ZplayDebug.d(TAG, "xiaomi interstitial closed", onoff);
						layerClosed();
					} else if (AdEvent.TYPE_CLICK == event.mType) {
						// 用户点击了c广告
						ZplayDebug.d(TAG, "xiaomi interstitial clicked", onoff);
						layerClicked(-99f, -99f);
//                        layerClosed();
					} else if (AdEvent.TYPE_VIEW == event.mType) {
						// 插播广告展示
						ZplayDebug
								.d(TAG, "xiaomi interstitial exposure", onoff);
						layerExposure();
					}
				}

				@Override
				public void onAdError(AdError error) {
					// 这个方法被调用时，表示从服务器端请求插屏广告时，出现错误。
					ZplayDebug.d(TAG, "xiaomi interstitial failed " + error,
							onoff);
					layerPreparedFailed(decodeErrorCode(error));
				}

                @Override
                public void onViewCreated(View arg0) {
                    // TODO Auto-generated method stub
                    
                }
			};
		}
	}

	private LayerErrorCode decodeErrorCode(AdError arg1) {
		if (arg1 == AdError.ERROR_NO_AD) {
			return LayerErrorCode.ERROR_NO_FILL;
		} 
		return LayerErrorCode.ERROR_INTERNAL;
	}

	@Override
	protected void callOnActivityDestroy() {
		// TODO Auto-generated method stub

	}

}
