package com.yumi.android.sdk.ads.adapter;

import com.qq.e.comm.util.AdError;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;

import static android.text.TextUtils.isEmpty;

/**
 * Created by hjl on 2017/10/26.
 */

public class GdtUtil {
    public static LayerErrorCode recodeError(AdError gdtError) {
        return recodeError(gdtError, null);
    }

    public static LayerErrorCode recodeError(AdError gdtError, String yumiLog) {
        LayerErrorCode result;
        if (gdtError == null) {
            result = LayerErrorCode.ERROR_INTERNAL;
            result.setExtraMsg("GDT errorMsg: null");
            return result;
        }

        if (gdtError.getErrorCode() == 4003) {
            result = LayerErrorCode.ERROR_INVALID;
        } else if (gdtError.getErrorCode() == 5004) {
            result = LayerErrorCode.ERROR_NO_FILL;
        } else {
            result = LayerErrorCode.ERROR_INTERNAL;
        }
        String extraMsg = "GDT errorMsg: " + gdtError.getErrorMsg();
        if (!isEmpty(yumiLog)) {
            extraMsg += ", " + extraMsg;
        }
        result.setExtraMsg(extraMsg);
        return result;
    }
}
