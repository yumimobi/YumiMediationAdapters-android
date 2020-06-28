package com.yumi.android.sdk.ads.adapter.bytedance;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerBannerAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.AdSize;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import java.util.List;

import static com.yumi.android.sdk.ads.adapter.bytedance.BytedanceUtil.getAppName;
import static com.yumi.android.sdk.ads.adapter.bytedance.BytedanceUtil.initSDK;
import static com.yumi.android.sdk.ads.adapter.bytedance.BytedanceUtil.recodeError;
import static com.yumi.android.sdk.ads.adapter.bytedance.BytedanceUtil.sdkVersion;
import static com.yumi.android.sdk.ads.utils.device.WindowSizeUtils.isTablet;


public class BytedanceBannerAdapter extends YumiCustomerBannerAdapter {

    private static final String TAG = "BytedanceBannerAdapter";
    private TTAdNative mTTAdNative;
    private int bannerHeight = 50;
    private int bannerWidth = 320;
    private TTAdNative.NativeExpressAdListener bannerAdListener;
    private TTNativeExpressAd mTTAd;

    protected BytedanceBannerAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }

    @Override
    protected void onPrepareBannerLayer() {
        ZplayDebug.d(TAG, "load new banner");
        if (bannerSize == AdSize.BANNER_SIZE_SMART) {
            ZplayDebug.d(TAG, "not support smart banner:");
            layerPreparedFailed(recodeError(-999, "not support smart banner."));
            return;
        }
        calculateBannerSize();
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(getProvider().getKey2())
                .setSupportDeepLink(true)
                .setAdCount(1)
                .setExpressViewAcceptedSize(bannerWidth, bannerHeight)
                .setImageAcceptedSize(bannerWidth, bannerHeight)
                .build();
        mTTAdNative.loadBannerExpressAd(adSlot, bannerAdListener);
    }

    @Override
    protected void init() {
        ZplayDebug.d(TAG, "init key1: " + getProvider().getKey1() + " ,key2: " + getProvider().getKey2());

        initSDK(getActivity(), getProvider().getKey1(), getAppName(getActivity().getPackageManager(), getActivity().getPackageName()));
        mTTAdNative = TTAdSdk.getAdManager().createAdNative(getActivity());//baseContext建议为activity
        createListener();
    }

    private void createListener() {
        bannerAdListener = new TTAdNative.NativeExpressAdListener() {

            @Override
            public void onError(int code, String message) {
                ZplayDebug.d(TAG, "onError：" + message);
                layerPreparedFailed(recodeError(code, message));

            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                ZplayDebug.d(TAG, "onNativeExpressAdLoad");
                if (ads == null || ads.size() == 0) {
                    layerPreparedFailed(recodeError(-999, "TTBannerAd is null"));
                    return;
                }
                mTTAd = ads.get(0);
                if (mTTAd == null) {
                    layerPreparedFailed(recodeError(-999, "bannerView is null"));
                    return;
                }
                if (getProvider().getAutoRefreshInterval() > 0) {
                    mTTAd.setSlideIntervalTime(getProvider().getAutoRefreshInterval());
                }

                bindAdListener(mTTAd);
                mTTAd.render();
            }
        };
    }

    private void bindAdListener(TTNativeExpressAd ad) {
        ad.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
            @Override
            public void onAdClicked(View view, int type) {
                ZplayDebug.d(TAG, "onAdClicked");
                layerClicked(-99f, -99f);
            }

            @Override
            public void onAdShow(View view, int type) {
                ZplayDebug.d(TAG, "onAdShow");
            }

            @Override
            public void onRenderFail(View view, String msg, int code) {
                ZplayDebug.d(TAG, "onAdRenderFail");
                layerPreparedFailed(recodeError(code, "render fail:" + msg));
            }

            @Override
            public void onRenderSuccess(View view, float width, float height) {
                ZplayDebug.d(TAG, "onAdRenderSuccess");
                FrameLayout bannerView = new FrameLayout(getContext());
                FrameLayout.LayoutParams parentParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                parentParams.gravity = Gravity.CENTER_HORIZONTAL;
                bannerView.setLayoutParams(parentParams);
                bannerView.addView(view);
                //返回view的宽高 单位 dp
                layerPrepared(bannerView, true);
            }
        });

    }

    private void calculateBannerSize() {
        if (bannerSize == AdSize.BANNER_SIZE_728X90) {
            bannerWidth = 576;
            bannerHeight = 90;
        } else if (bannerSize == AdSize.BANNER_SIZE_AUTO && isTablet()) {
            bannerWidth = 576;
            bannerHeight = 90;
        } else {
            bannerWidth = 320;
            bannerHeight = 50;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ZplayDebug.d(TAG, "onDestroy");
        if (mTTAd != null) {
            mTTAd.destroy();
        }
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