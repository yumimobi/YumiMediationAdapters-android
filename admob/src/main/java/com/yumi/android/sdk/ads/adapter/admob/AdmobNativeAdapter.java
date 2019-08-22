package com.yumi.android.sdk.ads.adapter.admob;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.formats.YumiNativeAdVideoController;
import com.yumi.android.sdk.ads.formats.YumiNativeAdView;
import com.yumi.android.sdk.ads.formats.YumiNativeMappedImage;
import com.yumi.android.sdk.ads.publish.NativeContent;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerNativeAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import java.util.ArrayList;
import java.util.List;

import static com.yumi.android.sdk.ads.adapter.admob.AdMobUtil.getAdRequest;
import static com.yumi.android.sdk.ads.adapter.admob.AdMobUtil.recodeError;
import static com.yumi.android.sdk.ads.adapter.admob.AdMobUtil.sdkVersion;

public class AdmobNativeAdapter extends YumiCustomerNativeAdapter {

    private AdLoader adLoader;
    private List<NativeContent> list;
    private int adCount;

    protected AdmobNativeAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }

    @Override
    public void webLayerClickedAndRequestBrowser(String url) {

    }

    @Override
    protected void onPrepareNative() {
        if (adLoader != null) {
            int currentPoolSpace = getCurrentPoolSpace();
            adCount = currentPoolSpace >= 5 ? 5 : currentPoolSpace;
            ZplayDebug.v(TAG, "admob native onPrepareNative adCount: " + adCount, onoff);
            if (adCount <= 0) {
                // 如果 adCount <= 0，adLoader.loadAds(new AdRequest.Builder().build(), adCount) 在无 google service 设备上会崩溃
                // 理论上 adCount 不应该为 0，暂时这样处理
                adCount = 1;
            }
            adLoader.loadAds(getAdRequest(getContext()), adCount);
        }
    }

    @Override
    protected void init() {
        list = new ArrayList<>();
        ZplayDebug.v(TAG, "admob native init getProvider().getKey1() : " + getProvider().getKey1(), onoff);
        VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(true).build();
        NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions)
                .setAdChoicesPlacement(getProvider().getNativeAdOptions().getAdChoicesPosition())
                .setReturnUrlsForImageAssets(!getProvider().getNativeAdOptions().getIsDownloadImage()).build();
        AdLoader.Builder builder = new AdLoader.Builder(getActivity(), getProvider().getKey1());
        adLoader = builder.forUnifiedNativeAd(
                new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        ZplayDebug.v(TAG, "admob native onUnifiedNativeAdLoaded isLoading" + adLoader.isLoading(), onoff);
                        try {
                            adCount--;
                            final NativeAdContent nativeAdContent = new NativeAdContent(unifiedNativeAd);
                            list.add(nativeAdContent);
                            if (adCount != 0) {
                                return;
                            }
                            if (list.size() > 0) {
                                ZplayDebug.v(TAG, "admob native Adapter onSuccess", onoff);
                                layerPrepared(list);
                            } else {
                                ZplayDebug.v(TAG, "admob native Adapter onFailed", onoff);
                                layerPreparedFailed(recodeError(AdRequest.ERROR_CODE_NO_FILL));
                            }

                        } catch (Exception e) {
                            ZplayDebug.e(TAG, "admob getNativeContentList error : " + e, onoff);
                        }
                    }
                }).withAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                ZplayDebug.d(TAG, "admob native onClick", onoff);
                super.onAdClicked();
                layerClicked(-99f, -99f);
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
                layerExposure();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                adCount--;
                ZplayDebug.v(TAG, "admob native Adapter onAdFailedToLoad isLoading()" + adLoader.isLoading() + ", errorCode=" + errorCode, onoff);
                layerPreparedFailed(recodeError(errorCode));
            }
        }).withNativeAdOptions(adOptions).build();
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

        private UnifiedNativeAd unifiedNativeAd;

        NativeAdContent(UnifiedNativeAd unifiedNativeAd) {
            NativeAdContent.this.unifiedNativeAd = unifiedNativeAd;
            if (unifiedNativeAd.getIcon() != null) {
                setIcon(new YumiNativeMappedImage(unifiedNativeAd.getIcon().getDrawable(), unifiedNativeAd.getIcon().getUri().toString(), unifiedNativeAd.getIcon().getScale()));
            } else {
                setIcon(null);
            }
            if (unifiedNativeAd.getImages().size() > 0) {
                setCoverImage(new YumiNativeMappedImage(unifiedNativeAd.getImages().get(0).getDrawable(), unifiedNativeAd.getImages().get(0).getUri().toString(), unifiedNativeAd.getImages().get(0).getScale()));
            } else {
                setCoverImage(null);
            }

            setTitle(unifiedNativeAd.getHeadline());
            setCallToAction(unifiedNativeAd.getCallToAction());
            setPrice(unifiedNativeAd.getPrice());
            setDesc(unifiedNativeAd.getBody());
            setStarRating(unifiedNativeAd.getStarRating());
            setHasVideoContent(unifiedNativeAd.getVideoController().hasVideoContent());
            ZplayDebug.v(TAG, "admob native hasVideoContent() =" + unifiedNativeAd.getVideoController().hasVideoContent(), onoff);
            setNativeAdVideoController(new AdmobNativeViewController(unifiedNativeAd.getVideoController()));

            setMaterialCreationTime(System.currentTimeMillis());
            setMaterialEtime(getProvider().getMaterialEtime());
            setProviderName(getProvider().getProviderName());
            setSpecifiedProvider(getProvider().getSpecifiedProvider());
            setIsExpressAdView(false);
        }
        @Override
        public void trackView() {
            if (getNativeAdView() == null) {
                ZplayDebug.v(TAG, "admob native trackView getNativeAdView() is null", onoff);
                return;
            }
            YumiNativeAdView yumiNativeAdView = getNativeAdView();
            ViewGroup parent = (ViewGroup) yumiNativeAdView.getParent();
            parent.removeView(yumiNativeAdView);

            UnifiedNativeAdView unifiedAdView = new UnifiedNativeAdView(yumiNativeAdView.getContext());
            unifiedAdView.setHeadlineView(yumiNativeAdView.getTitleView());
            unifiedAdView.setBodyView(yumiNativeAdView.getDescView());
            unifiedAdView.setIconView(yumiNativeAdView.getIconView());
            unifiedAdView.setImageView(yumiNativeAdView.getCoverImageView());
            unifiedAdView.setCallToActionView(yumiNativeAdView.getCallToActionView());
            unifiedAdView.setPriceView(yumiNativeAdView.getPriceView());
            unifiedAdView.setStarRatingView(yumiNativeAdView.getStarRatingView());

            if (yumiNativeAdView.getMediaLayout() != null) {
                MediaView mediaview = new MediaView(unifiedAdView.getContext());
                ((ViewGroup) yumiNativeAdView.getMediaLayout()).removeAllViews();
                ((ViewGroup) yumiNativeAdView.getMediaLayout()).addView(mediaview);
                unifiedAdView.setMediaView(mediaview);
            }

            if (!getProvider().getNativeAdOptions().getHideAdAttribution()) {
                TextView adAttribution = new TextView(getNativeAdView().getContext());
                adAttribution.setText(getProvider().getNativeAdOptions().getAdAttributionText());
                adAttribution.setTextColor(getProvider().getNativeAdOptions().getAdAttributionTextColor());
                adAttribution.setBackgroundColor(getProvider().getNativeAdOptions().getAdAttributionBackgroundColor());
                adAttribution.setTextSize(getProvider().getNativeAdOptions().getAdAttributionTextSize());
                adAttribution.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                yumiNativeAdView.addView(adAttribution);
                FrameLayout.LayoutParams adAttributionParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);

                setViewPosition(adAttributionParams, getProvider().getNativeAdOptions().getAdAttributionPosition());
                adAttribution.setLayoutParams(adAttributionParams);
                yumiNativeAdView.requestLayout();
            }

            unifiedAdView.removeAllViews();
            unifiedAdView.addView(yumiNativeAdView);
            parent.addView(unifiedAdView);
            unifiedAdView.setNativeAd(unifiedNativeAd);
        }


        @Override
        public void destroy() {
            ZplayDebug.v(TAG, "admob native destory", onoff);
            if(unifiedNativeAd != null){
                unifiedNativeAd.destroy();
            }
        }

        public class AdmobNativeViewController extends YumiNativeAdVideoController {
            private VideoController vc;

            private AdmobNativeViewController(VideoController vc) {
                this.vc = vc;
            }

            @Override
            public void play() {
                if (vc != null) {
                    vc.play();
                }
            }

            @Override
            public void pause() {
                if (vc != null) {
                    vc.pause();
                }
            }

            @Override
            public double getAspectRatio() {
                if (vc != null) {
                    return vc.getAspectRatio();
                }
                return 0;
            }

            @Override
            public void setVideoLifecycleCallbacks(final YumiVideoLifecycleCallbacks videoLifecycleCallbacks) {
                if (vc != null) {
                    vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                        @Override
                        public void onVideoPlay() {
                            super.onVideoPlay();
                            if (videoLifecycleCallbacks != null) {
                                videoLifecycleCallbacks.onVideoPlay();
                            }
                        }

                        @Override
                        public void onVideoPause() {
                            super.onVideoPause();
                            if (videoLifecycleCallbacks != null) {
                                videoLifecycleCallbacks.onVideoPause();
                            }
                        }

                        @Override
                        public void onVideoEnd() {
                            super.onVideoEnd();
                            if (videoLifecycleCallbacks != null) {
                                videoLifecycleCallbacks.onVideoEnd();
                            }
                        }
                    });
                }
            }

        }
    }

    @Override
    public String getProviderVersion() {
        return sdkVersion();
    }
}
