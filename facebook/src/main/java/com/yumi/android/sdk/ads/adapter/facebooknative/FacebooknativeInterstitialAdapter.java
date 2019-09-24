package com.yumi.android.sdk.ads.adapter.facebooknative;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdIconView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdListener;
import com.yumi.android.sdk.ads.adapter.facebook.FacebookUtil;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.nativead.YumiNativeAdvancedIntersititalAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import java.util.ArrayList;
import java.util.List;

import static com.yumi.android.sdk.ads.adapter.facebook.FacebookUtil.initSDK;
import static com.yumi.android.sdk.ads.adapter.facebook.FacebookUtil.sdkVersion;

public class FacebooknativeInterstitialAdapter extends YumiNativeAdvancedIntersititalAdapter {
    private String TAG = "FacebooknativeInterstitialAdapter";
    private NativeAdListener linstener;
    private NativeAd nativeAd;
    private Activity activity;
    private LinearLayout adView;
    private AdChoicesView adChoicesView;

    protected FacebooknativeInterstitialAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
        this.activity = activity;
    }

    @Override
    protected void onPreparedNativeInterstitial() {
        try {
            nativeAd = new NativeAd(getContext(), getProvider().getKey1());
            nativeAd.setAdListener(linstener);
            nativeAd.loadAd(NativeAd.MediaCacheFlag.ALL);
        } catch (Exception e) {
            ZplayDebug.e(TAG, "facebook native Interstitial onPreparedNativeInterstitial error :", e, false);
        }
    }

    @Override
    protected void NativeLayerPrepared(View view) {
        ZplayDebug.d(TAG, "facebook native Interstitial NativeLayerPrepared", onoff);
        layerPrepared();
    }

    @Override
    protected void NativeLayerOnShow() {
        ZplayDebug.d(TAG, "facebook native Interstitial NativeLayerOnShow", onoff);
        layerExposure();
    }

    @Override
    protected void calculateRequestSize() {
        ZplayDebug.d(TAG, "facebook native Interstitial calculateRequestSize", onoff);

    }

    @Override
    protected void NativeLayerDismiss() {
        ZplayDebug.d(TAG, "facebook native Interstitial NativeLayerDismiss", onoff);
        layerClosed();
    }


    @Override
    protected void init() {
        try {
            ZplayDebug.d(TAG, "facebook native Interstitial init", onoff);
            initSDK(getContext());
            LayoutInflater inflater = LayoutInflater.from(activity);
            adView = (LinearLayout) inflater.inflate(R.layout.ad_interstitial_layout, null, false);
            createListener();
        } catch (Exception e) {
            ZplayDebug.e(TAG, "facebook native Interstitial init error :", e, false);
        }
    }

    private void createListener() {
        linstener = new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
                ZplayDebug.d(TAG, "facebook native Interstitial onMediaDownloaded", onoff);

            }

            @Override
            public void onError(Ad ad, AdError adError) {
                ZplayDebug.d(TAG, "facebook native Interstitial onError ErrorCode : " + adError.getErrorCode() + "   ErrorMessage : " + adError.getErrorMessage(), onoff);
                layerPreparedFailed(FacebookUtil.recodeError(adError));
            }

            @Override
            public void onAdLoaded(Ad ad) {
                ZplayDebug.d(TAG, "facebook native Interstitial onAdLoaded", onoff);
                if (nativeAd == null || nativeAd != ad) {
                    // Race condition, load() called again before last ad was displayed
                    return;
                }
                // Unregister last ad
                nativeAd.unregisterView();
//                inflateAd(nativeAd, adView);
                inflateAd(nativeAd);
            }

            @Override
            public void onAdClicked(Ad ad) {
                ZplayDebug.d(TAG, "facebook native Interstitial onAdClicked", onoff);
                //  requestSystemBrowser(nativeAd.getAdChoicesLinkUrl());
                layerClicked(-99f, -99f);
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                ZplayDebug.d(TAG, "facebook native Interstitial onLoggingImpression", onoff);
                //Called immediately before an impression is logged.
            }
        };
    }

    private void inflateAd(NativeAd nativeAd) {
        nativeAd.unregisterView();
        // Add the Ad view into the ad container.
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        // Add the AdChoices icon
        LinearLayout adChoicesContainer = (LinearLayout) adView.findViewById(R.id.ad_choices_container);
        adChoicesContainer.removeAllViews();
        AdChoicesView adChoicesView = new AdChoicesView(getActivity(), nativeAd, true);
        adChoicesContainer.addView(adChoicesView, 0);

        // Create native UI using the ad metadata.
        AdIconView nativeAdIcon = (AdIconView) adView.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = (TextView) adView.findViewById(R.id.native_ad_title);
        MediaView nativeAdMedia = (MediaView) adView.findViewById(R.id.native_ad_media);
        TextView nativeAdSocialContext = (TextView) adView.findViewById(R.id.native_ad_social_context);
        TextView nativeAdBody = (TextView) adView.findViewById(R.id.native_ad_body);
        TextView sponsoredLabel = (TextView) adView.findViewById(R.id.native_ad_sponsored_label);
        Button nativeAdCallToAction = (Button) adView.findViewById(R.id.native_ad_call_to_action);

        // Set the Text.
        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdBody.setText(nativeAd.getAdBodyText());
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

        // Create a list of clickable views
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);

        // Register the Title and CTA button to listen for clicks.
        nativeAd.registerViewForInteraction(
                adView,
                nativeAdMedia,
                nativeAdIcon,
                clickableViews);

        loadData(adView);
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
    public String getProviderVersion() {
        return sdkVersion();
    }
}