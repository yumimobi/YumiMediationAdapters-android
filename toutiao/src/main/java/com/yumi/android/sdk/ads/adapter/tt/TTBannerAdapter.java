package com.yumi.android.sdk.ads.adapter.tt;

import android.app.Activity;
import android.view.View;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTBannerAd;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerBannerAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

/**
 * Created by Administrator on 2017/6/27.
 */

public class TTBannerAdapter extends YumiCustomerBannerAdapter{
    private TTAdNative.BannerAdListener bannerListener;
    private TTAppDownloadListener  bannerDownloadListener;
    private TTAdNative mTTAdNative;
    private String TAG = "TTBannerAdapter";
    private Activity activity;
    protected TTBannerAdapter(Activity activity, YumiProviderBean yumiProviderBean) {
        super(activity, yumiProviderBean);
        this.activity = activity;
    }
    @Override
    protected void onPrepareBannerLayer() {
        mTTAdNative = TTAdManagerHolder.getInstance(activity,getProvider().getKey1()).createAdNative(activity);
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(getProvider().getKey2())
                .setSupportDeepLink(true)
                .setImgMaxSize(640, 100)
                .build();
        mTTAdNative.loadBannerAd(adSlot,bannerListener );
    }

    @Override
    protected void init() {
        createBannerListener();

    }

    private void createBannerListener() {
        bannerListener = new TTAdNative.BannerAdListener(){

            @Override
            public void onError(int i, String s) {
                ZplayDebug.d(TAG, "请求广告错误码:"+i+"错误信息:"+s , onoff);
                layerPreparedFailed(decodeErrorCode(s));
            }

            @Override
            public void onBannerAdLoad(TTBannerAd ttBannerAd) {
                if (ttBannerAd == null) {
                    return;
                }
                ZplayDebug.d(TAG, "facebook banner prepared", onoff);
                View bannerView = ttBannerAd.getBannerView();
                layerPrepared(bannerView, true);
                ttBannerAd.setOnBannerClickListener(new TTBannerAd.BannerAdClickListener() {
                    @Override
                    public void onAdClicked(View view, int i) {
                        layerClicked(-99f, -99f);
                    }
                });
                if (bannerView == null) {
                    return;
                }
                if (ttBannerAd.getInteractionType() == TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
                    ttBannerAd.setDownloadListener(bannerDownloadListener);
                }
            }
        };
        bannerDownloadListener = new TTAppDownloadListener() {
            @Override
            public void onIdle() {
                ZplayDebug.d(TAG, "点击下载" , onoff);
            }

            @Override
            public void onDownloadActive(long l, long l1, String s) {
                ZplayDebug.d(TAG, "下载开始" , onoff);
            }

            @Override
            public void onDownloadPaused(long l, long l1, String s) {
                ZplayDebug.d(TAG, "下载暂停" , onoff);
            }

            @Override
            public void onDownloadFailed(long l, long l1, String s) {
                ZplayDebug.d(TAG, "下载失败" , onoff);
            }

            @Override
            public void onDownloadFinished(long l, String s) {
                ZplayDebug.d(TAG, "下载完成" , onoff);
            }

            @Override
            public void onInstalled(String s) {
                ZplayDebug.d(TAG, "安装完成" , onoff);
            }
        };

    }

    protected LayerErrorCode decodeErrorCode(String arg1) {
        if (arg1.equals("ERROR_CODE_NET_ERROR")) {
            return LayerErrorCode.ERROR_NETWORK_ERROR;
        }
        if (arg1.equals("ERROR_CODE_NO_AD")) {
            return LayerErrorCode.ERROR_NO_FILL;
        }
        if (arg1.equals("ERROR_CODE_NO_AD")) {
            return LayerErrorCode.ERROR_NO_FILL;
        }
        return LayerErrorCode.ERROR_INTERNAL;
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


}
