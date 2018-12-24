package com.yumi.android.sdk.ads.adapter.inmobinative;

import android.app.Activity;
import android.webkit.WebView;

import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiAdRequestStatus.StatusCode;
import com.inmobi.ads.InMobiNative;
import com.inmobi.ads.listeners.NativeAdEventListener;
import com.inmobi.sdk.InMobiSdk;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.NativeAdsBuild;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.publish.nativead.YumiNativeIntersititalAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import org.json.JSONException;
import org.json.JSONObject;

public class InmobinativeInterstitialAdapter extends YumiNativeIntersititalAdapter {

    private static final String TAG = "InmobinativeInterstitialAdapter";

    private InMobiNative nativeAd;
    private WebView webView;

    protected InmobinativeInterstitialAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }

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
    protected void onPreparedWebInterstitial() {
        if (nativeAd != null) {
            nativeAd.load();
        }
    }


    @Override
    protected void webLayerPrepared(WebView view) {
        this.webView = view;
        layerPrepared();
    }


    @Override
    protected void webLayerOnShow() {
        layerExposure();
    }


    @Override
    protected void webLayerDismiss() {
        layerClosed();
        nativeAd.destroy();
    }


    @Override
    protected void callOnActivityDestroy() {

    }


    @Override
    public void onActivityPause() {

    }


    @Override
    public void onActivityResume() {
        closeOnResume();
    }


    @Override
    public boolean onActivityBackPressed() {
        return false;
    }


    @Override
    protected void webLayerClickedAndRequestBrowser(String url) {
        requestSystemBrowser(url);
        layerClicked(upPoint[0], upPoint[1]);
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
                getProvider().setUseTemplateMode("0");
                JSONObject content = inMobiNative.getCustomAdContent();

                String imageUrl = content.getJSONObject("screenshots").getString("url");
                String interstitial_landingURL = content.getString("landingURL");
                setaTagUrl(interstitial_landingURL);
                String interstitial_html = NativeAdsBuild.getImageAdHtml(imageUrl, getaTagUrl());
                int interstitial_width = content.getJSONObject("screenshots").getInt("width");
                int interstitial_height = content.getJSONObject("screenshots").getInt("height");
                calculateWebSize(interstitial_width, interstitial_height);

                if (interstitial_html != null && !"".equals(interstitial_html) && !"null".equals(interstitial_html)) {
                    createWebview(null);
                    loadData(interstitial_html);
                } else {
                    layerPreparedFailed(LayerErrorCode.ERROR_INTERNAL);
                }
            } catch (JSONException e) {
                layerPreparedFailed(LayerErrorCode.ERROR_INTERNAL);
                e.printStackTrace();
            }
        }

        @Override
        public void onAdLoadFailed(InMobiNative arg0, InMobiAdRequestStatus inMobiAdRequestStatus) {
            ZplayDebug.d(TAG, "Inmobi nativead request failed :" + inMobiAdRequestStatus.getMessage(), onoff);
            StatusCode statusCode = inMobiAdRequestStatus.getStatusCode();
            if (statusCode == StatusCode.NO_FILL) {
                layerPreparedFailed(LayerErrorCode.ERROR_NO_FILL);
            } else {
                layerPreparedFailed(LayerErrorCode.ERROR_INTERNAL);
            }
        }
    }
}
