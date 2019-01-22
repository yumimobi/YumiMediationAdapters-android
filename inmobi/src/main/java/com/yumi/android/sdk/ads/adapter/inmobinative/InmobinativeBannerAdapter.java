package com.yumi.android.sdk.ads.adapter.inmobinative;

import android.app.Activity;
import android.view.View;

import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiAdRequestStatus.StatusCode;
import com.inmobi.ads.InMobiNative;
import com.inmobi.ads.InMobiNative.NativeAdListener;
import com.inmobi.sdk.InMobiSdk;
import com.yumi.android.sdk.ads.adapter.inmobi.InmobiExtraHolder;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.NativeAdsBuild;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.publish.nativead.YumiNativeBannerAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import org.json.JSONException;
import org.json.JSONObject;

public class InmobinativeBannerAdapter extends YumiNativeBannerAdapter
{
	
	protected InmobinativeBannerAdapter(Activity activity, YumiProviderBean provider)
	{
		super(activity, provider);
	}


	private static final String TAG = "InmobinativeBannerAdapter";
	
	private InMobiNative nativeAd;
	private View webView;

	@Override
	protected void init()
	{
		String key1 = getProvider().getKey1();
		String key2 = getProvider().getKey2();
		ZplayDebug.d(TAG, "key1:"+key1, onoff);
		ZplayDebug.d(TAG, "key2:"+key2, onoff);
		if (nativeAd==null)
		{
			InMobiSdk.init(getActivity(), key1);
			nativeAd = new InMobiNative(Long.valueOf(key2), new MyNativeAdListener());
		}
	}

	@Override
	protected void onPrepareBannerLayer()
	{
		if (nativeAd!=null)
		{
			ZplayDebug.d(TAG, "Inmobi native Banner request", onoff);
			nativeAd.load();
		}
	}

	@Override
	protected void webLayerPrepared(View view)
	{
		this.webView = view;
		layerPrepared(webView, false);
		layerExposure();
		InMobiNative.bind(webView, nativeAd);
	}

	@Override
	protected void callOnActivityDestroy()
	{
		InMobiNative.unbind(webView);
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
		layerClicked(upPoint[0], upPoint[1]);
		requestSystemBrowser(url);
		if (nativeAd!=null)
		{
			nativeAd.reportAdClick(null);
		}
	}

	@Override
	protected void calculateRequestSize()
	{
		
	}

	public class MyNativeAdListener implements NativeAdListener
	{

		@Override
		public void onUserLeftApplication(InMobiNative arg0)
		{
			
		}

		@Override
		public void onAdLoadSucceeded(InMobiNative inMobiNative)
		{
			try
			{
				YumiProviderBean provider = getProvider();
				provider.setUseTemplateMode("0");
				JSONObject content = new JSONObject((String) inMobiNative.getAdContent());
				String html = null;
				String banner_landingURL = content.getString("landingURL");
				setaTagUrl(banner_landingURL);
				String imageUrl = content.getJSONObject("screenshots").getString("url");
				if (imageUrl!=null && !"".equals(imageUrl) && !"null".equals(imageUrl))
				{
					html = NativeAdsBuild.getImageAdHtml(imageUrl, getaTagUrl());
				}else{
					String iconUrl = content.getJSONObject("icon").getString("url");
					String description = content.getString("description");
                    String title = content.getString("title");
					//html = NativeAdsBuild.getImageTextAdHtml(iconUrl,title, description, getaTagUrl(), getActivity());
                    html = NativeAdsBuild.getTemplateBanner(iconUrl,title, description, getaTagUrl(), getActivity(), getProvider());
				}
				if (html!=null && !"".equals(html) && !"null".equals(html))
				{
					ZplayDebug.d(TAG, "Inmobi native Banner request success!", onoff);
					calculateWebSize();
					createWebview(null);
					loadData(html);
				}else{
					layerPreparedFailed(LayerErrorCode.ERROR_INTERNAL);
					ZplayDebug.d(TAG, "Inmobi native Banner request failed!", onoff);
				}
			} catch (JSONException e)
			{
				ZplayDebug.d(TAG, "Inmobi native Banner request failed!!", onoff);
				layerPreparedFailed(LayerErrorCode.ERROR_INTERNAL);
				e.printStackTrace();
			}
		}

		@Override
		public void onAdLoadFailed(InMobiNative arg0, InMobiAdRequestStatus inMobiAdRequestStatus)
		{
			ZplayDebug.d(TAG, "Inmobi nativead request failed :" + inMobiAdRequestStatus.getMessage(), onoff);
			layerPreparedFailed(InmobiExtraHolder.decodeError(inMobiAdRequestStatus.getStatusCode()));
		}

		@Override
		public void onAdDisplayed(InMobiNative arg0)
		{
			
		}

		@Override
		public void onAdDismissed(InMobiNative arg0)
		{
			
		}
	}
	

}
