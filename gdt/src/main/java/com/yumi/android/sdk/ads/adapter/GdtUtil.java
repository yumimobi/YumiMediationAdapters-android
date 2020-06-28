package com.yumi.android.sdk.ads.adapter;

import com.yumi.android.sdk.ads.publish.AdError;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;

import static android.text.TextUtils.isEmpty;
import static com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode.ERROR_NON_RESPONSE;

/**
 * Created by hjl on 2017/10/26.
 */

public class GdtUtil {
    public static AdError recodeError(com.qq.e.comm.util.AdError gdtError) {
        return recodeError(gdtError, null);
    }

    public static AdError recodeError(com.qq.e.comm.util.AdError gdtError, String yumiLog) {
        AdError result;
        if (gdtError == null) {
            result = new AdError(LayerErrorCode.ERROR_INTERNAL);
            result.setErrorMessage("GDT errorMsg: null");
            return result;
        }

        if (gdtError.getErrorCode() == 4003) {
            result = new AdError(LayerErrorCode.ERROR_INVALID);
        } else if (gdtError.getErrorCode() == 4011) {
            result = new AdError(ERROR_NON_RESPONSE);
        } else if (gdtError.getErrorCode() == 5004) {
            result = new AdError(LayerErrorCode.ERROR_NO_FILL);
        } else {
            result = new AdError(LayerErrorCode.ERROR_INTERNAL);
        }
        String extraMsg = "GDT errorMsg: " + gdtError.getErrorMsg();
        if (!isEmpty(yumiLog)) {
            extraMsg += ", " + extraMsg;
        }
        result.setErrorMessage(extraMsg);
        return result;
    }

    public static AdError recodeFiledToShowError() {
        AdError result;
        result = new AdError(LayerErrorCode.ERROR_FAILED_TO_SHOW);
        result.setErrorMessage("GDT failed to show : material expired");
        return result;
    }

    public static String sdkVersion() {
        return "4.232.1102";
    }
}
