package com.yumi.android.sdk.ads.adapter.facebook;

import android.text.TextUtils;

import com.yumi.android.sdk.ads.publish.AdError;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;

import static com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode.ERROR_NETWORK_ERROR;

/**
 * Created by hjl on 2018/8/7.
 */
public class FacebookUtil {

    public static AdError recodeError(com.facebook.ads.AdError adError) {
        return recodeError(adError, null);
    }


    public static AdError recodeError(com.facebook.ads.AdError adError, String yumiLog) {
        AdError result;
        if (adError == null) {
            result = new AdError(LayerErrorCode.ERROR_INTERNAL);
            result.setErrorMessage("Facebook errorMsg: " + yumiLog);
            return result;
        }

        switch (adError.getErrorCode()) {
            case com.facebook.ads.AdError.NETWORK_ERROR_CODE:
                result = new AdError(ERROR_NETWORK_ERROR);
                break;
            case com.facebook.ads.AdError.NO_FILL_ERROR_CODE:
                result = new AdError(LayerErrorCode.ERROR_NO_FILL);
                break;
            default:
                result = new AdError(LayerErrorCode.ERROR_INTERNAL);
                break;
        }
        String extraMsg = "Facebook errorCode: " + adError.getErrorCode() + " and errorMsg: " + adError.getErrorMessage();
        if (!TextUtils.isEmpty(yumiLog)) {
            extraMsg += ", " + yumiLog;
        }
        result.setErrorMessage(extraMsg);
        return result;
    }

    public static String sdkVersion() {
        return "5.3.1";
    }
}
