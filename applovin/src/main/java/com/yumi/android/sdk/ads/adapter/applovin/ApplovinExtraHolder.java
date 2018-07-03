package com.yumi.android.sdk.ads.adapter.applovin;

import android.app.Activity;

import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkSettings;
import com.yumi.android.sdk.ads.publish.YumiDebug;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

/**
 * Created by hjl on 2018/6/21.
 */
public class ApplovinExtraHolder {

    private static final String TAG = "ApplovinExtraHolder";
    private static AppLovinSdk sdk = null;

    public static AppLovinSdk getAppLovinSDK(Activity activity, String appkey) {
        try {
            if (sdk == null && appkey != null && !"".equals(appkey)) {
                AppLovinSdkSettings settings = new AppLovinSdkSettings();
                settings.setVerboseLogging(YumiDebug.isDebugMode());
//            settings.setBannerAdRefreshSeconds(15);  设置banner刷新秒数
                sdk = AppLovinSdk.getInstance(appkey, settings, activity);
            }
        } catch (Throwable e) {
            ZplayDebug.e(TAG, "AppLovin getAppLovinSDK  error", e, true);
        }
        return sdk;
    }

    public static void destroyHolder() {
        sdk = null;
    }
}
