package com.yumi.android.sdk.ads.adapter.baidu;

import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;

import com.baidu.mobad.feeds.NativeErrorCode;
import com.yumi.android.sdk.ads.publish.AdError;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;

/**
 * Description:
 * <p>
 * Created by lgd on 2019/1/23.
 */
class BaiduUtil {

    static AdError recodeError(String baiduErrorMes) {
        AdError error = new AdError(LayerErrorCode.ERROR_INTERNAL);
        error.setErrorMessage("Baidu errorMsg: " + baiduErrorMes);
        return error;
    }

    static AdError recodeNativeError(NativeErrorCode nativeErrorCode, String errMsg) {
        LayerErrorCode errCode;
        if (nativeErrorCode == NativeErrorCode.LOAD_AD_FAILED) {
            errCode = LayerErrorCode.ERROR_NO_FILL;
        } else if (nativeErrorCode == NativeErrorCode.CONFIG_ERROR) {
            errCode = LayerErrorCode.ERROR_INVALID;
        } else {
            errCode = LayerErrorCode.ERROR_INTERNAL;
        }

        AdError result = new AdError(errCode);
        result.setErrorMessage("Baidu errorMsg: " + errMsg);
        return result;
    }

    static int dp2px(float dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return ((int) (dp * density + 0.5f));
    }

    static boolean isTablet() {
        if (Build.VERSION.SDK_INT >= 17) {
            DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
            float density = displayMetrics.density;
            double inch = Math.sqrt(Math.pow(displayMetrics.widthPixels, 2) + Math.pow(displayMetrics.heightPixels, 2)) / (160 * density);
            return inch >= 8.0d;
        }
        return isApproximateTablet();
    }

    private static boolean isApproximateTablet() {
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        float density = displayMetrics.density;
        double inch = Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2)) / (160 * density);
        return inch >= 8.0d;
    }

    static String sdkVersion(){
        return "5.85";
    }
}
