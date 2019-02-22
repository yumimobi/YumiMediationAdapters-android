package com.yumi.android.sdk.ads.adapter.applovin;

import android.text.TextUtils;

import com.applovin.sdk.AppLovinErrorCodes;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;

/**
 * Description:
 * <p>
 * Created by lgd on 2019/1/23.
 */
class ApplovinUtil {

    static LayerErrorCode recodeError(int applovinErrorCode) {
        return recodeError(applovinErrorCode, null);
    }

    static LayerErrorCode recodeError(int applovinErrorCode, String yumiLog) {
        LayerErrorCode error;
        if (applovinErrorCode == AppLovinErrorCodes.NO_FILL) {
            error = LayerErrorCode.ERROR_NO_FILL;
        } else {
            error = LayerErrorCode.ERROR_INTERNAL;
        }

        String extraMsg = "Applovin errorCode: " + applovinErrorCode;
        if (!TextUtils.isEmpty(yumiLog)) {
            extraMsg += ", " + yumiLog;
        }
        error.setExtraMsg(extraMsg);
        return error;
    }
}
