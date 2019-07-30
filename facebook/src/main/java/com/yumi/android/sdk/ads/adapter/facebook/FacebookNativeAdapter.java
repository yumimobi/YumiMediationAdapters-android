package com.yumi.android.sdk.ads.adapter.facebook;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.MediaView;
import com.facebook.ads.MediaViewListener;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdListener;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.formats.YumiNativeAdVideoController;
import com.yumi.android.sdk.ads.formats.YumiNativeAdView;
import com.yumi.android.sdk.ads.publish.NativeContent;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerNativeAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import java.util.ArrayList;
import java.util.List;


public class FacebookNativeAdapter extends YumiCustomerNativeAdapter {

    private NativeAd mNativeAd;
    private NativeAdListener nativeAdListener;

    protected FacebookNativeAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }

    @Override
    public void webLayerClickedAndRequestBrowser(String url) {

    }

    @Override
    protected void onPrepareNative() {
        try {
            if (mNativeAd == null) {
                mNativeAd = new NativeAd(getActivity(), getProvider().getKey1());
                mNativeAd.setAdListener(nativeAdListener);
            }
            ZplayDebug.v(TAG, "facebook native onPrepareNative adCount: " + getCurrentPoolSpace(), onoff);
            mNativeAd.loadAd();
        } catch (Exception e) {
            ZplayDebug.e(TAG, "facebook native onPrepareNative error", e, onoff);
        }
    }

    @Override
    protected void init() {
        ZplayDebug.v(TAG, "facebook native Adapter init key1 = " + getProvider().getKey1(), onoff);
        AudienceNetworkAds.initialize(getActivity());
        createListener();
    }

    private void createListener() {
        nativeAdListener = new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
                ZplayDebug.i(TAG, "facebook native onMediaDownloaded", onoff);
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                ZplayDebug.i(TAG, "facebook native onError ErrorCode : " + adError.getErrorCode() + "  || ErrorMessage : " + adError.getErrorMessage(), onoff);
                layerPreparedFailed(FacebookUtil.recodeError(adError));
            }

            @Override
            public void onAdLoaded(Ad ad) {
                ZplayDebug.i(TAG, "facebook native onAdLoaded PlacementId:" + ad.getPlacementId(), onoff);
                try {
                    // Race condition, load() called again before last ad was displayed
                    if (mNativeAd == null || mNativeAd != ad) {
                        return;
                    }
                    List<NativeContent> nativeContentsList = new ArrayList<>();
                    NativeAdContent nativeAdContent = new NativeAdContent(mNativeAd);
                    nativeContentsList.add(nativeAdContent);

                    if (nativeContentsList.isEmpty()) {
                        ZplayDebug.v(TAG, "facebook data is empty", onoff);
                        AdError adError = new AdError(AdError.NO_FILL_ERROR_CODE, "facebook ad is no fill");
                        layerPreparedFailed(FacebookUtil.recodeError(adError));
                        return;
                    }
                    layerPrepared(nativeContentsList);
                } catch (Exception e) {
                    ZplayDebug.e(TAG, "facebook getNativeContentList error : " + e, onoff);
                    AdError adError = new AdError(AdError.NO_FILL_ERROR_CODE, "download image data failed");
                    layerPreparedFailed(FacebookUtil.recodeError(adError));
                }
            }

            @Override
            public void onAdClicked(Ad ad) {
                ZplayDebug.i(TAG, "facebook native onAdClicked", onoff);
                layerClicked(-99f, -99f);
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                ZplayDebug.i(TAG, "facebook native onLoggingImpression", onoff);
                layerExposure();
            }
        };
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
        private NativeAd nativeAd;
        private MediaView mMediaView;

        NativeAdContent(NativeAd nativeAd) {
            NativeAdContent.this.nativeAd = nativeAd;

            setTitle(nativeAd.getAdHeadline());
            setDesc(nativeAd.getAdBodyText());

            Image icon = new Image(null);
            icon.setDrawable(getDrawable());
            setIcon(icon);

            Image image = new Image(null);
            icon.setDrawable(getDrawable());
            setCoverImage(image);

            setCallToAction(mNativeAd.getAdCallToAction());

            setMaterialCreationTime(System.currentTimeMillis());
            setMaterialEtime(getProvider().getMaterialEtime());
            setProviderName(getProvider().getProviderName());
            setSpecifiedProvider(getProvider().getSpecifiedProvider());
            setIsExpressAdView(false);
            if (getActivity() != null) {
                mMediaView = new MediaView(getActivity());
            }

            ZplayDebug.i(TAG, "facebook native hasVideoContent:" + (mNativeAd.getAdCreativeType() == NativeAd.AdCreativeType.VIDEO), onoff);
            setHasVideoContent(mNativeAd.getAdCreativeType() == NativeAd.AdCreativeType.VIDEO);
            setNativeAdVideoController(new FacebookNativeAdVideoController(mMediaView));
        }

        public void trackView() {
            YumiNativeAdView overlayView = getNativeAdView();
            if (overlayView != null) {
                // Create and add Facebook's AdChoicesView to the overlay view.
                AdChoicesView adChoicesView =
                        new AdChoicesView(getNativeAdView().getContext(), mNativeAd, true);
                overlayView.addView(adChoicesView);
                FrameLayout.LayoutParams adChoicesParams =
                        (FrameLayout.LayoutParams) adChoicesView.getLayoutParams();
                setViewPosition(adChoicesParams, getProvider().getNativeAdOptions().getAdChoicesPosition());
                overlayView.requestLayout();

                if (!getProvider().getNativeAdOptions().getHideAdAttribution()) {
                    TextView adAttribution = new TextView(getNativeAdView().getContext());
                    adAttribution.setText(getProvider().getNativeAdOptions().getAdAttributionText());
                    adAttribution.setTextColor(getProvider().getNativeAdOptions().getAdAttributionTextColor());
                    adAttribution.setBackgroundColor(getProvider().getNativeAdOptions().getAdAttributionBackgroundColor());
                    adAttribution.setTextSize(getProvider().getNativeAdOptions().getAdAttributionTextSize());
                    adAttribution.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    overlayView.addView(adAttribution);
                    FrameLayout.LayoutParams adAttributionParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);

                    setViewPosition(adAttributionParams, getProvider().getNativeAdOptions().getAdAttributionPosition());
                    adAttribution.setLayoutParams(adAttributionParams);
                    overlayView.requestLayout();
                }


                if (overlayView.getMediaLayout() != null) {
                    ((ViewGroup) overlayView.getMediaLayout()).removeAllViews();
                    ((ViewGroup) overlayView.getMediaLayout()).addView(mMediaView);
                }
                nativeAd.registerViewForInteraction(overlayView, mMediaView, (ImageView) overlayView.getIconView());
            }
        }
        @Override
        public void destroy(){
            ZplayDebug.v(TAG, "facebook native destory", onoff);
            if (nativeAd != null) {
                nativeAd.destroy();
            }
        }

        private Drawable getDrawable() {
            Bitmap bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
            bitmap.setPixel(0, 0, 0x00FFFFFF); // Set color to blue
            Bitmap resultBitmap = Bitmap.createScaledBitmap(bitmap, 1, 1, true);
            bitmap.recycle();
            return new BitmapDrawable(resultBitmap);
        }

        public class FacebookNativeAdVideoController extends YumiNativeAdVideoController {
            private MediaView mMediaView;

            private FacebookNativeAdVideoController(MediaView mMediaView) {
                this.mMediaView = mMediaView;
            }

            public void play() {

            }

            public void pause() {
            }

            public double getAspectRatio() {
                return 0;
            }

            public void setVideoLifecycleCallbacks(final YumiVideoLifecycleCallbacks videoLifecycleCallbacks) {
                if (mMediaView != null) {
                    mMediaView.setListener(new MediaViewListener() {
                        @Override
                        public void onPlay(MediaView mediaView) {
                            if (videoLifecycleCallbacks != null) {
                                videoLifecycleCallbacks.onVideoPlay();
                            }
                        }

                        @Override
                        public void onVolumeChange(MediaView mediaView, float v) {

                        }

                        @Override
                        public void onPause(MediaView mediaView) {
                            if (videoLifecycleCallbacks != null) {
                                videoLifecycleCallbacks.onVideoPlay();
                            }
                        }

                        @Override
                        public void onComplete(MediaView mediaView) {
                            if (videoLifecycleCallbacks != null) {
                                videoLifecycleCallbacks.onVideoEnd();
                            }
                        }

                        @Override
                        public void onEnterFullscreen(MediaView mediaView) {

                        }

                        @Override
                        public void onExitFullscreen(MediaView mediaView) {

                        }

                        @Override
                        public void onFullscreenBackground(MediaView mediaView) {

                        }

                        @Override
                        public void onFullscreenForeground(MediaView mediaView) {

                        }
                    });
                }
            }
        }
    }

}
