package com.yumi.android.sdk.ads.adapter.gdtmob;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;

import com.qq.e.ads.nativ.NativeAD;
import com.qq.e.ads.nativ.NativeAD.NativeAdListener;
import com.qq.e.ads.nativ.NativeADDataRef;
import com.qq.e.comm.util.AdError;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.NativeContent;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerNativeAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.file.BitmapDownloadUtil;

import java.util.ArrayList;
import java.util.List;

import static com.yumi.android.sdk.ads.adapter.GdtUtil.recodeError;
import static com.yumi.android.sdk.ads.utils.file.BitmapDownloadUtil.loadDrawables;

/**
 * Created by Administrator on 2017/7/3.
 */
public class GdtmobNativeAdapter extends YumiCustomerNativeAdapter {

    private NativeAD nativeAD;

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
            nativeAD.loadAD(currentPoolSpace);
            ZplayDebug.v(TAG, "GdtnativeAdapter invoke onPrepareInterstitial! currentPoolSpace=" + currentPoolSpace, onoff);
        }
    }

    @Override
    protected void init() {
        nativeAD = new NativeAD(getActivity(), getProvider().getKey1(), getProvider().getKey2(), new NativeAdListener() {
            @Override
            public void onADStatusChanged(NativeADDataRef arg0) {
            }

            @Override
            public void onADLoaded(List<NativeADDataRef> arg0) {
                ZplayDebug.v(TAG, "onADLoaded", onoff);
                final List<NativeContent> list = new ArrayList<>();
                for (final NativeADDataRef item : arg0) {
                    final NativeAdContent content = new NativeAdContent(item);
                    if (content.isValid()) {
                        list.add(content);
                    }
                }
                if (list.isEmpty()) {
                    ZplayDebug.v(TAG, "gdt data is empty", onoff);
                    layerPreparedFailed(recodeError(new AdError(-1, "got gdt native ad, but the ad ")));
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
                    ZplayDebug.d(TAG, "GDT nativead onNoAD adError = null", onoff);
                    layerPreparedFailed(recodeError(null));
                    return;
                }
                ZplayDebug.w(TAG, "GDT nativead onNoAD ErrorCode:" + adError.getErrorCode() + " msg:" + adError.getErrorMsg(), onoff);
                layerPreparedFailed(recodeError(adError));
            }

            @Override
            public void onADError(NativeADDataRef nativeADDataRef, AdError adError) {
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

    private class NativeAdContent extends NativeContent {

        private NativeADDataRef mGdtData;

        private NativeAdContent(NativeADDataRef gdtData) {
            mGdtData = gdtData;
            setTitle(gdtData.getTitle());
            setDesc(gdtData.getDesc());
            setStarRating((double) gdtData.getAPPScore());
            setImage(new Image(gdtData.getImgUrl()));
            setIcon(new Image(gdtData.getIconUrl()));
        }

        /**
         * 内容信息包括必要元素 iconUrl，title, desc, imageUrl
         *
         * @return 包含必要元素，返回 true；否则，返回 false
         */
        boolean isValid() {
            return !TextUtils.isEmpty(getTitle()) &&
                    !TextUtils.isEmpty(getDesc()) &&
                    getIcon() != null && !TextUtils.isEmpty(getIcon().getUrl()) &&
                    getImage() != null && !TextUtils.isEmpty(getImage().getUrl());
        }

        @Override
        public void trackView() {
            mGdtData.onExposured(getAdChoicesContent());
            getNativeAdView().setClickable(true);
            getNativeAdView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mGdtData.onClicked(v);
                }
            });
        }
    }
}
