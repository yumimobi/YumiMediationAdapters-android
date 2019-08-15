package com.yumi.android.sdk.ads.adapter.pubnative;

import android.app.Activity;

import com.yumi.android.sdk.ads.publish.AdError;
import com.yumi.android.sdk.ads.publish.YumiDebug;
import com.yumi.android.sdk.ads.publish.YumiSettings;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.publish.enumbean.YumiGDPRStatus;

import net.pubnative.lite.sdk.PNLite;

public class PubNativeUtil {

    private static boolean isPubNativeNotInited = true;

    static AdError recodeError(String errMsg) {
        LayerErrorCode errCode = LayerErrorCode.ERROR_NO_FILL;
        AdError result = new AdError(errCode);
        result.setErrorMessage("Pubnative errorMsg: " + errMsg);
        return result;
    }

    static void initPubNativeSDK(String appToken, Activity activity) {
        if (isPubNativeNotInited) {
            PNLite.initialize(appToken, activity.getApplication());
            PNLite.setTestMode(YumiDebug.isDebugMode());
        }
        isPubNativeNotInited = false;
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
