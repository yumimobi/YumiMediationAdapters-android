package com.yumi.android.sdk.ads.adapter.gdtmob;

import android.app.Activity;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.qq.e.ads.nativ.NativeAD;
import com.qq.e.ads.nativ.NativeAD.NativeAdListener;
import com.qq.e.ads.nativ.NativeADDataRef;
import com.qq.e.comm.util.AdError;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.formats.YumiNativeAdOptions;
import com.yumi.android.sdk.ads.formats.YumiNativeAdVideoController;
import com.yumi.android.sdk.ads.publish.NativeContent;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerNativeAdapter;
import com.yumi.android.sdk.ads.self.ui.ResFactory;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.device.PhoneInfoGetter;
import com.yumi.android.sdk.ads.utils.file.BitmapDownloadUtil;

import java.util.ArrayList;
import java.util.List;

import static com.yumi.android.sdk.ads.adapter.GdtUtil.recodeError;
import static com.yumi.android.sdk.ads.utils.device.WindowSizeUtils.dip2px;
import static com.yumi.android.sdk.ads.utils.file.BitmapDownloadUtil.loadDrawables;

/**
 * Created by Administrator on 2017/7/3.
 */
public class GdtmobNativeAdapter extends YumiCustomerNativeAdapter {

    private NativeAD nativeAD;

    protected GdtmobNativeAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }

    @Override
    public void webLayerClickedAndRequestBrowser(String s) {

    }

    @Override
    protected void onPrepareNative() {
        if (nativeAD != null) {
            int currentPoolSpace = getCurrentPoolSpace();
            ZplayDebug.v(TAG, "Gdt native Adapter invoke onPrepareNative adCount=" + getCurrentPoolSpace(), onoff);
            nativeAD.loadAD(currentPoolSpace);
        }
    }

    @Override
    protected void init() {
        ZplayDebug.v(TAG, "Gdt native Adapter init key1 = " + getProvider().getKey1() + ", key2 = " + getProvider().getKey2(), onoff);
        nativeAD = new NativeAD(getActivity(), getProvider().getKey1(), getProvider().getKey2(), new NativeAdListener() {
            @Override
            public void onADStatusChanged(NativeADDataRef arg0) {
            }

            @Override
            public void onADLoaded(List<NativeADDataRef> arg0) {
                ZplayDebug.v(TAG, "onADLoaded", onoff);
                final List<NativeContent> list = new ArrayList<>();
                for (final NativeADDataRef item : arg0) {
                    try{
                        final NativeAdContent content = new NativeAdContent(item);
                        if (content.isValid()) {
                            list.add(content);
                        }
                    }catch (Exception e) {
                        ZplayDebug.e(TAG, "gdt data parse error : " + e, onoff);
                    }
                }
                if (list.isEmpty()) {
                    ZplayDebug.v(TAG, "gdt data is empty", onoff);
                    layerPreparedFailed(recodeError(new AdError(-1, "got gdt native ad, but the ad ")));
                    return;
                }

                if (!getProvider().getNativeAdOptions().getIsDownloadImage()) {
                    layerPrepared(list);
                    return;
                }

                loadDrawables(getActivity(), list, new BitmapDownloadUtil.DownloadDrawableListener() {
                    @Override
                    public void onLoaded(List<NativeContent> data) {
                        layerPrepared(data);
                    }

                    @Override
                    public void onFailed() {
                        layerPreparedFailed(recodeError(new AdError(-1, "got gdt native ad, but cannot download image data")));
                    }
                });

            }

            @Override
            public void onNoAD(AdError adError) {
                if (adError == null) {
                    ZplayDebug.d(TAG, "GDT nativead onNoAD adError = null", onoff);
                    layerPreparedFailed(recodeError(null));
                    return;
                }
                ZplayDebug.w(TAG, "GDT nativead onNoAD ErrorCode:" + adError.getErrorCode() + " msg:" + adError.getErrorMsg(), onoff);
                layerPreparedFailed(recodeError(adError));
            }

            @Override
            public void onADError(NativeADDataRef nativeADDataRef, AdError adError) {
                if (adError == null) {
                    ZplayDebug.d(TAG, "GDT nativead onADError adError = null", onoff);
                    layerPreparedFailed(recodeError(null));
                    return;
                }
                ZplayDebug.d(TAG, "GDT nativead onADError ErrorCode:" + adError.getErrorCode() + " msg:" + adError.getErrorMsg(), onoff);
                layerPreparedFailed(recodeError(adError));
            }
        });
    }


    @Override
    protected void callOnActivityDestroy() {

    }

    @Override
    public void onActivityPause() {

    }

    @Override
    public void onActivityResume() {

    }

    @Override
    public boolean onActivityBackPressed() {
        return false;
    }

    private class NativeAdContent extends NativeContent {

        private NativeADDataRef mGdtData;

        private NativeAdContent(NativeADDataRef gdtData) {
            mGdtData = gdtData;
            setTitle(gdtData.getTitle());
            setDesc(gdtData.getDesc());
            setStarRating((double) gdtData.getAPPScore());
            setImage(new Image(gdtData.getImgUrl()));
            setIcon(new Image(gdtData.getIconUrl()));
            setCallToAction(PhoneInfoGetter.getLanguage().startsWith("zh") ? "查看详情" : "learn more");

            setMaterialCreationTime(System.currentTimeMillis());
            setMaterialEtime(getProvider().getMaterialEtime());
            setNativeAdVideoController(new YumiNativeAdVideoController());
            setProviderName("Gdt");
        }

        @Override
        public void trackView() {
            if (getNativeAdView() == null) {
                ZplayDebug.v(TAG, "GDT native trackView getNativeAdView() is null", onoff);
                return;
            }

            ImageView adLogo = new ImageView(getNativeAdView().getContext());
            Drawable zplayad_media_gdt_logo = ResFactory.getDrawableByAssets("zplayad_media_gdt_logo", getNativeAdView().getContext());
            adLogo.setBackground(zplayad_media_gdt_logo);
            getNativeAdView().addView(adLogo);
            FrameLayout.LayoutParams adLogoParams = new FrameLayout.LayoutParams(dip2px(getNativeAdView().getContext(), 20), dip2px(getNativeAdView().getContext(), 20));
            setViewPosition(adLogoParams, YumiNativeAdOptions.POSITION_BOTTOM_RIGHT);
            adLogo.setLayoutParams(adLogoParams);
            getNativeAdView().requestLayout();

            if (!getProvider().getNativeAdOptions().getHideAdAttribution()) {
                TextView adAttribution = new TextView(getNativeAdView().getContext());
                adAttribution.setText(getProvider().getNativeAdOptions().getAdAttributionText());
                adAttribution.setTextColor(getProvider().getNativeAdOptions().getAdAttributionTextColor());
                adAttribution.setBackgroundColor(getProvider().getNativeAdOptions().getAdAttributionBackgroundColor());
                adAttribution.setTextSize(getProvider().getNativeAdOptions().getAdAttributionTextSize());
                adAttribution.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                getNativeAdView().addView(adAttribution);
                FrameLayout.LayoutParams adAttributionParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);

                setViewPosition(adAttributionParams, getProvider().getNativeAdOptions().getAdAttributionPosition());
                adAttribution.setLayoutParams(adAttributionParams);
                getNativeAdView().requestLayout();
            }

            mGdtData.onExposured(getNativeAdView());
            layerExposure();

            getNativeAdView().setClickable(true);
            getNativeAdView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    layerClicked(-99, -99);
                    mGdtData.onClicked(v);
                }
            });
            if (getNativeAdView().getCallToActionView() != null) {
                getNativeAdView().getCallToActionView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getNativeAdView().performClick();
                    }
                });
            }
        }
    }
}
