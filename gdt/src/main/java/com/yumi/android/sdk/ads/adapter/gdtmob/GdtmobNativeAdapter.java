package com.yumi.android.sdk.ads.adapter.gdtmob;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.nativ.MediaView;
import com.qq.e.ads.nativ.NativeADEventListener;
import com.qq.e.ads.nativ.NativeADMediaListener;
import com.qq.e.ads.nativ.NativeADUnifiedListener;
import com.qq.e.ads.nativ.NativeExpressAD.NativeExpressADListener;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.ads.nativ.NativeExpressMediaListener;
import com.qq.e.ads.nativ.NativeUnifiedADData;
import com.qq.e.ads.nativ.widget.NativeAdContainer;
import com.qq.e.comm.constants.AdPatternType;
import com.qq.e.comm.util.AdError;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.formats.YumiNativeAdVideoController;
import com.yumi.android.sdk.ads.formats.YumiNativeAdView;
import com.yumi.android.sdk.ads.publish.NativeContent;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerNativeAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.device.PhoneInfoGetter;
import com.yumi.android.sdk.ads.utils.file.BitmapDownloadUtil;

import java.util.ArrayList;
import java.util.List;

import static com.yumi.android.sdk.ads.adapter.GdtUtil.recodeError;
import static com.yumi.android.sdk.ads.adapter.GdtUtil.sdkVersion;
import static com.yumi.android.sdk.ads.utils.file.BitmapDownloadUtil.loadDrawables;

/**
 * Created by Administrator on 2017/7/3.
 */
public class GdtmobNativeAdapter extends YumiCustomerNativeAdapter {
    private static final String TAG = "GdtmobNativeAdapter";
    private List<NativeContent> list = new ArrayList<>();

    protected GdtmobNativeAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }

    @Override
    public void webLayerClickedAndRequestBrowser(String s) {

    }

    @Override
    protected void onPrepareNative() {
        int currentPoolSpace = getCurrentPoolSpace();
        ZplayDebug.i(TAG, "load new native");
        if (getProvider().getExtraData("GDTRenderModel").equals("1")) {
            GdtmobNativeHolder.getInstance().loadNativeUnifiedAD(currentPoolSpace);
        } else {
            GdtmobNativeHolder.getInstance().loadNativeExpressAD(currentPoolSpace);
        }
    }

    @Override
    protected void init() {
        ZplayDebug.v(TAG, "init key1 = " + getProvider().getKey1() + ", key2 = " + getProvider().getKey2());
        if (getProvider().getExtraData("GDTRenderModel").equals("1")) {
            NativeADUnifiedListener nativeADUnifiedListener = new NativeADUnifiedListener() {
                @Override
                public void onADLoaded(List<NativeUnifiedADData> adlist) {
                    ZplayDebug.v(TAG, "onADLoaded");
                    final List<NativeContent> list = new ArrayList<>();
                    for (final NativeUnifiedADData item : adlist) {
                        try {
                            final GdtmobNativeAdapter.NativeAdContent content = new GdtmobNativeAdapter.NativeAdContent(item);
                            if (content.isValid()) {
                                list.add(content);
                            }
                        } catch (Exception e) {
                            ZplayDebug.e(TAG, "data parse error : " + e);
                        }
                    }
                    if (list.isEmpty()) {
                        ZplayDebug.v(TAG, "data is empty");
                        layerPreparedFailed(recodeError(new AdError(-1, "got native ad, but the ad ")));
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
                            layerPreparedFailed(recodeError(new AdError(-1, "got native ad, but cannot download image data")));
                        }
                    });
                }

                @Override
                public void onNoAD(AdError adError) {
                    if (adError == null) {
                        ZplayDebug.d(TAG, "onNoAD adError = null");
                        layerPreparedFailed(recodeError(null));
                        return;
                    }
                    ZplayDebug.d(TAG, "onNoAD ErrorCode:" + adError.getErrorCode() + " msg:" + adError.getErrorMsg());
                    layerPreparedFailed(recodeError(adError));
                }
            };
            GdtmobNativeHolder.getInstance().initNativeUnifiedAD(getActivity(), getProvider().getKey1(), getProvider().getKey2(), nativeADUnifiedListener);

        } else {
            NativeExpressADListener nativeExpressADListener = new NativeExpressADListener() {
                @Override
                public void onADLoaded(List<NativeExpressADView> adlist) {
                    ZplayDebug.v(TAG, "express nativeAd onADLoaded");

                    for (final NativeExpressADView item : adlist) {
                        try {
                            final NativeExpressAdContent content = new NativeExpressAdContent(item);
                            list.add(content);
                        } catch (Exception e) {
                            ZplayDebug.e(TAG, "express data parse error : " + e);
                        }
                    }

                    if (list.isEmpty()) {
                        ZplayDebug.v(TAG, "express data is empty");
                        layerPreparedFailed(recodeError(new AdError(-1, "get express native ad, but the ad is empty")));
                        return;
                    }

                    layerPrepared(list);

                }

                @Override
                public void onRenderFail(NativeExpressADView nativeExpressADView) {
                    ZplayDebug.v(TAG, "express nativeAd onExpressAdRenderFail");
                    for (NativeContent content : list) {
                        if (content.getExpressAdView() == nativeExpressADView) {
                            layerExpressAdRenderFail(content, "express nativeAd RenderFail");
                        }
                    }
                }

                @Override
                public void onRenderSuccess(NativeExpressADView nativeExpressADView) {
                    ZplayDebug.v(TAG, "express nativeAd onExpressAdRenderSuccess");
                    for (NativeContent content : list) {
                        if (content.getExpressAdView() == nativeExpressADView) {
                            layerExpressAdRenderSuccess(content);
                        }
                    }
                }

                @Override
                public void onADExposure(NativeExpressADView nativeExpressADView) {
                    ZplayDebug.v(TAG, "express nativeAd onADExposure");
                    layerExposure();
                }

                @Override
                public void onADClicked(NativeExpressADView nativeExpressADView) {
                    ZplayDebug.v(TAG, "express nativeAd onADClicked");
                    layerClicked(-99f, -99f);
                }

                @Override
                public void onADClosed(NativeExpressADView nativeExpressADView) {
                    ZplayDebug.v(TAG, "express nativeAd onExpressAdClosed");
                    for (NativeContent content : list) {
                        if (content.getExpressAdView() == nativeExpressADView) {
                            layerExpressAdClosed(content);
                            nativeExpressADView.destroy();
                        }
                    }
                }

                @Override
                public void onADLeftApplication(NativeExpressADView nativeExpressADView) {
                    ZplayDebug.v(TAG, "express nativeAd onADLeftApplication");
                }

                @Override
                public void onADOpenOverlay(NativeExpressADView nativeExpressADView) {
                    ZplayDebug.v(TAG, "express nativeAd onADOpenOverlay");
                }

                @Override
                public void onADCloseOverlay(NativeExpressADView nativeExpressADView) {
                    ZplayDebug.v(TAG, "express nativeAd onADCloseOverlay");
                }

                @Override
                public void onNoAD(AdError adError) {
                    if (adError == null) {
                        ZplayDebug.d(TAG, "onNoAD adError = null");
                        layerPreparedFailed(recodeError(null));
                        return;
                    }
                    ZplayDebug.d(TAG, "express nativead onADError ErrorCode:" + adError.getErrorCode() + " msg:" + adError.getErrorMsg());
                    layerPreparedFailed(recodeError(adError));
                }
            };
            GdtmobNativeHolder.getInstance().initNativeExpressAD(getActivity(), getProvider().getKey1(), getProvider().getKey2(), getProvider().getNativeAdOptions(), nativeExpressADListener);
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

    private class NativeExpressAdContent extends NativeContent {
        private NativeExpressADView expressADView;

        private NativeExpressAdContent(NativeExpressADView expressADView) {
            this.expressADView = expressADView;
            if (expressADView.getBoundData() != null) {
                setTitle(expressADView.getBoundData().getTitle());
                setDesc(expressADView.getBoundData().getDesc());
            }
            setExpressAdView(expressADView);
            setMaterialCreationTime(System.currentTimeMillis());
            setMaterialEtime(getProvider().getMaterialEtime());
            setHasVideoContent(expressADView.getBoundData().getAdPatternType() == AdPatternType.NATIVE_VIDEO);
            setProviderName(getProvider().getProviderName());
            setSpecifiedProvider(getProvider().getSpecifiedProvider());
            setIsExpressAdView(true);
            if (expressADView.getBoundData().getAdPatternType() == AdPatternType.NATIVE_VIDEO) {
                setNativeAdVideoController(new YumiNativeAdVideoController());
            }
        }

        @Override
        public void trackView() {
            if (expressADView.getBoundData().getAdPatternType() == AdPatternType.NATIVE_VIDEO) {
                setNativeAdVideoController(new GdtExpressNativeViewController(expressADView));
            }
            expressADView.render();

        }

        @Override
        public void destroy() {
            ZplayDebug.v(TAG, "express native destory");
            if (expressADView != null) {
                expressADView.destroy();
            }
            if (list != null && list.contains(NativeExpressAdContent.this)) {
                list.remove(NativeExpressAdContent.this);
            }
        }

        public class GdtExpressNativeViewController extends YumiNativeAdVideoController {
            YumiVideoLifecycleCallbacks videoLifecycleCallbacks;

            private GdtExpressNativeViewController(NativeExpressADView expressADView) {
                ZplayDebug.v(TAG, "express native Adapter ");
                expressADView.setMediaListener(new NativeExpressMediaListener() {
                    @Override
                    public void onVideoInit(NativeExpressADView nativeExpressADView) {
                        ZplayDebug.v(TAG, "express ad native Adapter onVideoInit");
                    }

                    @Override
                    public void onVideoLoading(NativeExpressADView nativeExpressADView) {
                        ZplayDebug.v(TAG, "express ad native Adapter onVideoLoading");
                    }

                    @Override
                    public void onVideoReady(NativeExpressADView nativeExpressADView, long l) {
                        ZplayDebug.v(TAG, "express ad native Adapter onVideoReady");
                    }

                    @Override
                    public void onVideoStart(NativeExpressADView nativeExpressADView) {
                        ZplayDebug.v(TAG, "express ad native Adapter onVideoStart");
                        if (videoLifecycleCallbacks != null) {
                            videoLifecycleCallbacks.onVideoPlay();
                        }
                    }

                    @Override
                    public void onVideoPause(NativeExpressADView nativeExpressADView) {
                        ZplayDebug.v(TAG, "express ad native Adapter onVideoPause");
                        if (videoLifecycleCallbacks != null) {
                            videoLifecycleCallbacks.onVideoPause();
                        }
                    }

                    @Override
                    public void onVideoComplete(NativeExpressADView nativeExpressADView) {
                        ZplayDebug.v(TAG, "express ad native Adapter onVideoComplete");
                        if (videoLifecycleCallbacks != null) {
                            videoLifecycleCallbacks.onVideoEnd();
                        }
                    }

                    @Override
                    public void onVideoError(NativeExpressADView nativeExpressADView, AdError adError) {
                        ZplayDebug.v(TAG, "express ad native Adapter onVideoError");
                    }

                    @Override
                    public void onVideoPageOpen(NativeExpressADView nativeExpressADView) {
                        ZplayDebug.v(TAG, "express ad native Adapter onVideoPageOpen");
                    }

                    @Override
                    public void onVideoPageClose(NativeExpressADView nativeExpressADView) {
                        ZplayDebug.v(TAG, "express ad native Adapter onVideoPageClose");
                    }
                });
            }

            @Override
            public void play() {
            }

            @Override
            public void pause() {
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
            setProviderName(getProvider().getProviderName());
            setSpecifiedProvider(getProvider().getSpecifiedProvider());
            setIsExpressAdView(false);
        }

        @Override
        public void trackView() {
            if (getNativeAdView() == null) {
                ZplayDebug.v(TAG, "native trackView getNativeAdView() is null");
                return;
            }

            YumiNativeAdView yumiNativeAdView = getNativeAdView();
            ViewGroup parent = (ViewGroup) yumiNativeAdView.getParent();
            parent.removeView(yumiNativeAdView);

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
                    ZplayDebug.v(TAG, "native Adapter onADExposed");
                    layerExposure();
                }

                @Override
                public void onADClicked() {
                    ZplayDebug.v(TAG, "native Adapter onADClicked");
                    layerClicked(-99f, -99f);

                }

                @Override
                public void onADError(AdError adError) {
                    ZplayDebug.v(TAG, "native Adapter onADError" + adError.getErrorMsg());
                }

                @Override
                public void onADStatusChanged() {
                    ZplayDebug.v(TAG, "native Adapter onADStatusChanged");
                }
            });
            ZplayDebug.v(TAG, "native Adapter AdPatternType : " + mGdtData.getAdPatternType());
            if (mGdtData.getAdPatternType() == AdPatternType.NATIVE_VIDEO) {
                if (mediaview != null) {
                    setNativeAdVideoController(new GdtNativeViewController(mGdtData, mediaview));
                }
            }

        }

        @Override
        public void onResume() {
            ZplayDebug.v(TAG, "native Adapter onResume");
            if (mGdtData != null) {
                try {
                    mGdtData.resume();
                } catch (Exception e) {
                    ZplayDebug.d(TAG, "onResume: " + e);
                }
            }
        }

        @Override
        public void destroy() {
            ZplayDebug.v(TAG, "native destory");
            if (mGdtData != null) {
                mGdtData.destroy();
            }
        }

        public class GdtNativeViewController extends YumiNativeAdVideoController {
            YumiVideoLifecycleCallbacks videoLifecycleCallbacks;
            private NativeUnifiedADData gdtData;

            private GdtNativeViewController(NativeUnifiedADData gdtData, MediaView mediaview) {
                this.gdtData = gdtData;
                ZplayDebug.v(TAG, "native Adapter AdPatternType : bindMediaView");
                gdtData.bindMediaView(mediaview, new VideoOption.Builder()
                        .setAutoPlayMuted(true).setAutoPlayPolicy(VideoOption.AutoPlayPolicy.WIFI).build(), new NativeADMediaListener() {
                    @Override
                    public void onVideoInit() {
                        ZplayDebug.v(TAG, "native Adapter onVideoInit");
                    }

                    @Override
                    public void onVideoLoading() {
                        ZplayDebug.v(TAG, "native Adapter onVideoLoading");
                    }

                    @Override
                    public void onVideoReady() {
                        ZplayDebug.v(TAG, "native Adapter onVideoReady");
                    }

                    @Override
                    public void onVideoLoaded(int videoDuration) {
                        ZplayDebug.v(TAG, "native Adapter onVideoLoaded");
                    }

                    @Override
                    public void onVideoStart() {
                        ZplayDebug.v(TAG, "native Adapter onVideoStart");
                        if (videoLifecycleCallbacks != null) {
                            videoLifecycleCallbacks.onVideoPlay();
                        }
                    }

                    @Override
                    public void onVideoPause() {
                        ZplayDebug.v(TAG, "native Adapter onVideoPause");
                        if (videoLifecycleCallbacks != null) {
                            videoLifecycleCallbacks.onVideoPause();
                        }
                    }

                    @Override
                    public void onVideoResume() {
                        ZplayDebug.v(TAG, "native Adapter onVideoResume");
                    }

                    @Override
                    public void onVideoCompleted() {
                        ZplayDebug.v(TAG, "native Adapter onVideoCompleted");
                        if (videoLifecycleCallbacks != null) {
                            videoLifecycleCallbacks.onVideoEnd();
                        }
                    }

                    @Override
                    public void onVideoError(AdError error) {
                        ZplayDebug.v(TAG, "native Adapter onVideoError" + error.getErrorMsg());
                    }

                    @Override
                    public void onVideoStop() {
                        ZplayDebug.v(TAG, "native Adapter onVideoStop");
                    }

                    @Override
                    public void onVideoClicked() {
                        ZplayDebug.v(TAG, "native Adapter onVideoClicked");
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

    @Override
    public String getProviderVersion() {
        return sdkVersion();
    }
}
