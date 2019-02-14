package com.yumi.android.sdk.ads.natives.adapter.admob;

import android.app.Activity;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.VideoOptions;
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
                        nativeAdContent.nativeAd(new NativeAdListener() {
                            @Override
                            public void onMappingSuccess() {
                                ZplayDebug.v(TAG, "admob native onMappingSuccess : ", onoff);
                                list.add(nativeAdContent);
                                if (adLoader.isLoading()) {
                                    return;
                                }
                                if (list.size() > 0) {
                                    ZplayDebug.v(TAG, "YuminativeAdapter onSuccess", onoff);
                                    layerPrepared(list);
                                } else if (list.size() <= 0) {
                                    ZplayDebug.v(TAG, "YuminativeAdapter onFailed", onoff);
                                    layerPreparedFailed(LayerErrorCode.ERROR_NO_FILL);
                                }

                            }

                            @Override
                            public void onMappingFailed() {
                                ZplayDebug.v(TAG, "admob native onMappingFailed : ", onoff);
                                if (adLoader.isLoading()) {
                                    return;
                                }

                                if (list.size() > 0) {
                                    ZplayDebug.v(TAG, "YuminativeAdapter onSuccess", onoff);
                                    layerPrepared(list);
                                } else if (list.size() <= 0) {
                                    ZplayDebug.v(TAG, "YuminativeAdapter onFailed", onoff);
                                    layerPreparedFailed(LayerErrorCode.ERROR_NO_FILL);
                                }
                            }
                        });
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
                ZplayDebug.d(TAG, "admob native interstitial failed errorCode=" + errorCode, onoff);
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


        public NativeAdContent(UnifiedNativeAd unifiedNativeAd) {
            NativeAdContent.this.unifiedNativeAd = unifiedNativeAd;
        }

        public void nativeAd(final NativeAdListener nativeAdListener) {
            ZplayDebug.v(TAG, "Admob nativeAd()", onoff);

            if (unifiedNativeAd.getIcon() != null) {
                ZplayDebug.v(TAG, "Admob getIcon()" + unifiedNativeAd.getIcon().getUri().toString(), onoff);
                setIcon(new YumiNativeMappedImage(unifiedNativeAd.getIcon().getDrawable(), unifiedNativeAd.getIcon().getUri().toString(), unifiedNativeAd.getIcon().getScale()));
            } else {
                setIcon(null);
            }
            if (unifiedNativeAd.getImages().size() > 0) {
                ZplayDebug.v(TAG, "Admob getImages()" + unifiedNativeAd.getImages().get(0).getUri().toString(), onoff);
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
            nativeAdListener.onMappingSuccess();
        }


        public void trackView() {
            if (getNativeAdView() == null) {
                ZplayDebug.v(TAG, "trackView getNativeAdView() is null", onoff);
                return;
            }
            YumiNativeAdView yumiNativeAdView = (YumiNativeAdView) getNativeAdView();
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

            unifiedAdView.removeAllViews();
            unifiedAdView.addView(yumiNativeAdView);
            parent.addView(unifiedAdView);
            unifiedAdView.setNativeAd(unifiedNativeAd);
        }

    }

    private interface NativeAdListener {

        /**
         * This method will be called once the native ad mapping is successfully.
         */
        void onMappingSuccess();

        /**
         * This method will be called if the native ad mapping failed.
         */
        void onMappingFailed();
    }
}
