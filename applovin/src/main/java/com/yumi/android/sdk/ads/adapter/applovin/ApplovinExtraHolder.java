package com.yumi.android.sdk.ads.adapter.applovin;

import android.app.Activity;

import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkSettings;
import com.yumi.android.sdk.ads.publish.YumiDebug;

/**
 * Created by hjl on 2018/6/21.
 */
public class ApplovinExtraHolder {
    private static AppLovinSdk sdk = null;

    public static AppLovinSdk getAppLovinSDK(Activity activity, String appkey) {
        if (sdk == null) {
            AppLovinSdkSettings settings = new AppLovinSdkSettings();
            settings.setVerboseLogging(YumiDebug.isDebugMode());
//            settings.setBannerAdRefreshSeconds(15);  设置banner刷新秒数
            sdk = AppLovinSdk.getInstance(appkey, settings, activity);
        }
        return sdk;
    }

    public static void destroyHolder() {
        sdk = null;
    }
}
