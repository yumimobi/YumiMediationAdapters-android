package com.yumi.android.sdk.ads.adapter.baidu;

import android.app.Activity;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.baidu.mobad.feeds.BaiduNative;
import com.baidu.mobad.feeds.NativeErrorCode;
import com.baidu.mobad.feeds.NativeResponse;
import com.baidu.mobad.feeds.RequestParameters;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.NativeContent;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerNativeAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.device.PhoneInfoGetter;
import com.yumi.android.sdk.ads.utils.file.BitmapDownloadUtil;

import java.util.ArrayList;
import java.util.List;

import static com.yumi.android.sdk.ads.adapter.baidu.BaiduUtil.recodeNativeError;
import static com.yumi.android.sdk.ads.utils.file.BitmapDownloadUtil.loadDrawables;

public class BaiduNativeAdapter extends YumiCustomerNativeAdapter {
    private BaiduNative baiduNative;
    private RequestParameters requestParameters;

    protected BaiduNativeAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }

    @Override
    public void webLayerClickedAndRequestBrowser(String url) {

    }

    @Override
    protected void onPrepareNative() {
        if (baiduNative != null && requestParameters != null) {
            baiduNative.makeRequest(requestParameters);
        }
    }

    @Override
    protected void init() {
        ZplayDebug.i(TAG, "baidu native key1 : " + getProvider().getKey1(), onoff);
        baiduNative = new BaiduNative(getActivity(), getProvider().getKey1(), new BaiduNative.BaiduNativeNetworkListener() {
            @Override
            public void onNativeLoad(List<NativeResponse> list) {
                ZplayDebug.i(TAG, "baidu native onNativeLoad : " + list.size(), onoff);
                getNativeContentList(list);
            }

            @Override
            public void onNativeFail(NativeErrorCode nativeErrorCode) {
                ZplayDebug.i(TAG, "baidu native onNativeFail : " + nativeErrorCode.toString(), onoff);
                LayerErrorCode result;
                if (nativeErrorCode == NativeErrorCode.LOAD_AD_FAILED) {
                    result = LayerErrorCode.ERROR_NO_FILL;
                } else if (nativeErrorCode == NativeErrorCode.CONFIG_ERROR) {
                    result = LayerErrorCode.ERROR_INVALID;
                } else {
                    result = LayerErrorCode.ERROR_INTERNAL;
                }
                layerPreparedFailed(recodeNativeError(result, nativeErrorCode.toString()));
            }
        });

        requestParameters = new RequestParameters.Builder()
                .downloadAppConfirmPolicy(RequestParameters.DOWNLOAD_APP_CONFIRM_NEVER).build();
    }


    private void getNativeContentList(final List<NativeResponse> baiduNativeAdlist) {
        List<NativeContent> nativeContentsList = new ArrayList<>();
        try {
            for (int i = 0; i < baiduNativeAdlist.size(); i++) {
                final NativeResponse adEntity = baiduNativeAdlist.get(i);
                final NativeAdContent nativeAdContent = new NativeAdContent(adEntity);
                if (nativeAdContent.isValid()) {
                    nativeContentsList.add(nativeAdContent);
                }
            }

            if (nativeContentsList.isEmpty()) {
                ZplayDebug.v(TAG, "baidu data is empty", onoff);
                LayerErrorCode request = LayerErrorCode.ERROR_NO_FILL;
                request.setExtraMsg("Baidu Native: baidu ad is no fill");
                layerPreparedFailed(request);
                return;
            }

            if (!getProvider().getNativeAdOptions().getIsDownloadImage()) {
                layerPrepared(nativeContentsList);
                return;
            }

            loadDrawables(getActivity(), nativeContentsList, new BitmapDownloadUtil.DownloadDrawableListener() {
                @Override
                public void onLoaded(List<NativeContent> data) {
                    layerPrepared(data);
                }

                @Override
                public void onFailed() {
                    LayerErrorCode request = LayerErrorCode.ERROR_INTERNAL;
                    request.setExtraMsg("Baidu Native: download image data failed");
                    layerPreparedFailed(request);
                }
            });
        } catch (Exception e) {
            ZplayDebug.e(TAG, "Baidu getNativeContentList error : " + e, onoff);
            layerPreparedFailed(recodeNativeError(LayerErrorCode.ERROR_INTERNAL, "Baidu get Native Content List error"));
        }
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

        private NativeResponse nativeAdData;

        NativeAdContent(NativeResponse nativeAdData) {
            BaiduNativeAdapter.NativeAdContent.this.nativeAdData = nativeAdData;

            setIcon(new Image(nativeAdData.getIconUrl()));
            setImage(new Image(nativeAdData.getImageUrl()));
            setDesc(nativeAdData.getDesc());
            setCallToAction(PhoneInfoGetter.getLanguage().startsWith("zh") ? "查看详情" : "learn more");
            setTitle(nativeAdData.getTitle());
        }

        public void trackView() {
            if (getNativeAdView() == null) {
                ZplayDebug.v(TAG, "baidu native trackView getNativeAdView() is null", onoff);
                return;
            }

            if (!getProvider().getNativeAdOptions().getHideAdAttribution()) {
                TextView adAttribution = new TextView(getNativeAdView().getContext());
                adAttribution.setText(getProvider().getNativeAdOptions().getAdAttributionText());
                adAttribution.setTextColor(getProvider().getNativeAdOptions().getAdAttributionColor());
                adAttribution.setTextSize(getProvider().getNativeAdOptions().getAdAttributionTextSize());
                adAttribution.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                getNativeAdView().addView(adAttribution);
                FrameLayout.LayoutParams adAttributionParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);

                setViewPosition(adAttributionParams, getProvider().getNativeAdOptions().getAdAttributionPosition());
                adAttribution.setLayoutParams(adAttributionParams);
                getNativeAdView().requestLayout();
            }
            layerExposure();
            nativeAdData.recordImpression(getNativeAdView());
            getNativeAdView().setOnClickListener(new ViewGroup.OnClickListener() {
                @Override
                public void onClick(View v) {
                    layerClicked(-99f, -99f);
                    nativeAdData.handleClick(getNativeAdView());
                }
            });
            getNativeAdView().setClickable(true);
            if (getNativeAdView().getCallToActionView() != null) {
                getNativeAdView().getCallToActionView().setOnClickListener(new ViewGroup.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getNativeAdView().performClick();
                    }
                });
            }
        }
    }

}
