package com.yumi.android.sdk.ads.adapter.gdtnative;

import android.app.Activity;
import android.view.View;

import com.qq.e.ads.nativ.NativeAD;
import com.qq.e.ads.nativ.NativeAD.NativeAdListener;
import com.qq.e.ads.nativ.NativeADDataRef;
import com.qq.e.comm.util.AdError;
import com.yumi.android.sdk.ads.adapter.ErrorCodeHelp;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.NativeAdsBuild;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.publish.nativead.YumiNativeBannerAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import java.util.List;

public class GdtnativeBannerAdapter extends YumiNativeBannerAdapter {

	private static final String TAG = "GdtnativeBannerAdapter";
	private NativeAD nativeAD;
	private NativeADDataRef adItem;
	private String html;
	private View bannerView;
	
	protected GdtnativeBannerAdapter(Activity activity, YumiProviderBean provider)
	{
		super(activity, provider);
	}

	@Override
	public void onActivityPause()
	{
	}

	@Override
	public void onActivityResume()
	{
		
	}

	@Override
	protected void webLayerClickedAndRequestBrowser(String url)
	{
		ZplayDebug.d(TAG, "GDT nativead banner clicked", onoff);
		layerClicked(upPoint[0], upPoint[1]);
		if (adItem!=null)
		{
			adItem.onClicked(this.bannerView);
		}
	}

	@Override
	protected void webLayerPrepared(View view)
	{
		ZplayDebug.d(TAG, "GDT nativead banner prepared", onoff);
		this.bannerView = view;
		layerPrepared(view, false);
		layerExposure();
		adItem.onExposured(view);
	}

	@Override
	protected void calculateRequestSize()
	{
		
	}

	@Override
	protected void onPrepareBannerLayer()
	{
		if (nativeAD!=null)
		{
			nativeAD.loadAD(1);
		}
	}

	@Override
	protected void init()
	{
		ZplayDebug.d(TAG, "appId : " + getProvider().getKey1(), onoff);
		ZplayDebug.d(TAG, "pId : " + getProvider().getKey2(), onoff);
		ZplayDebug.d(TAG, "GDT nativead banner init", onoff);
		if (nativeAD==null)
		{
			nativeAD = new NativeAD(getActivity(), getProvider().getKey1(), getProvider().getKey2(), new MyNativeAdListener());
		}
	}

	@Override
	protected void callOnActivityDestroy()
	{
		
	}
	
	private class MyNativeAdListener implements NativeAdListener
	{

		@Override
		public void onNoAD(AdError adError)
		{
			ZplayDebug.d(TAG, "GDT nativead banner onNoAD ErrorCode:" + adError.getErrorCode()+" ErrorMessage:"+adError.getErrorMsg(), onoff);
			layerPreparedFailed(ErrorCodeHelp.decodeErrorCode(adError.getErrorCode()));
		}
		
		@Override
		public void onADStatusChanged(NativeADDataRef arg0)
		{
			ZplayDebug.d(TAG, "GDT nativead banner onADStatusChanged", onoff);
		}

		@Override
		public void onADError(NativeADDataRef nativeADDataRef, AdError adError) {
			ZplayDebug.d(TAG, "GDT nativead banner onADError ErrorCode:" + adError.getErrorCode()+" ErrorMessage:"+adError.getErrorMsg(), onoff);
			layerPreparedFailed(ErrorCodeHelp.decodeErrorCode(adError.getErrorCode()));
		}

		@Override
		public void onADLoaded(List<NativeADDataRef> arg0)
		{
			if (arg0.size() > 0)
			{
				getProvider().setUseTemplateMode("0");
                adItem = arg0.get(0);
                //html = NativeAdsBuild.getImageTextAdHtml(adItem.getIconUrl(), adItem.getTitle(), adItem.getDesc(), getaTagUrl(), getActivity());
                html = NativeAdsBuild.getTemplateBanner(adItem.getIconUrl(), adItem.getTitle(), adItem.getDesc(), getaTagUrl(), getActivity(), getProvider());
				ZplayDebug.d(TAG, "GDT nativead banner request success!", onoff);
				if (html!=null && !"".equals(html) && !"null".equals(html)) {
					calculateWebSize();
					createWebview(null);
					loadData(html);
				} else {
					layerPreparedFailed(LayerErrorCode.ERROR_NO_FILL);
					ZplayDebug.d(TAG, "GDT nativead banner PreparedFailed ERROR_NO_FILL", onoff);
				}
			}else
			{
				layerPreparedFailed(LayerErrorCode.ERROR_NO_FILL);
				ZplayDebug.d(TAG, "GDT nativead banner PreparedFailed", onoff);
			}
		}

	}

}
