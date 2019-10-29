package com.yumi.android.sdk.ads.adapter.gdtmob;

import android.app.Activity;
import android.content.Context;

import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeADUnifiedListener;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressAD.NativeExpressADListener;
import com.qq.e.ads.nativ.NativeUnifiedAD;
import com.yumi.android.sdk.ads.formats.YumiNativeAdOptions;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

public class GdtmobNativeHolder {
    private static String TAG = "GdtmobNativeHolder";
    private boolean onoff = true;
    private static GdtmobNativeHolder instance;
    private NativeUnifiedAD nativeAD;

    private NativeExpressAD nativeExpressAD;

    public static GdtmobNativeHolder getInstance() {
        if (instance == null) {
            instance = new GdtmobNativeHolder();
        }
        return instance;
    }


    public void initNativeUnifiedAD(Activity mActivity, String key1, String key2, NativeADUnifiedListener unifiedListener) {
        nativeAD = new NativeUnifiedAD(mActivity, key1, key2, unifiedListener);
    }

    public void loadNativeUnifiedAD(int currentPoolSpace) {
        if (nativeAD != null) {
            ZplayDebug.v(TAG, "nativeUnifiedAD onPrepareNative adCount=" + currentPoolSpace, onoff);
            nativeAD.loadData(currentPoolSpace);
        }
    }

    public void initNativeExpressAD(Activity mActivity, String key1, String key2, YumiNativeAdOptions nativeAdOptions, NativeExpressADListener expressADListeners) {
        nativeExpressAD = new NativeExpressAD(mActivity, new ADSize(nativeAdOptions.getExpressAdSize().getWidth(), nativeAdOptions.getExpressAdSize().getHeight()), key1, key2, expressADListeners); // 传入Activity
        // 注意：如果您在联盟平台上新建原生模板广告位时，选择了支持视频，那么可以进行个性化设置（可选）
        nativeExpressAD.setVideoOption(new VideoOption.Builder()
                .setAutoPlayPolicy(VideoOption.AutoPlayPolicy.WIFI) // WIFI 环境下可以自动播放视频
                .setAutoPlayMuted(true) // 自动播放时为静音
                .build());
    }

    public void loadNativeExpressAD(int currentPoolSpace) {
        if (nativeExpressAD != null) {
            ZplayDebug.v(TAG, "nativeExpressAD onPrepareNative adCount=" + currentPoolSpace, onoff);
            nativeExpressAD.loadAD(currentPoolSpace);
        }
    }
}
