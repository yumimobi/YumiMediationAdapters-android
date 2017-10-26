package com.yumi.android.sdk.ads.adapter.gdtmob;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import com.qq.e.ads.interstitial.InterstitialAD;
import com.qq.e.ads.interstitial.InterstitialADListener;
import com.qq.e.comm.util.AdError;
import com.yumi.android.sdk.ads.adapter.ErrorCodeHelp;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerInterstitialAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

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
			interstitial.destroy();
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
			public void onNoAD(AdError arg0) {
				ZplayDebug.d(TAG, "gdt interstitial failed ErrorCode:" + arg0.getErrorCode()+" ErrorMessage:"+arg0.getErrorMsg(), onoff);
				layerPreparedFailed(ErrorCodeHelp.decodeErrorCode(arg0.getErrorCode()));
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
					interstitial.destroy();
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
}
