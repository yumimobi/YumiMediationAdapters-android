package com.yumi.android.sdk.ads.adapter.gdtmob;

import com.qq.e.ads.interstitial.InterstitialAD;
import com.qq.e.ads.interstitial.InterstitialADListener;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerInterstitialAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

public class GdtmobInterstitialAdapter extends YumiCustomerInterstitialAdapter {

	private static final String TAG = "GdtInterstitialAdapter";
	private InterstitialADListener interstitialListener;
	private InterstitialAD interstitial;
	protected boolean interstitialReady;
	private static final int REQ_INTERSTITIAL = 0x321;

	private final Handler gdtInterstitialHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == REQ_INTERSTITIAL) {
				if (interstitial != null) {
					interstitial.loadAD();
				}
			}
		};
	};

	protected GdtmobInterstitialAdapter(Activity activity,
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
		if (interstitial!=null)
		{
			interstitial.closePopupWindow();
			interstitial.destory();
		}
	}

	@Override
	public boolean onActivityBackPressed() {
		return false;
	}

	@Override
	protected void onPrepareInterstitial() {
		ZplayDebug.d(TAG, "gdt request new interstitial", onoff);
		interstitialReady = false;
		if (interstitial==null)
		{
			interstitial = new InterstitialAD(getActivity(), getProvider().getKey1(), getProvider().getKey2());
			interstitial.setADListener(interstitialListener);
		}
		gdtInterstitialHandler.sendEmptyMessageDelayed(REQ_INTERSTITIAL, 1000);
	}

	@Override
	protected void onShowInterstitialLayer(Activity activity) {
		if (interstitial!=null)
		{
			interstitial.show(activity);
		}
	}

	@Override
	protected boolean isInterstitialLayerReady() {
		if (interstitial != null && interstitialReady) {
			return true;
		}
		return false;
	}

	@Override
	protected void init() {
		ZplayDebug.i(TAG, "appId : " + getProvider().getKey1(), onoff);
		ZplayDebug.i(TAG, "pId : " + getProvider().getKey2(), onoff);
		interstitialListener = new InterstitialADListener() {
			
			@Override
			public void onNoAD(int arg0) {
				ZplayDebug.d(TAG, "gdt interstitial failed "  + arg0, onoff);
				layerPreparedFailed(decodeErrorCode(arg0));
			}
			
			@Override
			public void onADReceive() {
				interstitialReady = true;
				ZplayDebug.d(TAG, "gdt interstitial prepared", onoff);
				layerPrepared();
			}
			
			@Override
			public void onADOpened() {
				
			}
			
			@Override
			public void onADLeftApplication() {
				
			}
			
			@Override
			public void onADExposure() {
				ZplayDebug.d(TAG, "gdt interstitial shown", onoff);
				layerExposure();
			}
			
			@Override
			public void onADClosed() {
				if (interstitial!=null)
				{
					interstitial.destory();
				}
				ZplayDebug.d(TAG, "gdt interstitial closed", onoff);
				layerClosed();
			}
			
			@Override
			public void onADClicked() {
				ZplayDebug.d(TAG, "gdt interstitial clicked", onoff);
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
