package com.yumi.android.sdk.ads.adapter.applovin;

import android.content.Context;
import android.text.TextUtils;

import com.applovin.sdk.AppLovinErrorCodes;
import com.applovin.sdk.AppLovinPrivacySettings;
import com.yumi.android.sdk.ads.publish.AdError;
import com.yumi.android.sdk.ads.publish.YumiSettings;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;

/**
 * Description:
 * <p>
 * Created by lgd on 2019/1/23.
 */
class ApplovinUtil {

    static AdError recodeError(int applovinErrorCode) {
        return recodeError(applovinErrorCode, null);
    }

    static AdError recodeError(int applovinErrorCode, String yumiLog) {
        LayerErrorCode error;
        if (applovinErrorCode == AppLovinErrorCodes.NO_FILL) {
            error = LayerErrorCode.ERROR_NO_FILL;
        } else {
            error = LayerErrorCode.ERROR_INTERNAL;
        }

        AdError result = new AdError(error);

        String extraMsg = "Applovin errorCode: " + applovinErrorCode;
        if (!TextUtils.isEmpty(yumiLog)) {
            extraMsg += ", " + yumiLog;
        }
        result.setErrorMessage(extraMsg);
        return result;
    }

    static void updateGDPRStatus(Context context){
        Boolean isConsent = YumiSettings.isGDPRConsent(context);
        if(isConsent == null){
            return;
        }
        // https://dash.applovin.com/docs/integration#androidPrivacySettings
        AppLovinPrivacySettings.setHasUserConsent(isConsent, context);
    }
}
