package com.yumi.android.sdk.ads.adapter.inneractive;

import com.fyber.inneractive.sdk.external.InneractiveErrorCode;
import com.yumi.android.sdk.ads.publish.AdError;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;

import static android.text.TextUtils.isEmpty;

/**
 * Description:
 * <p>
 * Created by lgd on 2019/1/23.
 */
public class InneractiveUtil {

    public static AdError recodeError(InneractiveErrorCode errorCode) {
        AdError result;
        if (errorCode == null) {
            result = new AdError(LayerErrorCode.ERROR_INTERNAL);
            result.setErrorMessage("Inneractive errorMsg: null");
            return result;
        }
        result = new AdError(LayerErrorCode.ERROR_NO_FILL);
        result.setErrorMessage("Inneractive errorMsg: " + errorCode.toString());
        return result;
    }

    public static AdError recodeError(LayerErrorCode errorCode) {
        return recodeError(errorCode, null);
    }

    public static AdError recodeError(LayerErrorCode errorCode, String yumiLog) {
        String extraMsg = "Inneractive errorMsg: ";
        if (!isEmpty(yumiLog)) {
            extraMsg += yumiLog;
        }
        AdError result = new AdError(errorCode);
        result.setErrorMessage(extraMsg);
        return result;
    }

}
