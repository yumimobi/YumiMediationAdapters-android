package com.yumi.android.sdk.ads.adapter.gdtmob;

import android.app.Activity;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.nativ.MediaView;
import com.qq.e.ads.nativ.NativeADEventListener;
import com.qq.e.ads.nativ.NativeADMediaListener;
import com.qq.e.ads.nativ.NativeADUnifiedListener;
import com.qq.e.ads.nativ.NativeUnifiedAD;
import com.qq.e.ads.nativ.NativeUnifiedADData;
import com.qq.e.ads.nativ.widget.NativeAdContainer;
import com.qq.e.comm.constants.AdPatternType;
import com.qq.e.comm.util.AdError;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.formats.YumiNativeAdOptions;
import com.yumi.android.sdk.ads.formats.YumiNativeAdVideoController;
import com.yumi.android.sdk.ads.formats.YumiNativeAdView;
import com.yumi.android.sdk.ads.publish.NativeContent;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerNativeAdapter;
import com.yumi.android.sdk.ads.self.ui.ResFactory;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.device.PhoneInfoGetter;
import com.yumi.android.sdk.ads.utils.file.BitmapDownloadUtil;

import java.util.ArrayList;
import java.util.List;

import static com.yumi.android.sdk.ads.adapter.GdtUtil.recodeError;
import static com.yumi.android.sdk.ads.utils.device.WindowSizeUtils.dip2px;
import static com.yumi.android.sdk.ads.utils.file.BitmapDownloadUtil.loadDrawables;

/**
 * Created by Administrator on 2017/7/3.
 */
public class GdtmobNativeAdapter extends YumiCustomerNativeAdapter {

    private NativeUnifiedAD nativeAD;

    protected GdtmobNativeAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }

    @Override
    public void webLayerClickedAndRequestBrowser(String s) {

    }

    @Override
    protected void onPrepareNative() {
        if (nativeAD != null) {
            int currentPoolSpace = getCurrentPoolSpace();
            ZplayDebug.v(TAG, "Gdt native Adapter invoke onPrepareNative adCount=" + getCurrentPoolSpace(), onoff);
            nativeAD.loadData(currentPoolSpace);
        }
    }

    @Override
    protected void init() {
        ZplayDebug.v(TAG, "Gdt native Adapter init key1 = " + getProvider().getKey1() + ", key2 = " + getProvider().getKey2(), onoff);
        nativeAD = new NativeUnifiedAD(getActivity(), getProvider().getKey1(), getProvider().getKey2(), new NativeADUnifiedListener() {
            @Override
            public void onADLoaded(List<NativeUnifiedADData> adlist) {
                ZplayDebug.v(TAG, "onADLoaded", onoff);
                final List<NativeContent> list = new ArrayList<>();
                for (final NativeUnifiedADData item : adlist) {
                    try {
                        final NativeAdContent content = new NativeAdContent(item);
                        if (content.isValid()) {
                            list.add(content);
                        }
                    } catch (Exception e) {
                        ZplayDebug.e(TAG, "gdt data parse error : " + e, onoff);
                    }
                }
                if (list.isEmpty()) {
                    ZplayDebug.v(TAG, "gdt data is empty", onoff);
                    layerPreparedFailed(recodeError(new AdError(-1, "got gdt native ad, but the ad ")));
                    return;
                }

                if (!getProvider().getNativeAdOptions().getIsDownloadImage()) {
                    layerPrepared(list);
                    return;
                }

                loadDrawables(getActivity(), list, new BitmapDownloadUtil.DownloadDrawableListener() {
                    @Override
                    public void onLoaded(List<NativeContent> data) {
                        layerPrepared(data);
                    }

                    @Override
                    public void onFailed() {
                        layerPreparedFailed(recodeError(new AdError(-1, "got gdt native ad, but cannot download image data")));
                    }
                });
            }

            @Override
            public void onNoAD(AdError adError) {
                if (adError == null) {
                    ZplayDebug.d(TAG, "GDT nativead onADError adError = null", onoff);
                    layerPreparedFailed(recodeError(null));
                    return;
                }
                ZplayDebug.d(TAG, "GDT nativead onADError ErrorCode:" + adError.getErrorCode() + " msg:" + adError.getErrorMsg(), onoff);
                layerPreparedFailed(recodeError(adError));
            }
        });

    }


    @Override
    protected void callOnActivityDestroy() {

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

    private class NativeAdContent extends NativeContent {

        private NativeUnifiedADData mGdtData;
        private MediaView mediaview;

        private NativeAdContent(NativeUnifiedADData gdtData) {
            mGdtData = gdtData;
            setTitle(gdtData.getTitle());
            setDesc(gdtData.getDesc());
            setStarRating((double) gdtData.getAppScore());
            setCoverImage(new Image(gdtData.getImgUrl()));
            setIcon(new Image(gdtData.getIconUrl()));
            setCallToAction(PhoneInfoGetter.getLanguage().startsWith("zh") ? "查看详情" : "learn more");

            setMaterialCreationTime(System.currentTimeMillis());
            setHasVideoContent(mGdtData.getAdPatternType() == AdPatternType.NATIVE_VIDEO);
            setMaterialEtime(getProvider().getMaterialEtime());
            setNativeAdVideoController(new YumiNativeAdVideoController());
            setProviderName("Gdt");
        }

        @Override
        public void trackView() {
            if (getNativeAdView() == null) {
                ZplayDebug.v(TAG, "GDT native trackView getNativeAdView() is null", onoff);
                return;
            }

            YumiNativeAdView yumiNativeAdView = getNativeAdView();
            ViewGroup parent = (ViewGroup) yumiNativeAdView.getParent();
            parent.removeView(yumiNativeAdView);

            ImageView adLogo = new ImageView(getNativeAdView().getContext());
            Drawable zplayad_media_gdt_logo = ResFactory.getDrawableByAssets("zplayad_media_gdt_logo", getNativeAdView().getContext());
            adLogo.setBackground(zplayad_media_gdt_logo);
            getNativeAdView().addView(adLogo);
            FrameLayout.LayoutParams adLogoParams = new FrameLayout.LayoutParams(dip2px(getNativeAdView().getContext(), 20), dip2px(getNativeAdView().getContext(), 20));
            setViewPosition(adLogoParams, YumiNativeAdOptions.POSITION_BOTTOM_RIGHT);
            adLogo.setLayoutParams(adLogoParams);
            getNativeAdView().requestLayout();

            if (!getProvider().getNativeAdOptions().getHideAdAttribution()) {
                TextView adAttribution = new TextView(getNativeAdView().getContext());
                adAttribution.setText(getProvider().getNativeAdOptions().getAdAttributionText());
                adAttribution.setTextColor(getProvider().getNativeAdOptions().getAdAttributionTextColor());
                adAttribution.setBackgroundColor(getProvider().getNativeAdOptions().getAdAttributionBackgroundColor());
                adAttribution.setTextSize(getProvider().getNativeAdOptions().getAdAttributionTextSize());
                adAttribution.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                getNativeAdView().addView(adAttribution);
                FrameLayout.LayoutParams adAttributionParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);

                setViewPosition(adAttributionParams, getProvider().getNativeAdOptions().getAdAttributionPosition());
                adAttribution.setLayoutParams(adAttributionParams);
                getNativeAdView().requestLayout();
            }

            if (mGdtData.getAdPatternType() == AdPatternType.NATIVE_VIDEO) {
                if (yumiNativeAdView.getMediaLayout() != null) {
                    mediaview = new MediaView(getActivity());
                    mediaview.setVisibility(View.VISIBLE);
                    ((ViewGroup) yumiNativeAdView.getMediaLayout()).removeAllViews();
                    ((ViewGroup) yumiNativeAdView.getMediaLayout()).addView(mediaview);
                }
            }

            NativeAdContainer nativeAdContainer = new NativeAdContainer(getActivity());
            nativeAdContainer.removeAllViews();
            nativeAdContainer.addView(yumiNativeAdView);
            parent.addView(nativeAdContainer);

            List<View> clickableViews = new ArrayList<>();
            clickableViews.add(getNativeAdView());
            if (getNativeAdView().getCallToActionView() != null) {
                clickableViews.add(getNativeAdView().getCallToActionView());
            }
            mGdtData.bindAdToView(getActivity(), nativeAdContainer, null, clickableViews);

            mGdtData.setNativeAdEventListener(new NativeADEventListener() {
                @Override
                public void onADExposed() {
                    ZplayDebug.v(TAG, "Gdt native Adapter onADExposed", onoff);
                    layerExposure();
                }

                @Override
                public void onADClicked() {
                    ZplayDebug.v(TAG, "Gdt native Adapter onADClicked", onoff);
                    layerClicked(-99f, -99f);

                }

                @Override
                public void onADError(AdError adError) {
                    ZplayDebug.v(TAG, "Gdt native Adapter onADError" + adError.getErrorMsg(), onoff);
                }

                @Override
                public void onADStatusChanged() {
                    ZplayDebug.v(TAG, "Gdt native Adapter onADStatusChanged", onoff);
                }
            });
            ZplayDebug.v(TAG, "Gdt native Adapter AdPatternType : " + mGdtData.getAdPatternType(), onoff);
            if (mGdtData.getAdPatternType() == AdPatternType.NATIVE_VIDEO) {
                if (mediaview != null) {
                    setNativeAdVideoController(new GdtNativeViewController(mGdtData, mediaview));
                }
            }

        }

        public void onResume() {
            ZplayDebug.v(TAG, "Gdt native Adapter onResume", onoff);
            if (mGdtData != null) {
                try {
                    mGdtData.resume();
                } catch (Exception e) {
                    ZplayDebug.d(TAG, "onResume: " + e);
                }
            }
        }

        public class GdtNativeViewController extends YumiNativeAdVideoController {
            private NativeUnifiedADData gdtData;
            YumiVideoLifecycleCallbacks videoLifecycleCallbacks;

            private GdtNativeViewController(NativeUnifiedADData gdtData, MediaView mediaview) {
                this.gdtData = gdtData;
                ZplayDebug.v(TAG, "Gdt native Adapter AdPatternType : bindMediaView", onoff);
                gdtData.bindMediaView(mediaview, new VideoOption.Builder()
                        .setAutoPlayMuted(true).setAutoPlayPolicy(VideoOption.AutoPlayPolicy.WIFI).build(), new NativeADMediaListener() {
                    @Override
                    public void onVideoInit() {
                        ZplayDebug.v(TAG, "Gdt native Adapter onVideoInit", onoff);
                    }

                    @Override
                    public void onVideoLoading() {
                        ZplayDebug.v(TAG, "Gdt native Adapter onVideoLoading", onoff);
                    }

                    @Override
                    public void onVideoReady() {
                        ZplayDebug.v(TAG, "Gdt native Adapter onVideoReady", onoff);
                    }

                    @Override
                    public void onVideoLoaded(int videoDuration) {
                        ZplayDebug.v(TAG, "Gdt native Adapter onVideoLoaded", onoff);
                    }

                    @Override
                    public void onVideoStart() {
                        ZplayDebug.v(TAG, "Gdt native Adapter onVideoStart", onoff);
                        if (videoLifecycleCallbacks != null) {
                            videoLifecycleCallbacks.onVideoPlay();
                        }
                    }

                    @Override
                    public void onVideoPause() {
                        ZplayDebug.v(TAG, "Gdt native Adapter onVideoPause", onoff);
                        if (videoLifecycleCallbacks != null) {
                            videoLifecycleCallbacks.onVideoPause();
                        }
                    }

                    @Override
                    public void onVideoResume() {
                        ZplayDebug.v(TAG, "Gdt native Adapter onVideoResume", onoff);
                    }

                    @Override
                    public void onVideoCompleted() {
                        ZplayDebug.v(TAG, "Gdt native Adapter onVideoCompleted", onoff);
                        if (videoLifecycleCallbacks != null) {
                            videoLifecycleCallbacks.onVideoEnd();
                        }
                    }

                    @Override
                    public void onVideoError(AdError error) {
                        ZplayDebug.v(TAG, "Gdt native Adapter onVideoError" + error.getErrorMsg(), onoff);
                    }
                });
            }

            @Override
            public void play() {
                if (gdtData != null) {
                    try {
                        gdtData.resumeVideo();
                    } catch (Exception e) {
                        ZplayDebug.d(TAG, "play: " + e);
                    }
                }
            }

            @Override
            public void pause() {
                if (gdtData != null) {
                    try {
                        gdtData.pauseVideo();
                    } catch (Exception e) {
                        ZplayDebug.d(TAG, "pause: " + e);
                    }
                }
            }

            @Override
            public double getAspectRatio() {
                return 0;
            }

            @Override
            public void setVideoLifecycleCallbacks(YumiVideoLifecycleCallbacks videoLifecycleCallbacks) {
                this.videoLifecycleCallbacks = videoLifecycleCallbacks;
            }

        }
    }
}
