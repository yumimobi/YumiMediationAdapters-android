package com.yumi.android.sdk.ads.adapter.admob;

import android.app.Activity;
import android.view.ViewGroup;

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
import com.yumi.android.sdk.ads.formats.YumiNativeAdView;
import com.yumi.android.sdk.ads.formats.YumiNativeMappedImage;
import com.yumi.android.sdk.ads.publish.NativeContent;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerNativeAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import java.util.ArrayList;
import java.util.List;

import static com.yumi.android.sdk.ads.adapter.admob.AdMobUtil.recodeError;

public class AdmobNativeAdapter extends YumiCustomerNativeAdapter {

    private AdLoader adLoader;
    private List<NativeContent> list;

    protected AdmobNativeAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }

    @Override
    public void webLayerClickedAndRequestBrowser(String url) {

    }

    @Override
    protected void onPrepareNative() {
        if (adLoader != null) {
            adLoader.loadAds(new AdRequest.Builder().build(), 5);
        }
    }

    @Override
    protected void init() {
        list = new ArrayList<>();
        ZplayDebug.v(TAG, "admob native init getProvider().getKey1() : " + getProvider().getKey1(), onoff);
        VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(true).build();
        NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();
        AdLoader.Builder builder = new AdLoader.Builder(getActivity(), getProvider().getKey1());
        adLoader = builder.forUnifiedNativeAd(
                new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        ZplayDebug.v(TAG, "admob native isnotLoading", onoff);
                        final NativeAdContent nativeAdContent = new NativeAdContent(unifiedNativeAd);
                        list.add(nativeAdContent);
                        if (adLoader.isLoading()) {
                            return;
                        }
                        if (list.size() > 0) {
                            ZplayDebug.v(TAG, "YuminativeAdapter onSuccess", onoff);
                            layerPrepared(list);
                        } else {
                            ZplayDebug.v(TAG, "YuminativeAdapter onFailed", onoff);
                            layerPreparedFailed(LayerErrorCode.ERROR_NO_FILL);
                        }
                    }
                }).withAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                super.onAdClicked();
                ZplayDebug.d(TAG, "admob native onClick", onoff);
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
                ZplayDebug.d(TAG, "admob native failed errorCode=" + errorCode, onoff);
                layerPreparedFailed(recodeError(errorCode));
            }
        }).withNativeAdOptions(adOptions).build();
    }

    @Override
    protected void callOnActivityDestroy() {

    }

    @Override
    protected void onRequestNonResponse() {

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
                ZplayDebug.v(TAG, "Admob native getIcon()" + unifiedNativeAd.getIcon().getUri().toString(), onoff);
                setIcon(new YumiNativeMappedImage(unifiedNativeAd.getIcon().getDrawable(), unifiedNativeAd.getIcon().getUri().toString(), unifiedNativeAd.getIcon().getScale()));
            } else {
                setIcon(null);
            }
            if (unifiedNativeAd.getImages().size() > 0) {
                ZplayDebug.v(TAG, "Admob native getImages()" + unifiedNativeAd.getImages().get(0).getUri().toString(), onoff);
                setImage(new YumiNativeMappedImage(unifiedNativeAd.getImages().get(0).getDrawable(), unifiedNativeAd.getImages().get(0).getUri().toString(), unifiedNativeAd.getImages().get(0).getScale()));
            } else {
                setImage(null);
            }
            setTitle(unifiedNativeAd.getHeadline());
            setCallToAction(unifiedNativeAd.getCallToAction());
            setPrice(unifiedNativeAd.getPrice());
            setDesc(unifiedNativeAd.getBody());
            setExtras(unifiedNativeAd.getExtras());
            setStarRating(unifiedNativeAd.getStarRating());
        }


        public void trackView() {
            if (getNativeAdView() == null) {
                ZplayDebug.v(TAG, "admob native trackView getNativeAdView() is null", onoff);
                return;
            }
            YumiNativeAdView yumiNativeAdView = getNativeAdView();
            ViewGroup parent = (ViewGroup) yumiNativeAdView.getParent();
            parent.removeView(yumiNativeAdView);

            UnifiedNativeAdView unifiedAdView = new UnifiedNativeAdView(yumiNativeAdView.getContext());
            unifiedAdView.setHeadlineView(yumiNativeAdView.getHeadlineView());
            unifiedAdView.setBodyView(yumiNativeAdView.getBodyView());
            unifiedAdView.setIconView(yumiNativeAdView.getIconView());
            unifiedAdView.setImageView(yumiNativeAdView.getImageView());
            unifiedAdView.setCallToActionView(yumiNativeAdView.getCallToActionView());
            unifiedAdView.setPriceView(yumiNativeAdView.getPriceView());
            unifiedAdView.setStarRatingView(yumiNativeAdView.getStarRatingView());

            VideoController vc = unifiedNativeAd.getVideoController();
            ZplayDebug.v(TAG, "admob native VideoController.hasVideoContent() =" + vc.hasVideoContent(), onoff);
            if (yumiNativeAdView.getMediaLayout() != null) {
                MediaView mediaview = new MediaView(unifiedAdView.getContext());
                ((ViewGroup)yumiNativeAdView.getMediaLayout()).removeAllViews();
                ((ViewGroup)yumiNativeAdView.getMediaLayout()).addView(mediaview);
                unifiedAdView.setMediaView(mediaview);
            }

            unifiedAdView.removeAllViews();
            unifiedAdView.addView(yumiNativeAdView);
            parent.addView(unifiedAdView);
            unifiedAdView.setNativeAd(unifiedNativeAd);
        }

    }

}
