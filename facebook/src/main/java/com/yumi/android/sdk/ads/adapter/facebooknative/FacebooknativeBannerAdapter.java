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
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeBannerAd;
import com.yumi.android.sdk.ads.adapter.facebook.FacebookUtil;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerBannerAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.ads.AdError.NO_FILL;
import static com.yumi.android.sdk.ads.adapter.facebook.FacebookUtil.initSDK;
import static com.yumi.android.sdk.ads.adapter.facebook.FacebookUtil.recodeError;
import static com.yumi.android.sdk.ads.adapter.facebook.FacebookUtil.sdkVersion;
import static com.yumi.android.sdk.ads.publish.enumbean.AdSize.BANNER_SIZE_SMART;


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
        ZplayDebug.d(TAG, "native banner init");
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
                ZplayDebug.d(TAG, "onMediaDownloaded");

            }

            @Override
            public void onError(Ad ad, AdError adError) {
                ZplayDebug.d(TAG, "onError ErrorCode : " + adError.getErrorCode() + "   ErrorMessage : " + adError.getErrorMessage());
                layerPreparedFailed(FacebookUtil.recodeError(adError));
            }

            @Override
            public void onAdLoaded(Ad ad) {
                ZplayDebug.d(TAG, "onAdLoaded");
                // Race condition, load() called again before last ad was displayed
                if (nativeBannerAd == null || nativeBannerAd != ad) {
                    return;
                }
                // Inflate Native Banner Ad into Container
                inflateAd(nativeBannerAd);
            }

            @Override
            public void onAdClicked(Ad ad) {
                ZplayDebug.d(TAG, "onAdClicked");
                layerClicked(-99f, -99f);
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                ZplayDebug.d(TAG, "onLoggingImpression");
                //Called immediately before an impression is logged.
            }
        };
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
    protected void onDestroy() {
        if (nativeBannerAd != null) {
            nativeBannerAd.destroy();
        }
    }

    @Override
    protected void onPrepareBannerLayer() {
        try {
            if (!AudienceNetworkAds.isInitialized(getContext())) {
                initSDK(getContext(), new AudienceNetworkAds.InitListener() {
                    @Override
                    public void onInitialized(AudienceNetworkAds.InitResult initResult) {
                        if (initResult.isSuccess()) {
                            loadAd();
                        } else {
                            layerPreparedFailed(recodeError(AdError.INTERNAL_ERROR, "facebook init errorMsg: " + initResult.getMessage()));
                        }
                    }
                });
                return;
            }

            loadAd();
        } catch (Exception e) {
            ZplayDebug.e(TAG, "facebook native banner onPrepareBannerLayer error : ", e);
        }
    }

    private void loadAd(){
        ZplayDebug.i(TAG, "load new banner");
        if (bannerSize == BANNER_SIZE_SMART) {
            ZplayDebug.d(TAG, "not support smart banner");
            layerPreparedFailed(FacebookUtil.recodeError(NO_FILL, "not support smart banner."));
            return;
        }
        nativeBannerAd = new NativeBannerAd(getActivity(), getProvider().getKey1());
        nativeBannerAd.loadAd();
        nativeBannerAd.setAdListener(adListener);
    }

    @Override
    public void onActivityPause() {

    }

    @Override
    public void onActivityResume() {

    }

    @Override
    public String getProviderVersion() {
        return sdkVersion();
    }
}