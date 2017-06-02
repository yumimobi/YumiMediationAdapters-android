package com.yumi.android.sdk.ads.adapter.gdtnative;

import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Point;
import android.view.Display;
import android.webkit.WebView;

import com.qq.e.ads.nativ.NativeAD;
import com.qq.e.ads.nativ.NativeAD.NativeAdListener;
import com.qq.e.ads.nativ.NativeADDataRef;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.NativeAdsBuild;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.publish.nativead.YumiNativeIntersititalAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

public class GdtnativeInterstitialAdapter extends YumiNativeIntersititalAdapter
{

	private static final String TAG = "GdtnativeInterstitialAdapter";
	
	private NativeAD nativeAD;
	private NativeADDataRef adItem;
	private String html;

	private WebView wv_interstitial;
	
	protected GdtnativeInterstitialAdapter(Activity activity, YumiProviderBean provider)
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
		closeOnResume();
	}

	@Override
	public boolean onActivityBackPressed()
	{
		return false;
	}

	@Override
	protected void onPreparedWebInterstitial()
	{
		if (nativeAD!=null)
		{
			nativeAD.loadAD(1);
		}
	}

	@Override
	protected void webLayerClickedAndRequestBrowser(String url)
	{
		if (adItem!=null)
		{
			adItem.onClicked(wv_interstitial);
		}
		layerClicked(upPoint[0], upPoint[1]);
	}

	@Override
	protected void webLayerPrepared(WebView view)
	{
		this.wv_interstitial = view;
		ZplayDebug.d(TAG, "GDT navitead interstitial prapared", onoff);
		layerPrepared();
	}

	@Override
	protected void webLayerOnShow()
	{
		if (adItem!=null)
		{
			adItem.onExposured(wv_interstitial);
		}
		layerExposure();
	}

	@Override
	protected void calculateRequestSize()
	{
		
	}

	@Override
	protected void webLayerDismiss()
	{
		layerClosed();
	}

	@Override
	protected void init()
	{
		ZplayDebug.d(TAG, "appId : " + getProvider().getKey1(), onoff);
		ZplayDebug.d(TAG, "pId : " + getProvider().getKey2(), onoff);
		ZplayDebug.d(TAG, "GDT nativead init", onoff);
		if (nativeAD == null)
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
		public void onNoAD(int arg0)
		{
			ZplayDebug.d(TAG, "GDT nativead interstitial no ad:"+arg0, onoff);
			layerPreparedFailed(LayerErrorCode.ERROR_NO_FILL);
		}
		
		@Override
		public void onADStatusChanged(NativeADDataRef arg0)
		{
			ZplayDebug.d(TAG, "GDT nativead interstitial onADStatusChanged", onoff);
		}
		
		@Override
		public void onADLoaded(List<NativeADDataRef> arg0)
		{
			if (arg0.size() > 0)
			{
				getProvider().setUseTemplateMode(0);
				adItem = arg0.get(0);
				//html = NativeAdsBuild.getImageAdHtml(getActivity(),adItem.getTitle(),adItem.getDesc(),adItem.getIconUrl(), adItem.getImgUrl(), getaTagUrl(),adItem.getAPPScore(),0);				
				html = NativeAdsBuild.getTemplateInterstitial(getActivity(),adItem.getTitle(),adItem.getDesc(),adItem.getIconUrl(), adItem.getImgUrl(), getaTagUrl(),adItem.getAPPScore(),0, getProvider());				
				ZplayDebug.d(TAG, "GDT nativead interstitial request success!", onoff);
				if (html!=null && !"".equals(html) && !"null".equals(html))
				{
				    haveStroke=false;
//					calculateWebSize(1280, 720);
	                int[] screen = getRealSize(getActivity());
	                calculateWebSize(screen[0], screen[1]);
	                
	                ZplayDebug.d(TAG, "GDT nativead interstitial Width="+screen[0]+" || Height="+screen[1], onoff);
	                
					createWebview(null);
					loadData(html);
				}else{
					layerPreparedFailed(LayerErrorCode.ERROR_NO_FILL);
				}
			}else
			{
				layerPreparedFailed(LayerErrorCode.ERROR_NO_FILL);
				ZplayDebug.d(TAG, "GDT nativead interstitial PreparedFailed ERROR_NO_FILL", onoff);
			}
		}

		@Override
		public void onADError(NativeADDataRef arg0, int arg1)
		{
			layerPreparedFailed(LayerErrorCode.ERROR_INTERNAL);
			ZplayDebug.d(TAG, "GDT nativead interstitial PreparedFailed " + arg1, onoff);
		}
		
	}
	
	@SuppressLint("NewApi")
	public static final int[] getRealSize(Activity activity)
	{
		try
		{
			// TODO 增加系统版本判断
			if (getAndroidSDK() >= 17)
			{
				Point point = new Point();
				activity.getWindowManager().getDefaultDisplay().getRealSize(point);
				int[] realSize = new int[]
				{ point.x, point.y };
				return realSize;
			} else
			{
				Display display = activity.getWindowManager().getDefaultDisplay();
				int[] realSize = new int[]
				{ display.getWidth(), display.getHeight() };
				return realSize;
			}
		} catch (Exception e)
		{
			ZplayDebug.e(TAG, "GDT nativead interstitial getRealSize error  ", e, onoff);
		}
		return new int[]
		{ 1280, 720 };

	}

	/**
	 * 获取android版本号int
	 * 
	 * @return
	 */
	public static int getAndroidSDK()
	{
		return android.os.Build.VERSION.SDK_INT;
	}

}
