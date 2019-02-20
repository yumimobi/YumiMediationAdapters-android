package com.yumi.android.sdk.ads.adapter.facebook;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdListener;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.formats.YumiNativeAdView;
import com.yumi.android.sdk.ads.publish.NativeContent;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerNativeAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
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
        if (mNativeAd == null) {
            mNativeAd = new NativeAd(getActivity(), getProvider().getKey1());
            mNativeAd.setAdListener(nativeAdListener);
        }
        mNativeAd.loadAd();
    }

    @Override
    protected void init() {
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
                    if (nativeAdContent.isValid()) {
                        nativeContentsList.add(nativeAdContent);
                    }

                    if (nativeContentsList.isEmpty()) {
                        ZplayDebug.v(TAG, "facebook data is empty", onoff);
                        LayerErrorCode request = LayerErrorCode.ERROR_NO_FILL;
                        request.setExtraMsg("Facebook Native: facebook ad is no fill");
                        layerPreparedFailed(request);
                        return;
                    }
                    layerPrepared(nativeContentsList);
                } catch (Exception e) {
                    ZplayDebug.e(TAG, "facebook getNativeContentList error : " + e, onoff);
                    LayerErrorCode request = LayerErrorCode.ERROR_INTERNAL;
                    request.setExtraMsg("facebook Native: download image data failed");
                    layerPreparedFailed(request);
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
        private NativeAd nativeAd;

        NativeAdContent(NativeAd nativeAd) {
            NativeAdContent.this.nativeAd = nativeAd;

            setTitle(nativeAd.getAdHeadline());
            setDesc(nativeAd.getAdBodyText());
            ZplayDebug.i(TAG, "facebook native icon:" + mNativeAd.getAdIcon().toString(), onoff);

            Image icon = new Image(null);
            icon.setDrawable(getDrawable());
            setIcon(icon);

            Image image = new Image(null);
            icon.setDrawable(getDrawable());
            ZplayDebug.i(TAG, "facebook native icon:" + mNativeAd.getAdCoverImage().toString(), onoff);
            setImage(image);

            setCallToAction(mNativeAd.getAdCallToAction());
        }

        /**
         * 内容信息包括必要元素 iconUrl，title, desc, imageUrl
         *
         * @return 包含必要元素，返回 true；否则，返回 false
         */
        boolean isValid() {
            return !TextUtils.isEmpty(getTitle()) &&
                    !TextUtils.isEmpty(getDesc());
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

                //创建广告标识
                TextView adAttribution = new TextView(getNativeAdView().getContext());
                adAttribution.setText(getProvider().getNativeAdOptions().getAdAttributionText());
                adAttribution.setTextColor(getProvider().getNativeAdOptions().getAdAttributionColor());
                adAttribution.setTextSize(getProvider().getNativeAdOptions().getAdAttributionTextSize());
                adAttribution.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                overlayView.addView(adAttribution);
                FrameLayout.LayoutParams adAttributionParams=new FrameLayout.LayoutParams (FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);

                setViewPosition(adAttributionParams, getProvider().getNativeAdOptions().getAdAttributionPosition());
                adAttribution.setLayoutParams(adAttributionParams);
                overlayView.requestLayout();

                MediaView mMediaView = new MediaView(getNativeAdView().getContext());
                if (overlayView.getMediaLayout() != null) {
                    ((ViewGroup) overlayView.getMediaLayout()).removeAllViews();
                    ((ViewGroup) overlayView.getMediaLayout()).addView(mMediaView);
                }
                nativeAd.registerViewForInteraction(overlayView, mMediaView, (ImageView) overlayView.getIconView());
            }
        }

        private Drawable getDrawable() {
            Bitmap bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
            bitmap.setPixel(0, 0, 0x00FFFFFF); // Set color to blue
            Bitmap resultBitmap = Bitmap.createScaledBitmap(bitmap, 1, 1, true);
            bitmap.recycle();
            return new BitmapDrawable(resultBitmap);
        }
    }

}
