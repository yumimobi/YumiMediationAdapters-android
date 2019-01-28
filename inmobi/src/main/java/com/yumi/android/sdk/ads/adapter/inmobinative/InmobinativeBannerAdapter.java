package com.yumi.android.sdk.ads.adapter.inmobinative;

import android.app.Activity;
import android.view.View;

import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiNative;
import com.inmobi.ads.listeners.NativeAdEventListener;
import com.inmobi.sdk.InMobiSdk;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.NativeAdsBuild;
import com.yumi.android.sdk.ads.publish.nativead.YumiNativeBannerAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import org.json.JSONException;
import org.json.JSONObject;

import static com.yumi.android.sdk.ads.adapter.inmobi.InmobUtil.recodeError;
import static com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode.ERROR_INTERNAL;

public class InmobinativeBannerAdapter extends YumiNativeBannerAdapter {

    protected InmobinativeBannerAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }

    private static final String TAG = "InmobinativeBannerAdapter";

    private InMobiNative nativeAd;

    @Override
    protected void init() {
        String key1 = getProvider().getKey1();
        String key2 = getProvider().getKey2();
        ZplayDebug.d(TAG, "key1:" + key1, onoff);
        ZplayDebug.d(TAG, "key2:" + key2, onoff);
        if (nativeAd == null) {
            InMobiSdk.init(getActivity(), key1);
            nativeAd = new InMobiNative(getActivity(), Long.valueOf(key2), new MyNativeAdListener());
        }
    }

    @Override
    protected void onPrepareBannerLayer() {
        if (nativeAd != null) {
            ZplayDebug.d(TAG, "Inmobi native Banner request", onoff);
            nativeAd.load();
        }
    }

    @Override
    protected void webLayerPrepared(View view) {
        layerPrepared(view, false);
        layerExposure();
    }

    @Override
    protected void callOnActivityDestroy() {
        nativeAd.destroy();
    }

    @Override
    public void onActivityPause() {

    }

    @Override
    public void onActivityResume() {

    }

    @Override
    protected void webLayerClickedAndRequestBrowser(String url) {
        layerClicked(upPoint[0], upPoint[1]);
        requestSystemBrowser(url);
        if (nativeAd != null) {
            nativeAd.reportAdClickAndOpenLandingPage();
        }
    }

    @Override
    protected void calculateRequestSize() {

    }

    public class MyNativeAdListener extends NativeAdEventListener {

        @Override
        public void onAdLoadSucceeded(InMobiNative inMobiNative) {
            try {
                YumiProviderBean provider = getProvider();
                provider.setUseTemplateMode("0");
                JSONObject content = inMobiNative.getCustomAdContent();
                String html = null;
                String banner_landingURL = content.getString("landingURL");
                setaTagUrl(banner_landingURL);
                String imageUrl = content.getJSONObject("screenshots").getString("url");
                if (imageUrl != null && !"".equals(imageUrl) && !"null".equals(imageUrl)) {
                    html = NativeAdsBuild.getImageAdHtml(imageUrl, getaTagUrl());
                } else {
                    String iconUrl = content.getJSONObject("icon").getString("url");
                    String description = content.getString("description");
                    String title = content.getString("title");
                    //html = NativeAdsBuild.getImageTextAdHtml(iconUrl,title, description, getaTagUrl(), getActivity());
                    html = NativeAdsBuild.getTemplateBanner(iconUrl, title, description, getaTagUrl(), getActivity(), getProvider());
                }
                if (html != null && !"".equals(html) && !"null".equals(html)) {
                    ZplayDebug.d(TAG, "Inmobi native Banner request success!", onoff);
                    calculateWebSize();
                    createWebview(null);
                    loadData(html);
                } else {
                    layerPreparedFailed(recodeError(ERROR_INTERNAL));
                    ZplayDebug.d(TAG, "Inmobi native Banner request failed!", onoff);
                }
            } catch (JSONException e) {
                ZplayDebug.d(TAG, "Inmobi native Banner request failed!!", onoff);
                layerPreparedFailed(recodeError(ERROR_INTERNAL));
                e.printStackTrace();
            }
        }

        @Override
        public void onAdLoadFailed(InMobiNative arg0, InMobiAdRequestStatus inMobiAdRequestStatus) {
            ZplayDebug.d(TAG, "Inmobi nativead request failed :" + inMobiAdRequestStatus.getMessage(), onoff);
            layerPreparedFailed(recodeError(inMobiAdRequestStatus));
        }
    }


}
