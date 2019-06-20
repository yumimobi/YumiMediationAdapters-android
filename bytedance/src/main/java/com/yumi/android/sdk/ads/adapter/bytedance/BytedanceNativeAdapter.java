package com.yumi.android.sdk.ads.adapter.bytedance;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.bytedance.sdk.openadsdk.TTNativeAd;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.formats.YumiNativeAdOptions;
import com.yumi.android.sdk.ads.formats.YumiNativeAdView;
import com.yumi.android.sdk.ads.publish.NativeContent;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerNativeAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.device.PhoneInfoGetter;
import com.yumi.android.sdk.ads.utils.file.BitmapDownloadUtil;

import java.util.ArrayList;
import java.util.List;

import static com.yumi.android.sdk.ads.adapter.bytedance.BytedanceUtil.getAppName;
import static com.yumi.android.sdk.ads.adapter.bytedance.BytedanceUtil.recodeError;
import static com.yumi.android.sdk.ads.adapter.bytedance.BytedanceUtil.recodeNativeAdError;
import static com.yumi.android.sdk.ads.utils.device.WindowSizeUtils.dip2px;
import static com.yumi.android.sdk.ads.utils.file.BitmapDownloadUtil.loadDrawables;

public class BytedanceNativeAdapter extends YumiCustomerNativeAdapter {

    private TTAdNative mTTAdNative;
    private TTAdNative.FeedAdListener loadAdListener;

    protected BytedanceNativeAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }

    @Override
    public void webLayerClickedAndRequestBrowser(String url) {

    }

    @Override
    protected void onPrepareNative() {
        ZplayDebug.d(TAG, "Bytedance request new native", onoff);
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(getProvider().getKey2())
                .setSupportDeepLink(true)
                .setImageAcceptedSize(640, 320)
                .setAdCount(getCurrentPoolSpace()) //请求广告数量为1到3条
                .build();
        mTTAdNative.loadFeedAd(adSlot, loadAdListener);
    }

    @Override
    protected void init() {
        ZplayDebug.d(TAG, "bytedance native init key1: " + getProvider().getKey1() + "key2: " + getProvider().getKey2(), onoff);

        TTAdSdk.init(getActivity(),
                new TTAdConfig.Builder()
                        .appId(getProvider().getKey1())
                        .useTextureView(false)
                        .appName(getAppName(getActivity().getPackageManager(), getActivity().getPackageName()))
                        .titleBarTheme(TTAdConstant.TITLE_BAR_THEME_DARK)
                        .allowShowNotify(false)
                        .allowShowPageWhenScreenLock(false)
                        .debug(false)
                        .directDownloadNetworkType(TTAdConstant.NETWORK_STATE_WIFI, TTAdConstant.NETWORK_STATE_3G)
                        .supportMultiProcess(false)
                        .build());
        mTTAdNative = TTAdSdk.getAdManager().createAdNative(getActivity());//baseContext建议为activity
        createrListener();
    }

    private void createrListener() {
        loadAdListener = new TTAdNative.FeedAdListener() {
            @Override
            public void onError(int code, String message) {
                ZplayDebug.d(TAG, "Bytedance native onError" + message, onoff);
                layerPreparedFailed(recodeError(code, message));
            }

            @Override
            public void onFeedAdLoad(List<TTFeedAd> ads) {
                if (ads == null || ads.isEmpty()) {
                    return;
                }
                ZplayDebug.d(TAG, "Bytedance native onFeedAdLoad", onoff);
                getNativeContentList(ads);

            }
        };
    }

    private void getNativeContentList(final List<TTFeedAd> bytedanceFeedAdList) {
        List<NativeContent> nativeContentsList = new ArrayList<>();
        try {
            for (int i = 0; i < bytedanceFeedAdList.size(); i++) {
                try {
                    final TTFeedAd adEntity = bytedanceFeedAdList.get(i);
                    final NativeAdContent nativeAdContent = new NativeAdContent(adEntity);
                    if (nativeAdContent.isValid()) {
                        nativeContentsList.add(nativeAdContent);
                    }
                } catch (Exception e) {
                    ZplayDebug.e(TAG, "bytedance data parse error : " + e, onoff);
                }
            }

            if (nativeContentsList.isEmpty()) {
                ZplayDebug.v(TAG, "bytedance data is empty", onoff);
                layerPreparedFailed(recodeNativeAdError(0, "bytedance ad is no fill"));
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
                    layerPreparedFailed(recodeNativeAdError(0, "download image data failed"));
                }
            });
        } catch (Exception e) {
            ZplayDebug.e(TAG, "bytedance getNativeContentList error : " + e, onoff);
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

        private TTFeedAd nativeAdData;

        NativeAdContent(TTFeedAd nativeAdData) {

            setTitle(nativeAdData.getTitle());
            BytedanceNativeAdapter.NativeAdContent.this.nativeAdData = nativeAdData;
            if (nativeAdData.getIcon().isValid()) {
                setIcon(new Image(nativeAdData.getIcon().getImageUrl()));
            }

            if (nativeAdData.getImageList().size() > 0 && nativeAdData.getImageList().get(0).isValid()) {
                setCoverImage(new Image(nativeAdData.getImageList().get(0).getImageUrl()));
            }

            setDesc(nativeAdData.getDescription());
            setCallToAction(PhoneInfoGetter.getLanguage().startsWith("zh") ? "查看详情" : "learn more");
            setHasVideoContent(nativeAdData.getImageMode() == 5);

            setMaterialCreationTime(System.currentTimeMillis());
            setMaterialEtime(getProvider().getMaterialEtime());
            setProviderName("Bytedance");
        }

        public void trackView() {
            if (getNativeAdView() == null) {
                ZplayDebug.v(TAG, "baidu native trackView getNativeAdView() is null", onoff);
                return;
            }

            YumiNativeAdView overlayView = getNativeAdView();

            ImageView adLogo = new ImageView(getNativeAdView().getContext());
            adLogo.setImageBitmap(nativeAdData.getAdLogo());
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

            //可以被点击的view, 也可以把convertView放进来意味item可被点击
            List<View> clickViewList = new ArrayList<>();
            clickViewList.add(overlayView);
            if (overlayView.getCallToActionView() != null) {
                clickViewList.add(overlayView.getCallToActionView());
            }

            nativeAdData.registerViewForInteraction((ViewGroup) overlayView, clickViewList, null, new TTNativeAd.AdInteractionListener() {
                @Override
                public void onAdClicked(View view, TTNativeAd ad) {
                    layerClicked(-99f, -99f);
                }

                @Override
                public void onAdCreativeClick(View view, TTNativeAd ad) {
                    layerClicked(-99f, -99f);
                }

                @Override
                public void onAdShow(TTNativeAd ad) {
                    layerExposure();
                }
            });

            if (overlayView.getMediaLayout() != null) {
                //获取视频播放view,该view SDK内部渲染，在媒体平台可配置视频是否自动播放等设置。
                View video = nativeAdData.getAdView();
                if (video != null) {
                    if (video.getParent() == null) {
                        ((ViewGroup) overlayView.getMediaLayout()).removeAllViews();
                        ((ViewGroup) overlayView.getMediaLayout()).addView(video);
                    }
                }
            }
        }
    }
}
