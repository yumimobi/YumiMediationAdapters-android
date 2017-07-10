package com.yumi.android.sdk.ads.adapter.facebooknative;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.NativeAd;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerBannerAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.yumi.android.sdk.ads.adapter.facebooknative.R.*;


public class FacebooknativeBannerAdapter extends YumiCustomerBannerAdapter {

    private static final String TAG = "FacebooknativeBannerAdapter";
    private ImageView iconView;
    private TextView titleView;
    private TextView descView;
    private AdListener linstener;
    private NativeAd nativeAd;
    private RelativeLayout nativeAdContainer;
    private AdChoicesView adChoicesView;
    private View bannerView;
//    private int bannerHeight;
//    private int bannerWidth;

    protected FacebooknativeBannerAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
        Log.e("sss", "FacebooknativeBannerAdapter in");
    }

    @Override
    protected void init() {
        Log.e("sss", "FacebooknativeBannerAdapter init");
        try {
            bannerView = getActivity().getLayoutInflater().inflate(layout.ad_banner_layout, null, false);
            nativeAdContainer = (RelativeLayout) bannerView.findViewById(id.ll_header);
            iconView = (ImageView) bannerView.findViewById(id.native_ad_icon);
            titleView = (TextView) bannerView.findViewById(id.native_ad_title);
            descView = (TextView) bannerView.findViewById(id.sponsored_label);
            createBannerListener();
        } catch (Exception e) {
            e.printStackTrace();
            ZplayDebug.e(TAG, "Init FacebooknativeBanner Faild", false);
        }
    }

    private void createBannerListener() {
        Log.e("sss", "init5");
        linstener = new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                layerPreparedFailed(decodeErrorCode(adError));
            }

            @Override
            public void onAdLoaded(Ad ad) {
                Log.e("sss", "init6");
                if (nativeAd == null || nativeAd != ad) {
                    // Race condition, load() called again before last ad was displayed
                    return;
                }
                inflateAd(nativeAd, nativeAdContainer);
                //确认是否添加布局进去会不会有问题
                layerPrepared(nativeAdContainer, true);
            }

            @Override
            public void onAdClicked(Ad ad) {
                ZplayDebug.d(TAG, "facebook banner clicked", onoff);
                layerClicked(-99f, -99f);
            }

            @Override
            public void onLoggingImpression(Ad ad) {
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

    private void inflateAd(NativeAd nativeAd, RelativeLayout nativeAdContainer) {
        titleView.setText(nativeAd.getAdTitle());
        descView.setText(nativeAd.getAdSubtitle());
        // Downloading and setting the ad icon.
        NativeAd.Image adIcon = nativeAd.getAdIcon();
        NativeAd.downloadAndDisplayImage(adIcon, iconView);
        if (adChoicesView == null) {
            RelativeLayout adChoicesContainer = (RelativeLayout) bannerView.findViewById(id.ad_choices_container);
            adChoicesView = new AdChoicesView(getContext(), nativeAd, true);
            adChoicesContainer.addView(adChoicesView);
        }
        nativeAd.registerViewForInteraction(nativeAdContainer);
        sendChangeViewBeforePrepared(nativeAdContainer);

    }


    @Override
    protected void callOnActivityDestroy() {
        if (nativeAd != null) {
            nativeAd.destroy();
        }
    }

    @Override
    protected void onPrepareBannerLayer() {

        nativeAd = new NativeAd(getContext(), getProvider().getKey1());
        nativeAd.setAdListener(linstener);

        nativeAd.loadAd();
    }
    @Override
    public void onActivityPause() {

    }

    @Override
    public void onActivityResume() {

    }
}