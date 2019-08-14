package com.yumi.android.sdk.ads.adapter.pubnative;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;

import com.yumi.android.sdk.ads.publish.AdError;
import com.yumi.android.sdk.ads.publish.YumiDebug;
import com.yumi.android.sdk.ads.publish.YumiSettings;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.publish.enumbean.YumiGDPRStatus;

import net.pubnative.lite.sdk.PNLite;

public class PubNativeUtil {


    static AdError recodeError(String errMsg) {
        LayerErrorCode errCode = LayerErrorCode.ERROR_NO_FILL;
        AdError result = new AdError(errCode);
        result.setErrorMessage("Pubnative errorMsg: " + errMsg);
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

    static void initPubNativeSDK(String appToken, Activity activity) {
        PNLite.initialize(appToken, activity.getApplication());
        PNLite.setTestMode(YumiDebug.isDebugMode());

    }

    static void updateGDPRStatus() {
        if (YumiSettings.getGDPRStatus() == YumiGDPRStatus.UNKNOWN) {
            return;
        }
        boolean isConsent = YumiSettings.getGDPRStatus() == YumiGDPRStatus.PERSONALIZED;
        //https://developers.pubnative.net/docs/hybid-android-sdk-gdpr-configuration
        if (isConsent) {
            PNLite.getUserDataManager().grantConsent();
        } else {
            PNLite.getUserDataManager().denyConsent();
        }
    }

}
