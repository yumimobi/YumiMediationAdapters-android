package com.yumi.android.sdk.ads.natives.adapter.gdtmob;

import android.app.Activity;
import android.view.ViewGroup;

import com.qq.e.ads.nativ.NativeAD;
import com.qq.e.ads.nativ.NativeAD.NativeAdListener;
import com.qq.e.ads.nativ.NativeADDataRef;
import com.qq.e.comm.util.AdError;
import com.yumi.android.sdk.ads.adapter.ErrorCodeHelp;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.NativeContent;
import com.yumi.android.sdk.ads.publish.NativeReportRunnable;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerNativeAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/3.
 */
public class GdtmobNativeAdapter extends YumiCustomerNativeAdapter{

    private NativeAD nativeAD;
    private static final String DEFAULT_JUMPURL = "http://zplay.android.com/jump";

    protected GdtmobNativeAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }

    @Override
    public void webLayerClickedAndRequestBrowser(String s) {

    }

    @Override
    protected void onPrepareNative() {
        if (nativeAD!=null)
        {
//			int feedsAdNumber = getProvider().getGlobal().getFeedsAdNumber();
            int currentPoolSpace = getCurrentPoolSpace();
            nativeAD.loadAD(currentPoolSpace);
            ZplayDebug.v(TAG, "GdtnativeAdapter invoke onPrepareInterstitial! currentPoolSpace="+currentPoolSpace, onoff);
        }
    }

    @Override
    protected void init() {
        nativeAD = new NativeAD(getActivity(), getProvider().getKey1(), getProvider().getKey2(), new NativeAdListener()
        {
            @Override
            public void onADStatusChanged(NativeADDataRef arg0)
            {
            }
            @Override
            public void onADLoaded(List<NativeADDataRef> arg0)
            {
                ZplayDebug.v(TAG, "onADLoaded", onoff);
                List<NativeContent> list = new ArrayList<>();
                for (final NativeADDataRef item : arg0)
                {
                    NativeContent content = new NativeContent();
                    content.setIcon_url(item.getIconUrl());
                    content.setImg_url(item.getImgUrl());
                    content.setDesc(item.getDesc());
                    content.setTitle(item.getTitle());
                    content.setJumpUrl(DEFAULT_JUMPURL);
                    content.setImg_width(1280);
                    content.setImg_height(720);
                    content.setReportShowRunnable(new NativeReportRunnable()
                    {
                        @Override
                        public void run(ViewGroup view,NativeContent nativeContent)
                        {
                            layerExposure();
                            item.onExposured(view);
                        }
                    });
                    content.setReportClickRunnable(new NativeReportRunnable() {
                        @Override
                        public void run(ViewGroup viewGroup, NativeContent nativeContent) {

                            layerClicked(-99f, -99f);
                            item.onClicked(viewGroup);
                        }
                    });
                    list.add(content);
                }
                ZplayDebug.v(TAG, "adprepared length = "+list.size(), onoff);
                layerPrepared(list);
            }

            @Override
            public void onNoAD(AdError adError) {
                if (adError == null){
                    ZplayDebug.d(TAG, "GDT nativead onNoAD adError = null", onoff);
                    layerPreparedFailed(LayerErrorCode.ERROR_INTERNAL);
                    return;
                }
                ZplayDebug.w(TAG, "GDT nativead onNoAD ErrorCode:" + adError.getErrorCode() + " msg:" + adError.getErrorMsg(), onoff);
                layerPreparedFailed(ErrorCodeHelp.decodeErrorCode(adError.getErrorCode()));
            }

            @Override
            public void onADError(NativeADDataRef nativeADDataRef, AdError adError) {
                if (adError == null){
                    ZplayDebug.d(TAG, "GDT nativead onADError adError = null", onoff);
                    layerPreparedFailed(LayerErrorCode.ERROR_INTERNAL);
                    return;
                }
                ZplayDebug.d(TAG, "GDT nativead onADError ErrorCode:" + adError.getErrorCode() + " msg:" + adError.getErrorMsg(), onoff);
                layerPreparedFailed(ErrorCodeHelp.decodeErrorCode(adError.getErrorCode()));
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
}
