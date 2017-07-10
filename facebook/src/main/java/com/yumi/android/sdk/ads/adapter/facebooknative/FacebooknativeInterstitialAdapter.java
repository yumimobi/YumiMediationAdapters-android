package com.yumi.android.sdk.ads.adapter.facebooknative;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSettings;
import com.facebook.ads.MediaView;
import com.facebook.ads.MediaViewListener;
import com.facebook.ads.NativeAd;


import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.publish.nativead.YumiNativeAdvancedIntersititalAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import static com.yumi.android.sdk.ads.adapter.facebooknative.R.*;

public class FacebooknativeInterstitialAdapter extends YumiNativeAdvancedIntersititalAdapter {
    private String TAG = "FacebooknativeInterstitialAdapter";
    private AdListener linstener;
    private NativeAd nativeAd;
    private Activity activity;
    private LinearLayout adView;
    private AdChoicesView adChoicesView;

    protected FacebooknativeInterstitialAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
        this.activity = activity;
        Log.e("sss", "FacebooknativeInterstitialAdapter in");
    }

    @Override
    protected void onPreparedNativeInterstitial() {
        nativeAd = new NativeAd(getContext(), getProvider().getKey1());
        nativeAd.setAdListener(linstener);
        nativeAd.loadAd(NativeAd.MediaCacheFlag.ALL);
    }

    @Override
    protected void NativeLayerPrepared(View view) {
        layerPrepared();
    }

    @Override
    protected void NativeLayerOnShow() {
        layerExposure();
    }

    @Override
    protected void calculateRequestSize() {

    }

    @Override
    protected void NativeLayerDismiss() {
        layerClosed();
    }


    @Override
    protected void init() {
        try {
            String key1 = getProvider().getKey1();
            ZplayDebug.d(TAG, "key1:" + key1, onoff);
            LayoutInflater inflater = LayoutInflater.from(activity);
            adView = (LinearLayout) inflater.inflate(layout.ad_unit_banner_new, null, false);
            createBannerListener();

        } catch (Exception e) {
            e.printStackTrace();
            ZplayDebug.e(TAG, "Init FacebooknativeInterstitial Faild", false);
        }
    }

    private void createBannerListener() {
        linstener = new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                ZplayDebug.d(TAG, "FacebooknativeInterstitial  request failed :" + adError.getErrorMessage(), onoff);
                decodeErrorCode(adError);
            }

            private LayerErrorCode decodeErrorCode(AdError arg1) {
                if (arg1.equals(AdError.NETWORK_ERROR)) {
                    return LayerErrorCode.ERROR_NETWORK_ERROR;
                }
                if (arg1.equals(AdError.NO_FILL)) {
                    return LayerErrorCode.ERROR_NO_FILL;
                }
                return LayerErrorCode.ERROR_INTERNAL;
            }

            @Override
            public void onAdLoaded(Ad ad) {
                if (nativeAd == null || nativeAd != ad) {
                    // Race condition, load() called again before last ad was displayed
                    return;
                }
                // Unregister last ad
                nativeAd.unregisterView();
                inflateAd(nativeAd, adView);
            }

            @Override
            public void onAdClicked(Ad ad) {
            //  requestSystemBrowser(nativeAd.getAdChoicesLinkUrl());
                layerClicked(-99f, -99f);
            }

            @Override
            public void onLoggingImpression(Ad ad) {
            //Called immediately before an impression is logged.
            }
        };
    }

    private void inflateAd(NativeAd nativeAd, LinearLayout adView) {
        // Create native UI using the ad metadata.
        ImageView nativeAdIcon = (ImageView) adView.findViewById(id.native_ad_icon);
        TextView nativeAdTitle = (TextView) adView.findViewById(id.native_ad_title);
        TextView nativeAdBody = (TextView) adView.findViewById(id.native_ad_body);
        MediaView nativeAdMedia = (MediaView) adView.findViewById(id.native_ad_media);
        nativeAdMedia.setListener(new MediaViewListener() {
            @Override
            public void onVolumeChange(MediaView mediaView, float volume) {
            }

            @Override
            public void onPause(MediaView mediaView) {
            }

            @Override
            public void onPlay(MediaView mediaView) {
            }

            @Override
            public void onFullscreenBackground(MediaView mediaView) {
            }

            @Override
            public void onFullscreenForeground(MediaView mediaView) {
            }

            @Override
            public void onExitFullscreen(MediaView mediaView) {
            }

            @Override
            public void onEnterFullscreen(MediaView mediaView) {
            }

            @Override
            public void onComplete(MediaView mediaView) {
            }
        });
        nativeAdMedia.setAutoplay(AdSettings.isVideoAutoplay());
        nativeAdMedia.setAutoplayOnMobile(AdSettings.isVideoAutoplayOnMobile());
        TextView nativeAdSocialContext =
                (TextView) adView.findViewById(id.native_ad_social_context);
        Button nativeAdCallToAction = (Button) adView.findViewById(id.native_ad_call_to_action);

        // Setting the Text
        NativeAd.Image adIcon = nativeAd.getAdIcon();
        NativeAd.downloadAndDisplayImage(adIcon, nativeAdIcon);
        nativeAdTitle.setText(nativeAd.getAdTitle());
        if (adChoicesView == null) {
            LinearLayout adChoicesContainer = (LinearLayout) adView.findViewById(id.ad_choices_container);
            adChoicesView = new AdChoicesView(getContext(), nativeAd, true);
            adChoicesContainer.addView(adChoicesView);
        }
        nativeAdBody.setText(nativeAd.getAdBody());
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        nativeAdCallToAction.setVisibility(View.VISIBLE);
        // Downloading and setting the cover image.
        NativeAd.Image adCoverImage = nativeAd.getAdCoverImage();
        Log.e("sss", "nativeAd.getAdCoverImage()" + nativeAd.getAdCoverImage().getUrl());
        int bannerWidth = adCoverImage.getWidth();
        int bannerHeight = adCoverImage.getHeight();
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        int mediaWidth = adView.getWidth() > 0 ? adView.getWidth() : metrics.widthPixels;
        nativeAdMedia.setLayoutParams(new LinearLayout.LayoutParams(
                mediaWidth,
                Math.min(
                        (int) (((double) mediaWidth / (double) bannerWidth) * bannerHeight),
                        metrics.heightPixels / 3)));

        nativeAdMedia.setNativeAd(nativeAd);

        // Wire up the View with the native ad, the whole nativeAdContainer will be clickable.
        nativeAd.registerViewForInteraction(adView);
//		calculateViewSize(mediaWidth,);
        loadData(adView);

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
}