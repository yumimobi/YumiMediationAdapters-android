package com.yumi.android.sdk.ads.adapter.pubnative;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;

import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.formats.YumiNativeAdVideoController;
import com.yumi.android.sdk.ads.formats.YumiNativeAdView;
import com.yumi.android.sdk.ads.publish.NativeContent;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerNativeAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.file.BitmapDownloadUtil;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.PNLite;
import net.pubnative.lite.sdk.models.NativeAd;
import net.pubnative.lite.sdk.nativead.HyBidNativeAdRequest;

import java.util.ArrayList;
import java.util.List;

import static com.yumi.android.sdk.ads.adapter.pubnative.PubNativeUtil.initPubNativeSDK;
import static com.yumi.android.sdk.ads.adapter.pubnative.PubNativeUtil.recodeError;
import static com.yumi.android.sdk.ads.adapter.pubnative.PubNativeUtil.sdkVersion;
import static com.yumi.android.sdk.ads.adapter.pubnative.PubNativeUtil.updateGDPRStatus;
import static com.yumi.android.sdk.ads.utils.file.BitmapDownloadUtil.loadDrawables;

public class PubnativeNativeAdapter extends YumiCustomerNativeAdapter {
    private String TAG = "PubnativeNativeAdapter";

    protected PubnativeNativeAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }

    @Override
    public void webLayerClickedAndRequestBrowser(String url) {

    }

    @Override
    protected void onPrepareNative() {
        final boolean isInitialized = PNLite.isInitialized();
        final String appToken = getProvider().getKey1();
        ZplayDebug.d(TAG, "onPrepareNative: " + isInitialized + ", appToken: " + appToken);
        if (!isInitialized) {
            initPubNativeSDK(appToken, getActivity(), new HyBid.InitialisationListener() {
                @Override
                public void onInitialisationFinished(boolean b) {
                    ZplayDebug.d(TAG, "onInitialisationFinished: " + b);
                    if (b) {
                        loadAd();
                    } else {
                        layerPreparedFailed(recodeError("sdk initialization failed."));
                    }
                }
            });
            return;
        }

        loadAd();
    }

    private void loadAd() {
        final String zoneId = getProvider().getKey2();
        ZplayDebug.d(TAG, "loadAd: " + zoneId);
        HyBidNativeAdRequest nativeAdRequest = new HyBidNativeAdRequest();
        nativeAdRequest.load(zoneId, new HyBidNativeAdRequest.RequestListener() {

            @Override
            public void onRequestSuccess(NativeAd NativeAd) {
                List<NativeContent> nativeContentsList = new ArrayList<>();
                try {
                    final NativeAdContent nativeAdContent = new NativeAdContent(NativeAd);
                    if (nativeAdContent.isValid()) {
                        nativeContentsList.add(nativeAdContent);
                    }
                } catch (Exception e) {
                    ZplayDebug.d(TAG, "onRequestSuccess: exception: " + e);
                }

                ZplayDebug.d(TAG, "onRequestSuccess: " + nativeContentsList);

                if (nativeContentsList.isEmpty()) {
                    layerPreparedFailed(recodeError("pubnative ad is no fill"));
                    return;
                }

                if (!getProvider().getNativeAdOptions().getIsDownloadImage()) {
                    layerPrepared(nativeContentsList);
                    return;
                }

                loadDrawables(getActivity(), nativeContentsList, new BitmapDownloadUtil.DownloadDrawableListener() {
                    @Override
                    public void onLoaded(List<NativeContent> data) {
                        ZplayDebug.d(TAG, "onLoaded: " + data);
                        layerPrepared(data);
                    }

                    @Override
                    public void onFailed() {
                        ZplayDebug.d(TAG, "onFailed: ");
                        layerPreparedFailed(recodeError("download image data failed"));
                    }
                });
            }

            @Override
            public void onRequestFail(Throwable throwable) {
                ZplayDebug.d(TAG, "onRequestFail: " + throwable);
                layerPreparedFailed(recodeError(throwable.toString()));
            }
        });
    }

    @Override
    protected void init() {
        ZplayDebug.d(TAG, "init: ");
        updateGDPRStatus();
    }

    class NativeAdContent extends NativeContent {
        private NativeAd nativeAd;

        NativeAdContent(NativeAd nativeAd) {
            NativeAdContent.this.nativeAd = nativeAd;
            setTitle(nativeAd.getTitle());
            setDesc(nativeAd.getDescription());
            setCallToAction(nativeAd.getCallToActionText());
            setStarRating((double) nativeAd.getRating());
            setCoverImage(new Image(nativeAd.getBannerUrl()));
            setIcon(new Image(nativeAd.getIconUrl()));
            setNativeAdVideoController(new YumiNativeAdVideoController());

            setMaterialCreationTime(System.currentTimeMillis());
            setMaterialEtime(getProvider().getMaterialEtime());
            setProviderName(getProvider().getProviderName());
            setSpecifiedProvider(getProvider().getSpecifiedProvider());
            setIsExpressAdView(false);
        }

        @Override
        public void trackView() {
            super.trackView();
            if (getNativeAdView() == null) {
                ZplayDebug.v(TAG, "pubnative native trackView getNativeAdView() is null", onoff);
                return;
            }
            YumiNativeAdView nativeAdView = getNativeAdView();
            View adChoicesView = nativeAd.getContentInfo(getContext());
            if (nativeAd.getContentInfo(getContext()) != null) {
                FrameLayout contentInfoContainer = new FrameLayout(getContext());

                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT);
                setViewPosition(layoutParams, getProvider().getNativeAdOptions().getAdChoicesPosition());
                contentInfoContainer.setLayoutParams(layoutParams);
                contentInfoContainer.addView(adChoicesView);

                nativeAdView.addView(contentInfoContainer);
                nativeAdView.requestLayout();
            }

            if (getNativeAdView().getCallToActionView() != null) {
                getNativeAdView().getCallToActionView().setClickable(false);
            }

            nativeAd.startTracking(nativeAdView, new NativeAd.Listener() {
                @Override
                public void onAdImpression(NativeAd ad, View view) {
                    ZplayDebug.d(TAG, "onAdImpression: ");
                    layerExposure();
                }

                @Override
                public void onAdClick(NativeAd ad, View view) {
                    ZplayDebug.d(TAG, "onAdClick: ");
                    layerClicked(-999f, -999f);
                }
            });
        }

        @Override
        public void destroy() {
            if (nativeAd != null) {
                nativeAd.stopTracking();
            }
        }
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

    @Override
    public String getProviderVersion() {
        return sdkVersion();
    }
}
