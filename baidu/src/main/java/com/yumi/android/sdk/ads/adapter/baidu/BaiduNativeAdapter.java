package com.yumi.android.sdk.ads.adapter.baidu;

import android.app.Activity;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mobad.feeds.BaiduNative;
import com.baidu.mobad.feeds.NativeErrorCode;
import com.baidu.mobad.feeds.NativeResponse;
import com.baidu.mobads.AdView;
import com.baidu.mobads.component.XNativeView;
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

import static com.yumi.android.sdk.ads.adapter.baidu.BaiduUtil.recodeNativeError;
import static com.yumi.android.sdk.ads.utils.device.WindowSizeUtils.dip2px;
import static com.yumi.android.sdk.ads.utils.file.BitmapDownloadUtil.loadDrawables;

public class BaiduNativeAdapter extends YumiCustomerNativeAdapter {
    private BaiduNative baiduNative;

    protected BaiduNativeAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }

    @Override
    public void webLayerClickedAndRequestBrowser(String url) {

    }

    @Override
    protected void onPrepareNative() {
        if (baiduNative != null) {
            ZplayDebug.v(TAG, "baidu native onPrepareNative adCount: " + getCurrentPoolSpace(), onoff);
            baiduNative.makeRequest();
        }
    }

    @Override
    protected void init() {
        ZplayDebug.i(TAG, "baidu native key1 : " + getProvider().getKey1() + ",key2 :" + getProvider().getKey2(), onoff);
        AdView.setAppSid(getActivity(), getProvider().getKey1());
        createrListener();
    }

    private void createrListener() {
        baiduNative = new BaiduNative(getActivity(), getProvider().getKey2(), new BaiduNative.BaiduNativeNetworkListener() {
            @Override
            public void onNativeLoad(List<NativeResponse> list) {
                ZplayDebug.i(TAG, "baidu native onNativeLoad : " + list.size(), onoff);
                getNativeContentList(list);
            }

            @Override
            public void onNativeFail(NativeErrorCode nativeErrorCode) {
                ZplayDebug.i(TAG, "baidu native onNativeFail : " + nativeErrorCode.toString(), onoff);
                layerPreparedFailed(recodeNativeError(nativeErrorCode, nativeErrorCode.toString()));
            }
        });

    }

    private void getNativeContentList(final List<NativeResponse> baiduNativeAdlist) {
        List<NativeContent> nativeContentsList = new ArrayList<>();
        try {
            for (int i = 0; i < baiduNativeAdlist.size(); i++) {
                try {
                    final NativeResponse adEntity = baiduNativeAdlist.get(i);
                    final NativeAdContent nativeAdContent = new NativeAdContent(adEntity);
                    if (nativeAdContent.isValid()) {
                        nativeContentsList.add(nativeAdContent);
                    }
                } catch (Exception e) {
                    ZplayDebug.e(TAG, "Baidu data parse error : " + e, onoff);
                }
            }

            if (nativeContentsList.isEmpty()) {
                ZplayDebug.v(TAG, "baidu data is empty", onoff);
                layerPreparedFailed(recodeNativeError(NativeErrorCode.LOAD_AD_FAILED, "baidu ad is no fill"));
                return;
            }

            if (!getProvider().getNativeAdOptions().getIsDownloadImage()) {
                layerPrepared(nativeContentsList);
                return;
            }

            loadDrawables(getActivity(), nativeContentsList, new BitmapDownloadUtil.DownloadDrawableListener() {
                @Override
                public void onLoaded(List<NativeContent> data) {
                    layerPrepared(data);
                }

                @Override
                public void onFailed() {
                    layerPreparedFailed(recodeNativeError(NativeErrorCode.LOAD_AD_FAILED, "download image data failed"));
                }
            });
        } catch (Exception e) {
            ZplayDebug.e(TAG, "Baidu getNativeContentList error : " + e, onoff);
            layerPreparedFailed(recodeNativeError(NativeErrorCode.INTERNAL_ERROR, "get Native Content List error"));
        }
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


    class NativeAdContent extends NativeContent {

        private NativeResponse nativeAdData;
        private XNativeView xNativeView;

        NativeAdContent(NativeResponse nativeAdData) {
            BaiduNativeAdapter.NativeAdContent.this.nativeAdData = nativeAdData;

            setIcon(new Image(nativeAdData.getIconUrl()));
            setImage(new Image(nativeAdData.getImageUrl()));
            setDesc(nativeAdData.getDesc());
            setCallToAction(PhoneInfoGetter.getLanguage().startsWith("zh") ? "查看详情" : "learn more");
            setTitle(nativeAdData.getTitle());

            xNativeView = new XNativeView(getContext());
            xNativeView.setNativeItem(nativeAdData);
            ZplayDebug.v(TAG, "baidu native hasVideoContent :" + (nativeAdData.getMaterialType() == NativeResponse.MaterialType.VIDEO), onoff);
            setHasVideoContent(nativeAdData.getMaterialType() == NativeResponse.MaterialType.VIDEO);
            setNativeAdVideoController(new BaiduNativeAdVideoController(xNativeView));

            setMaterialCreationTime(System.currentTimeMillis());
            setMaterialEtime(getProvider().getMaterialEtime());
            setProviderName("Baidu");
        }

        public void trackView() {
            if (getNativeAdView() == null) {
                ZplayDebug.v(TAG, "baidu native trackView getNativeAdView() is null", onoff);
                return;
            }

            ImageView adLogo = new ImageView(getNativeAdView().getContext());
            Drawable zplayad_media_baidu_logo = ResFactory.getDrawableByAssets("zplayad_media_baidu_logo", getNativeAdView().getContext());
            adLogo.setBackground(zplayad_media_baidu_logo);
            getNativeAdView().addView(adLogo);
            FrameLayout.LayoutParams adLogoParams = new FrameLayout.LayoutParams(dip2px(getNativeAdView().getContext(), 20), dip2px(getNativeAdView().getContext(), 20));
            setViewPosition(adLogoParams, YumiNativeAdOptions.POSITION_BOTTOM_RIGHT);
            adLogo.setLayoutParams(adLogoParams);
            getNativeAdView().requestLayout();

            if (!getProvider().getNativeAdOptions().getHideAdAttribution()) {
                TextView adAttribution = new TextView(getNativeAdView().getContext());
                adAttribution.setText(getProvider().getNativeAdOptions().getAdAttributionText());
                adAttribution.setTextColor(getProvider().getNativeAdOptions().getAdAttributionColor());
                adAttribution.setTextSize(getProvider().getNativeAdOptions().getAdAttributionTextSize());
                adAttribution.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                getNativeAdView().addView(adAttribution);
                FrameLayout.LayoutParams adAttributionParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);

                setViewPosition(adAttributionParams, getProvider().getNativeAdOptions().getAdAttributionPosition());
                adAttribution.setLayoutParams(adAttributionParams);
                getNativeAdView().requestLayout();
            }

            if (getNativeAdView().getMediaLayout() != null && nativeAdData.getMaterialType() == NativeResponse.MaterialType.VIDEO) {
                ((ViewGroup) getNativeAdView().getMediaLayout()).removeAllViews();
                ((ViewGroup) getNativeAdView().getMediaLayout()).addView(xNativeView);
            }

            layerExposure();
            nativeAdData.recordImpression(getNativeAdView());
            getNativeAdView().setOnClickListener(new ViewGroup.OnClickListener() {
                @Override
                public void onClick(View v) {
                    layerClicked(-99f, -99f);
                    nativeAdData.handleClick(getNativeAdView());
                }
            });
            getNativeAdView().setClickable(true);
            if (getNativeAdView().getCallToActionView() != null) {
                getNativeAdView().getCallToActionView().setOnClickListener(new ViewGroup.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getNativeAdView().performClick();
                    }
                });
            }
        }

        public class BaiduNativeAdVideoController extends YumiNativeAdVideoController {
            private XNativeView xNativeView;

            private BaiduNativeAdVideoController(XNativeView xNativeView) {
                this.xNativeView = xNativeView;
            }

            @Override
            public void play() {
                if (xNativeView != null) {
                    xNativeView.render();
                }
            }

            @Override
            public void pause() {
                if (xNativeView != null) {
                    xNativeView.pause();
                }
            }

            @Override
            public double getAspectRatio() {
                return 0;
            }

            @Override
            public void setVideoLifecycleCallbacks(YumiVideoLifecycleCallbacks videoLifecycleCallbacks) {

            }
        }
    }

}
