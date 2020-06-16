package com.yumi.android.sdk.ads.adapter.bytedance;

import android.app.Activity;
import android.view.View;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.NativeContent;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerNativeAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import java.util.ArrayList;
import java.util.List;

import static com.yumi.android.sdk.ads.adapter.bytedance.BytedanceUtil.getAppName;
import static com.yumi.android.sdk.ads.adapter.bytedance.BytedanceUtil.initSDK;
import static com.yumi.android.sdk.ads.adapter.bytedance.BytedanceUtil.recodeError;
import static com.yumi.android.sdk.ads.adapter.bytedance.BytedanceUtil.recodeNativeAdError;
import static com.yumi.android.sdk.ads.adapter.bytedance.BytedanceUtil.sdkVersion;

public class BytedanceNativeAdapter extends YumiCustomerNativeAdapter {

    private TTAdNative mTTAdNative;
    private TTAdNative.NativeExpressAdListener loadAdListener;
    private List<NativeContent> nativeContentsList;
    private int nativeAdsSize = 0;

    protected BytedanceNativeAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }

    @Override
    public void webLayerClickedAndRequestBrowser(String url) {

    }

    @Override
    protected void onPrepareNative() {
        ZplayDebug.d(TAG, "load new native");
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(getProvider().getKey2())
                .setSupportDeepLink(true)
                .setExpressViewAcceptedSize(1080, 0)
                .setImageAcceptedSize(640, 320)
                .setAdCount(getCurrentPoolSpace()) //请求广告数量为1到3条
                .build();
        mTTAdNative.loadNativeExpressAd(adSlot, loadAdListener);
    }

    @Override
    protected void init() {
        ZplayDebug.d(TAG, "init key1: " + getProvider().getKey1() + " ,key2: " + getProvider().getKey2());

        initSDK(getActivity(), getProvider().getKey1(), getAppName(getActivity().getPackageManager(), getActivity().getPackageName()));
        mTTAdNative = TTAdSdk.getAdManager().createAdNative(getActivity());//baseContext建议为activity
        createrListener();
    }

    private void createrListener() {
        loadAdListener = new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int code, String message) {
                ZplayDebug.d(TAG, "onRewardVideoAdLoad: " + message);
                layerPreparedFailed(recodeError(code, message));
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                if (ads == null || ads.isEmpty()) {
                    return;
                }
                ZplayDebug.d(TAG, "onFeedAdLoad");
                getNativeContentList(ads);

            }
        };
    }

    private void getNativeContentList(final List<TTNativeExpressAd> bytedanceNativeExpressAd) {
        nativeContentsList = new ArrayList<>();
        try {
            nativeAdsSize = 0;
            for (int i = 0; i < bytedanceNativeExpressAd.size(); i++) {
                try {
                    final TTNativeExpressAd adEntity = bytedanceNativeExpressAd.get(i);
                    final TTNativeAdContent nativeAdContent = new TTNativeAdContent(adEntity);
                    adEntity.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
                        @Override
                        public void onAdClicked(View view, int i) {
                            ZplayDebug.v(TAG, "onAdClicked");
                            layerClicked(-99f, -99f);
                        }

                        @Override
                        public void onAdShow(View view, int i) {
                            ZplayDebug.v(TAG, "onAdShow");
                            layerExposure();
                        }

                        @Override
                        public void onRenderFail(View view, String s, int i) {
                            ZplayDebug.v(TAG, "onRenderFail: " + s);
                            nativeAdsSize++;
                            if (nativeAdsSize == bytedanceNativeExpressAd.size()) {
                                if (nativeContentsList.isEmpty()) {
                                    ZplayDebug.v(TAG, "bytedance data is empty");
                                    layerPreparedFailed(recodeNativeAdError(0, "bytedance ad is no fill"));
                                    return;
                                }

                                layerPrepared(nativeContentsList);
                            }
                        }

                        @Override
                        public void onRenderSuccess(View view, float v, float v1) {
                            ZplayDebug.v(TAG, "onRenderSuccess");
                            if (nativeAdContent.isValid()) {
                                nativeAdContent.setExpressAdView(view);
                                nativeContentsList.add(nativeAdContent);
                            }
                            nativeAdsSize++;

                            if (nativeAdsSize == bytedanceNativeExpressAd.size()) {
                                if (nativeContentsList.isEmpty()) {
                                    ZplayDebug.v(TAG, "bytedance data is empty");
                                    layerPreparedFailed(recodeNativeAdError(0, "bytedance ad is no fill"));
                                    return;
                                }

                                layerPrepared(nativeContentsList);
                            }
                        }
                    });
                    adEntity.render();
                } catch (Exception e) {
                    ZplayDebug.e(TAG, "bytedance data parse error : " + e);
                }
            }
        } catch (Exception e) {
            ZplayDebug.e(TAG, "bytedance getNativeContentList error : " + e);
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

    class TTNativeAdContent extends NativeContent {

        private TTNativeExpressAd nativeExpressAd;

        TTNativeAdContent(TTNativeExpressAd nativeExpressAd) {

            BytedanceNativeAdapter.TTNativeAdContent.this.nativeExpressAd = nativeExpressAd;
            setMaterialCreationTime(System.currentTimeMillis());
            setMaterialEtime(getProvider().getMaterialEtime());
            setProviderName(getProvider().getProviderName());
            setSpecifiedProvider(getProvider().getSpecifiedProvider());
            setIsExpressAdView(true);
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public void trackView() {

        }

        @Override
        public void destroy() {
            super.destroy();
            if (nativeExpressAd != null) {
                nativeExpressAd.destroy();
            }
        }
    }

    @Override
    public String getProviderVersion() {
        return sdkVersion();
    }
}
