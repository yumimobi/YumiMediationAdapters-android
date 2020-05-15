package com.yumi.android.sdk.ads.adapter.gdtnative;

import android.app.Activity;
import android.view.View;

import com.qq.e.ads.nativ.NativeADEventListener;
import com.qq.e.ads.nativ.NativeADUnifiedListener;
import com.qq.e.ads.nativ.NativeUnifiedADData;
import com.qq.e.ads.nativ.widget.NativeAdContainer;
import com.qq.e.comm.managers.GDTADManager;
import com.qq.e.comm.util.AdError;
import com.yumi.android.sdk.ads.adapter.gdtmob.GdtmobNativeHolder;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.NativeAdsBuild;
import com.yumi.android.sdk.ads.publish.nativead.YumiNativeBannerAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import java.util.ArrayList;
import java.util.List;

import static com.yumi.android.sdk.ads.adapter.GdtUtil.recodeError;
import static com.yumi.android.sdk.ads.adapter.GdtUtil.sdkVersion;
import static com.yumi.android.sdk.ads.publish.enumbean.AdSize.BANNER_SIZE_SMART;

public class GdtnativeBannerAdapter extends YumiNativeBannerAdapter {

    private static final String TAG = "GdtnativeBannerAdapter";
    private NativeUnifiedADData adItem;
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

    }

    @Override
    protected void webLayerPrepared(View view) {
        ZplayDebug.d(TAG, "banner prepared");

        NativeAdContainer nativeAdContainer = new NativeAdContainer(getActivity());
        nativeAdContainer.removeAllViews();
        nativeAdContainer.addView(view);

        this.bannerView = nativeAdContainer;

        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(view);
        adItem.bindAdToView(getActivity(), nativeAdContainer, null, clickableViews);

        adItem.setNativeAdEventListener(new NativeADEventListener() {
            @Override
            public void onADExposed() {
                ZplayDebug.v(TAG, "gdt native banner Adapter onADExposed");
            }

            @Override
            public void onADClicked() {
                ZplayDebug.v(TAG, "gdt native banner Adapter onADClicked");
                layerClicked(upPoint[0], upPoint[1]);
            }

            @Override
            public void onADError(AdError adError) {
                ZplayDebug.v(TAG, "gdt native banner Adapter onADError" + adError.getErrorMsg());
            }

            @Override
            public void onADStatusChanged() {
                ZplayDebug.v(TAG, "gdt native banner Adapter onADStatusChanged");
            }
        });

        layerPrepared(nativeAdContainer, false);
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

        ZplayDebug.d(TAG, "load new banner");
        GdtmobNativeHolder.getInstance().loadNativeUnifiedAD(1);
    }

    @Override
    protected void init() {
        ZplayDebug.d(TAG, "appId : " + getProvider().getKey1() + " ,pId : " + getProvider().getKey2());
        GDTADManager.getInstance().initWith(getContext(), getProvider().getKey1());
        NativeADUnifiedListener nativeADUnifiedListener = new NativeADUnifiedListener() {
            @Override
            public void onADLoaded(List<NativeUnifiedADData> adlist) {
                ZplayDebug.v(TAG, "onADLoaded");
                if (adlist.size() > 0) {
                    getProvider().setUseTemplateMode("0");
                    adItem = adlist.get(0);
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
        };
        GdtmobNativeHolder.getInstance().initNativeUnifiedAD(getActivity(), getProvider().getKey2(), nativeADUnifiedListener);

    }

    @Override
    public String getProviderVersion() {
        return sdkVersion();
    }
}
