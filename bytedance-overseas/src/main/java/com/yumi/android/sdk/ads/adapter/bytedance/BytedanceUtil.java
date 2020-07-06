package com.yumi.android.sdk.ads.adapter.bytedance;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.yumi.android.sdk.ads.publish.AdError;
import com.yumi.android.sdk.ads.publish.YumiSettings;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.publish.enumbean.YumiGDPRStatus;

/**
 * Description:
 * <p>
 * Created by lgd on 2019/1/23.
 */
public class BytedanceUtil {
    public static AdError recodeError(int errorCode, String errMags) {
        LayerErrorCode errCode;
        if (errorCode == -999) {
            errCode = LayerErrorCode.ERROR_INTERNAL;
        } else {
            errCode = LayerErrorCode.ERROR_NO_FILL;
        }

        AdError adError = new AdError(errCode);
        adError.setErrorMessage("Bytedance errorCode: " + errorCode + ", errorMessage: " + errMags);
        return adError;
    }

    public static AdError recodeNativeAdError(int errorCode, String errMags) {
        LayerErrorCode errCode;
        errCode = LayerErrorCode.ERROR_NO_FILL;

        AdError adError = new AdError(errCode);
        adError.setErrorMessage("Bytedance errorCode: " + errorCode + ", errorMessage: " + errMags);
        return adError;
    }

    public static String getAppName(PackageManager pm, String pkg) {
        try {
            ApplicationInfo info = pm.getApplicationInfo(pkg, 0);
            return info.loadLabel(pm).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void initSDK(Activity activity,String appid, String appName){
        int gdpr = 0;
        if (YumiSettings.getGDPRStatus() != YumiGDPRStatus.NON_PERSONALIZED) {
            gdpr = 1;
        }

        TTAdSdk.init(activity,
                new TTAdConfig.Builder()
                        .appId(appid)
                        .useTextureView(false)
                        .appName(appName)
                        .titleBarTheme(TTAdConstant.TITLE_BAR_THEME_DARK)
                        .allowShowPageWhenScreenLock(false)
                        .debug(false)
                        .supportMultiProcess(false)
                        .coppa(0)
                        .setGDPR(gdpr)
                        .build());
    }

    static String sdkVersion() {
        return "2.9.0.3";
    }
}
