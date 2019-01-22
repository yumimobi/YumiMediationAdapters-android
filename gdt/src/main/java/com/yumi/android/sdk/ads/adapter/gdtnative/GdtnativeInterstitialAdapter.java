package com.yumi.android.sdk.ads.adapter.gdtnative;


import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;

import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.comm.util.AdError;
import com.yumi.android.sdk.ads.adapter.ErrorCodeHelp;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.publish.nativead.YumiNativeExpressIntersititalAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import java.util.List;

public class GdtnativeInterstitialAdapter extends YumiNativeExpressIntersititalAdapter {
	private static final String TAG = "GdtnativeInterstitialAdapter";
	private NativeExpressAD nativeExpressAD;
	private NativeExpressADView nativeExpressADView;
	private Activity mActivity;
	private static int width;
	private static int height;

	protected GdtnativeInterstitialAdapter(Activity activity, YumiProviderBean yumiProviderBean) {
		super(activity, yumiProviderBean);
		mActivity=activity;
	}

	@Override
	protected void onPreparedNativeInterstitial() {
		ZplayDebug.d(TAG, "appId : " + getProvider().getKey1(), onoff);
		ZplayDebug.d(TAG, "pId : " + getProvider().getKey2(), onoff);
		ZplayDebug.d(TAG, "GDT nativead Interstitial init", onoff);
		nativeExpressAD = new NativeExpressAD(getActivity(), calculateInterstitialSize(),
				getProvider().getKey1(),getProvider().getKey2(),new MyNativeExpressADListener());
		nativeExpressAD.loadAD(1);
	}

	@Override
	protected void NativeLayerPrepared(View view) {
		ZplayDebug.d(TAG, "gdt native Interstitial NativeLayerPrepared", onoff);
		layerPrepared();
	}

	@Override
	protected void NativeLayerOnShow() {
		ZplayDebug.d(TAG, "gdt native Interstitial NativeLayerOnShow", onoff);
	}

	@Override
	protected void calculateRequestSize() {
		ZplayDebug.d(TAG, "gdt native Interstitial calculateRequestSize", onoff);
	}

	@Override
	protected void NativeLayerDismiss() {
		ZplayDebug.d(TAG, "gdt native Interstitial NativeLayerDismiss", onoff);
		layerClosed();
		if (nativeExpressADView != null) {
			nativeExpressADView.destroy();
		}
	}

	@Override
	protected void init() {

	}
	private class MyNativeExpressADListener implements NativeExpressAD.NativeExpressADListener {

		@Override
		public void onNoAD(AdError adError) {
			if (adError == null){
				ZplayDebug.d(TAG, "GDT nativead Interstitial onNoAD adError = null", onoff);
				layerPreparedFailed(LayerErrorCode.ERROR_INTERNAL);
				return;
			}
			ZplayDebug.d(TAG, "GDT nativead Interstitial onNoAD ErrorCode:" + adError.getErrorCode() + " msg:" + adError.getErrorMsg(), onoff);
			layerPreparedFailed(ErrorCodeHelp.decodeErrorCode(adError));
		}

		@Override
		public void onADLoaded(List<NativeExpressADView> list) {
			ZplayDebug.d(TAG, "GDT native Interstitial loaded"+list.size(), onoff);
			if (list.size() > 0) {
				nativeExpressADView = list.get(0);
				nativeExpressADView.render();
			}else{
				layerPreparedFailed(LayerErrorCode.ERROR_NO_FILL);
			}
		}

		@Override
		public void onRenderFail(NativeExpressADView nativeExpressADView) {
			ZplayDebug.d(TAG, "GDT native Interstitial render fail", onoff);
			layerPreparedFailed(LayerErrorCode.ERROR_INTERNAL);
		}

		@Override
		public void onRenderSuccess(NativeExpressADView adView) {
			ZplayDebug.d(TAG, "GDT native Interstitial renderSuccess"+adView.getHeight()+","+adView.getWidth(), onoff);
			loadData(adView, false);
		}

		@Override
		public void onADExposure(NativeExpressADView nativeExpressADView) {
			ZplayDebug.d(TAG, "GDT native Interstitial shown", onoff);
			layerExposure();
		}

		@Override
		public void onADClicked(NativeExpressADView nativeExpressADView) {
			ZplayDebug.d(TAG, "GDT native Interstitial Clicked", onoff);
			layerClicked(-99f, -99f);
		}

		@Override
		public void onADClosed(NativeExpressADView nativeExpressADView) {
			ZplayDebug.d(TAG, "GDT native Interstitial closed", onoff);
			closeOnResume();
		}

		@Override
		public void onADLeftApplication(NativeExpressADView nativeExpressADView) {
			ZplayDebug.d(TAG, "GDT native Interstitial onADLeftApplication", onoff);
		}

		@Override
		public void onADOpenOverlay(NativeExpressADView nativeExpressADView) {
			ZplayDebug.d(TAG, "GDT native Interstitial onADOpenOverlay", onoff);
		}

		@Override
		public void onADCloseOverlay(NativeExpressADView nativeExpressADView) {
			ZplayDebug.d(TAG, "GDT native Interstitial onADCloseOverlay", onoff);
		}
	}
	@Override
	protected void callOnActivityDestroy() {
		ZplayDebug.d(TAG, "gdt native Interstitial callOnActivityDestroy", onoff);
		if (nativeExpressADView != null) {
			nativeExpressADView.destroy();
		}
	}

	@Override
	public void onActivityPause() {

	}
	private ADSize calculateInterstitialSize() {
		if(isPortrait(mActivity))
		{
			return new ADSize(ADSize.FULL_WIDTH,ADSize.AUTO_HEIGHT);
		}
		else{
			int adWeight = width * 2/3;
			int adHeight = adWeight * 950/1230;
			if(adHeight>height){
				adHeight = height;
			}
			return new ADSize(adWeight,adHeight);
		}
	}


	private static final boolean isPortrait(Context context){
		try {
			DisplayMetrics dm = context.getResources().getDisplayMetrics();
			float density = dm.density;
			width = (int)(dm.widthPixels/density);
			height = (int)(dm.heightPixels/density);
			if (dm.widthPixels <= dm.heightPixels) {
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}
	@Override
	public void onActivityResume() {
		closeOnResume();
	}

	@Override
	public boolean onActivityBackPressed() {
		return false;
	}
}