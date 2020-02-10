package com.yumi.android.sdk.ads.adapter.bytedance;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.yumi.android.sdk.ads.publish.AdError;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;

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

    static String sdkVersion() {
        return "2.7.5.2";
    }
}
