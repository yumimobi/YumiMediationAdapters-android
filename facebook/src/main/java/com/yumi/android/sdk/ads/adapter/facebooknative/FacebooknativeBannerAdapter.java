package com.yumi.android.sdk.ads.adapter.facebooknative;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdIconView;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeBannerAd;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerBannerAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import java.util.ArrayList;
import java.util.List;


public class FacebooknativeBannerAdapter extends YumiCustomerBannerAdapter {

    private static final String TAG = "FacebookNativeBannerAdapter";
    private LinearLayout adView;
    private NativeBannerAd nativeBannerAd;
    private NativeAdListener adListener;

    protected FacebooknativeBannerAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }

    @Override
    protected void init() {
        ZplayDebug.d(TAG, "facebook native banner init", onoff);
        try {
            createBannerListener();
        } catch (Exception e) {
            ZplayDebug.e(TAG, "Init facebook native banner error", false);
        }
    }

    private void createBannerListener() {
        adListener = new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
                ZplayDebug.d(TAG, "facebook native banner onMediaDownloaded", onoff);

            }

            @Override
            public void onError(Ad ad, AdError adError) {
                ZplayDebug.d(TAG, "facebook native banner onError ErrorCode : " + adError.getErrorCode() + "   ErrorMessage : " + adError.getErrorMessage(), onoff);
                layerPreparedFailed(decodeErrorCode(adError));
            }

            @Override
            public void onAdLoaded(Ad ad) {
                ZplayDebug.d(TAG, "facebook native banner onAdLoaded", onoff);
                // Race condition, load() called again before last ad was displayed
                if (nativeBannerAd == null || nativeBannerAd != ad) {
                    return;
                }
                // Inflate Native Banner Ad into Container
                inflateAd(nativeBannerAd);
            }

            @Override
            public void onAdClicked(Ad ad) {
                ZplayDebug.d(TAG, "facebook native banner clicked", onoff);
                layerClicked(-99f, -99f);
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                ZplayDebug.d(TAG, "facebook native banner onLoggingImpression", onoff);
                //Called immediately before an impression is logged.
            }
        };
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

    private void inflateAd(NativeBannerAd nativeBannerAd) {
        // Unregister last ad
        nativeBannerAd.unregisterView();

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        // Inflate the Ad view.  The layout referenced is the one you created in the last step.
        adView = (LinearLayout) inflater.inflate(R.layout.ad_banner_layout, null);

        // Add the AdChoices icon
        RelativeLayout adChoicesContainer = (RelativeLayout) adView.findViewById(R.id.ad_choices_container);
        AdChoicesView adChoicesView = new AdChoicesView(getActivity(), nativeBannerAd, true);
        adChoicesContainer.addView(adChoicesView, 0);

        // Create native UI using the ad metadata.
        TextView nativeAdTitle = (TextView) adView.findViewById(R.id.native_ad_title);
        TextView nativeAdSocialContext = (TextView) adView.findViewById(R.id.native_ad_social_context);
        TextView sponsoredLabel = (TextView) adView.findViewById(R.id.native_ad_sponsored_label);
        AdIconView nativeAdIconView = (AdIconView) adView.findViewById(R.id.native_icon_view);
        Button nativeAdCallToAction = (Button) adView.findViewById(R.id.native_ad_call_to_action);

        // Set the Text.
        nativeAdCallToAction.setText(nativeBannerAd.getAdCallToAction());
        nativeAdCallToAction.setVisibility(nativeBannerAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdTitle.setText(nativeBannerAd.getAdvertiserName());
        nativeAdSocialContext.setText(nativeBannerAd.getAdSocialContext());
        sponsoredLabel.setText(nativeBannerAd.getSponsoredTranslation());

        // Register the Title and CTA button to listen for clicks.
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);
        nativeBannerAd.registerViewForInteraction(adView, nativeAdIconView, clickableViews);
        layerPrepared(adView, true);
    }

    @Override
    protected void callOnActivityDestroy() {
        if (nativeBannerAd != null) {
            nativeBannerAd.destroy();
        }
    }

    @Override
    protected void onPrepareBannerLayer() {
        try {
            nativeBannerAd = new NativeBannerAd(getActivity(), getProvider().getKey1());
            nativeBannerAd.loadAd();
            nativeBannerAd.setAdListener(adListener);
        } catch (Exception e) {
            ZplayDebug.e(TAG, "facebook native banner onPrepareBannerLayer error : ", e, onoff);
        }
    }

    @Override
    public void onActivityPause() {

    }

    @Override
    public void onActivityResume() {

    }
}