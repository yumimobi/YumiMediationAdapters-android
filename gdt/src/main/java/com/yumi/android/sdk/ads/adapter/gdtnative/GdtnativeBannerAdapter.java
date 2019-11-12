package com.yumi.android.sdk.ads.adapter.gdtnative;

import android.app.Activity;
import android.view.View;

import com.qq.e.ads.nativ.NativeAD;
import com.qq.e.ads.nativ.NativeAD.NativeAdListener;
import com.qq.e.ads.nativ.NativeADDataRef;
import com.qq.e.comm.util.AdError;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.NativeAdsBuild;
import com.yumi.android.sdk.ads.publish.nativead.YumiNativeBannerAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import java.util.List;

import static com.yumi.android.sdk.ads.adapter.GdtUtil.recodeError;
import static com.yumi.android.sdk.ads.adapter.GdtUtil.sdkVersion;
import static com.yumi.android.sdk.ads.publish.enumbean.AdSize.BANNER_SIZE_SMART;

public class GdtnativeBannerAdapter extends YumiNativeBannerAdapter {

    private static final String TAG = "GdtnativeBannerAdapter";
    private NativeAD nativeAD;
    private NativeADDataRef adItem;
    private String html;
    private View bannerView;

    protected GdtnativeBannerAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }

    @Override
    public void onActivityPause() {
    }

    @Override
    public void onActivityResume() {

    }

    @Override
    protected void webLayerClickedAndRequestBrowser(String url) {
        ZplayDebug.d(TAG, "banner clicked");
        layerClicked(upPoint[0], upPoint[1]);
        if (adItem != null) {
            adItem.onClicked(this.bannerView);
        }
    }

    @Override
    protected void webLayerPrepared(View view) {
        ZplayDebug.d(TAG, "banner prepared");
        this.bannerView = view;
        layerPrepared(view, false);
        adItem.onExposured(view);
    }

    @Override
    protected void calculateRequestSize() {

    }

    @Override
    protected void onPrepareBannerLayer() {
        if (bannerSize == BANNER_SIZE_SMART) {
            ZplayDebug.d(TAG, "not support smart banner");
            layerPreparedFailed(recodeError(new AdError(5004, null), "not support smart banner."));
            return;
        }

        if (nativeAD != null) {
            ZplayDebug.d(TAG, "load new banner");
            nativeAD.loadAD(1);
        }
    }

    @Override
    protected void init() {
        ZplayDebug.d(TAG, "appId : " + getProvider().getKey1() + " ,pId : " + getProvider().getKey2());
        if (nativeAD == null) {
            nativeAD = new NativeAD(getActivity(), getProvider().getKey1(), getProvider().getKey2(), new MyNativeAdListener());
        }
    }

    private class MyNativeAdListener implements NativeAdListener {

        @Override
        public void onNoAD(AdError adError) {
            if (adError == null) {
                ZplayDebug.d(TAG, "onNoAD adError = null");
                layerPreparedFailed(recodeError(null));
                return;
            }
            ZplayDebug.d(TAG, "onNoAD ErrorCode:" + adError.getErrorCode() + " msg:" + adError.getErrorMsg());
            layerPreparedFailed(recodeError(adError));
        }

        @Override
        public void onADError(NativeADDataRef nativeADDataRef, AdError adError) {
            if (adError == null) {
                ZplayDebug.d(TAG, "onADError adError = null");
                layerPreparedFailed(recodeError(null));
                return;
            }
            ZplayDebug.d(TAG, "onADError ErrorCode:" + adError.getErrorCode() + " msg:" + adError.getErrorMsg());
            layerPreparedFailed(recodeError(adError));
        }

        @Override
        public void onADStatusChanged(NativeADDataRef arg0) {
            ZplayDebug.d(TAG, "onADStatusChanged");
        }

        @Override
        public void onADLoaded(List<NativeADDataRef> arg0) {
            if (arg0.size() > 0) {
                getProvider().setUseTemplateMode("0");
                adItem = arg0.get(0);
                //html = NativeAdsBuild.getImageTextAdHtml(adItem.getIconUrl(), adItem.getTitle(), adItem.getDesc(), getaTagUrl(), getActivity());
                html = NativeAdsBuild.getTemplateBanner(adItem.getIconUrl(), adItem.getTitle(), adItem.getDesc(), getaTagUrl(), getActivity(), getProvider());
                ZplayDebug.d(TAG, "request success!");
                if (html != null && !"".equals(html) && !"null".equals(html)) {
                    calculateWebSize();
                    createWebview(null);
                    loadData(html);
                } else {
                    layerPreparedFailed(recodeError(null));
                    ZplayDebug.d(TAG, "PreparedFailed ERROR_NO_FILL");
                }
            } else {
                layerPreparedFailed(recodeError(null));
                ZplayDebug.d(TAG, "PreparedFailed");
            }
        }


    }

    @Override
    public String getProviderVersion() {
        return sdkVersion();
    }
}
