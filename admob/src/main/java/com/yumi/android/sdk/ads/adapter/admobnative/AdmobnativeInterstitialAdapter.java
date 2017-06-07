package com.yumi.android.sdk.ads.adapter.admobnative;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;
import com.yumi.android.sdk.ads.adapter.admob.R;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.publish.nativead.YumiNativeAdvancedIntersititalAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import java.util.List;

/**
 * Created by Administrator on 2017/3/23.
 */

public class AdmobnativeInterstitialAdapter extends YumiNativeAdvancedIntersititalAdapter {

    private static final String TAG = "AdmobnativeInterstitialAdapter";

//    private static final String ADMOB_AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110";
//    private static final String ADMOB_APP_ID = "ca-app-pub-3940256099942544~3347511713";

    protected AdmobnativeInterstitialAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
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

    @Override
    protected void onPreparedNativeInterstitial() {
        ZplayDebug.d(TAG, "admob navitead interstitial onPreparedNativeInterstitial", onoff);
        refreshAd(true,true);
    }

    @Override
    protected void NativeLayerPrepared(View view) {
        ZplayDebug.d(TAG, "admob navitead interstitial prapared", onoff);
        layerPrepared();
    }

    @Override
    protected void NativeLayerOnShow() {
        ZplayDebug.d(TAG, "admob navitead interstitial layerExposure", onoff);
        layerExposure();
    }

    @Override
    protected void calculateRequestSize() {

    }

    @Override
    protected void NativeLayerDismiss() {
        ZplayDebug.d(TAG, "admob navitead interstitial layerClosed", onoff);
        layerClosed();
    }

    @Override
    protected void init() {
        ZplayDebug.d(TAG, "admob navitead interstitial init", onoff);
        // Initialize the Mobile Ads SDK.
//        MobileAds.initialize(getActivity(), ADMOB_APP_ID);  他们技术支持说可以去掉，没有任何影响
    }

    @Override
    protected void callOnActivityDestroy() {

    }

    private void refreshAd(boolean requestAppInstallAds, boolean requestContentAds) {
        if (!requestAppInstallAds && !requestContentAds) {
            return;
        }
        AdLoader.Builder builder = new AdLoader.Builder(getActivity(), getProvider().getKey1());

        if (requestAppInstallAds) {
            builder.forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
                @Override
                public void onAppInstallAdLoaded(NativeAppInstallAd ad) {
                    NativeAppInstallAdView adView = (NativeAppInstallAdView) getActivity().getLayoutInflater()
                            .inflate(R.layout.ad_app_install, null);
                    populateAppInstallAdView(ad, adView);
                    if(adView!=null) {
                        ZplayDebug.d(TAG, "admob navitead interstitial refreshAd onAppInstallAdLoaded", onoff);
                        loadData(adView);
                    }
                }
            });
        }

        if (requestContentAds) {
            builder.forContentAd(new NativeContentAd.OnContentAdLoadedListener() {
                @Override
                public void onContentAdLoaded(NativeContentAd ad) {
                    NativeContentAdView adView = (NativeContentAdView) getActivity().getLayoutInflater()
                            .inflate(R.layout.ad_content, null);
                    populateContentAdView(ad, adView);
                    if(adView!=null) {
                        ZplayDebug.d(TAG, "admob navitead interstitial refreshAd onContentAdLoaded", onoff);
                        loadData(adView);
                    }
                }
            });
        }

        VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(true).build();
        NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();

        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                ZplayDebug.d(TAG, "admob native interstitial failed errorCode=" + errorCode, onoff);
                layerPreparedFailed(decodeErrorCode(errorCode));
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                ZplayDebug.d(TAG, "admob native interstitial onClick", onoff);
                layerClicked(-99f, -99f);
            }
        }).build();

        adLoader.loadAd(new AdRequest.Builder().build());


    }


    private void populateContentAdView(NativeContentAd nativeContentAd,
                                       NativeContentAdView adView) {
        adView.setHeadlineView(adView.findViewById(R.id.contentad_headline));
        adView.setImageView(adView.findViewById(R.id.contentad_image));
        adView.setBodyView(adView.findViewById(R.id.contentad_body));
        adView.setCallToActionView(adView.findViewById(R.id.contentad_call_to_action));
        adView.setLogoView(adView.findViewById(R.id.contentad_logo));
        adView.setAdvertiserView(adView.findViewById(R.id.contentad_advertiser));

        // Some assets are guaranteed to be in every NativeContentAd.
        ((TextView) adView.getHeadlineView()).setText(nativeContentAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeContentAd.getBody());
        ((TextView) adView.getCallToActionView()).setText(nativeContentAd.getCallToAction());
        ((TextView) adView.getAdvertiserView()).setText(nativeContentAd.getAdvertiser());

        List<NativeAd.Image> images = nativeContentAd.getImages();

        if (images.size() > 0) {
            ((ImageView) adView.getImageView()).setImageDrawable(images.get(0).getDrawable());
        }

        // Some aren't guaranteed, however, and should be checked.
        NativeAd.Image logoImage = nativeContentAd.getLogo();

        if (logoImage == null) {
            adView.getLogoView().setVisibility(View.INVISIBLE);
        } else {
            ((ImageView) adView.getLogoView()).setImageDrawable(logoImage.getDrawable());
            adView.getLogoView().setVisibility(View.VISIBLE);
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeContentAd);
    }


    private void populateAppInstallAdView(NativeAppInstallAd nativeAppInstallAd,
                                          NativeAppInstallAdView adView) {
        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        VideoController vc = nativeAppInstallAd.getVideoController();

        // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
        // VideoController will call methods on this object when events occur in the video
        // lifecycle.
        vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
            public void onVideoEnd() {
                // Publishers should allow native ads to complete video playback before refreshing
                // or replacing them with another ad in the same UI location.
                ZplayDebug.v(TAG, "admob native Video status: Video playback has ended.", onoff);
                layerMediaEnd();
                super.onVideoEnd();
            }
        });

        adView.setHeadlineView(adView.findViewById(R.id.appinstall_headline));
        adView.setBodyView(adView.findViewById(R.id.appinstall_body));
        adView.setCallToActionView(adView.findViewById(R.id.appinstall_call_to_action));
        adView.setIconView(adView.findViewById(R.id.appinstall_app_icon));
        adView.setPriceView(adView.findViewById(R.id.appinstall_price));
        adView.setStarRatingView(adView.findViewById(R.id.appinstall_stars));
        adView.setStoreView(adView.findViewById(R.id.appinstall_store));

        // The MediaView will display a video asset if one is present in the ad, and the first image
        // asset otherwise.
        MediaView mediaView = (MediaView) adView.findViewById(R.id.appinstall_media);
        adView.setMediaView(mediaView);

        // Some assets are guaranteed to be in every NativeAppInstallAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAppInstallAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeAppInstallAd.getBody());
        ((Button) adView.getCallToActionView()).setText(nativeAppInstallAd.getCallToAction());

        // These assets aren't guaranteed to be in every NativeAppInstallAd, so it's important to
        // check before trying to display them.
        if (nativeAppInstallAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAppInstallAd.getPrice());
        }

        if (nativeAppInstallAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAppInstallAd.getStore());
        }

        if (nativeAppInstallAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAppInstallAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeAppInstallAd);
    }


    private LayerErrorCode decodeErrorCode(int errorCode) {
        switch (errorCode) {
            case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                return LayerErrorCode.ERROR_INTERNAL;
            case AdRequest.ERROR_CODE_INVALID_REQUEST:
                return LayerErrorCode.ERROR_INVALID;
            case AdRequest.ERROR_CODE_NO_FILL:
                return LayerErrorCode.ERROR_NO_FILL;
            case AdRequest.ERROR_CODE_NETWORK_ERROR:
                return LayerErrorCode.ERROR_NETWORK_ERROR;
            default:
                return LayerErrorCode.ERROR_INTERNAL;
        }
    }
}
